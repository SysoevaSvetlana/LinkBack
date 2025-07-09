package com.example.Link.service;


import com.example.Link.dto.LinkRequest;
import com.example.Link.dto.LinkResponse;
import com.example.Link.exception.TooManyRequestsException;
import com.example.Link.model.Link;
import com.example.Link.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class LinkService {

    private final LinkRepository repo;
    private static final String ABC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final SecureRandom rnd = new SecureRandom();


    @Transactional
    public LinkResponse create(Jwt jwt, LinkRequest dto) {
        String email = jwt.getClaim("email");
        OffsetDateTime today = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
        if (repo.countCreatedToday(email, today) >= 1_000)
            throw new TooManyRequestsException("Daily limit 1000 links");

        Link link = Link.builder()
                .originalUrl(dto.originalUrl())
                .shortCode(generateUniqueCode(8))
                .ownerEmail(email)
                .build();
        return toDto(repo.save(link));
    }


    @Transactional(readOnly = true)
    public LinkResponse findById(Long id, String email) {
        return repo.findById(id)
                .filter(l -> l.getOwnerEmail().equals(email))
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public LinkResponse update(Long id, String email, LinkRequest dto) {
        Link link = repo.findById(id)
                .filter(l -> l.getOwnerEmail().equals(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        link.setOriginalUrl(dto.originalUrl());
        return toDto(repo.save(link));
    }

    @Transactional
    public void softDelete(Long id, String email) {
        Link link = repo.findById(id)
                .filter(l -> l.getOwnerEmail().equals(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repo.delete(link);                    // @SQLDelete
    }


    @Transactional(readOnly = true)
    public Page<LinkResponse> findMy(String email, String search,
                                     OffsetDateTime from, OffsetDateTime to,
                                     Pageable p) {
        return repo.findMyLinks(email, search, from, to, p).map(this::toDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<LinkResponse> findAllAdmin(String userEmail, String search,
                                           OffsetDateTime from, OffsetDateTime to,
                                           Pageable p) {
        return repo.searchAllAdmin(userEmail, search, from, to, p).map(this::toDto);
    }


    private String generateUniqueCode(int len) {
        String code;
        do {
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++)
                sb.append(ABC.charAt(rnd.nextInt(ABC.length())));
            code = sb.toString();
        } while (repo.existsByShortCode(code));
        return code;
    }

    private LinkResponse toDto(Link l) {
        return new LinkResponse(l.getId(), l.getOriginalUrl(), l.getShortCode(),
                l.getCreatedAt(), l.getUpdatedAt());
    }
}


