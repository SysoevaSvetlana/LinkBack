package com.example.Link.service;


import com.example.Link.dto.LinkRequest;
import com.example.Link.dto.LinkResponse;
import com.example.Link.model.Link;
import com.example.Link.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepo;


    @Transactional
    public LinkResponse create(String email, LinkRequest dto) {
        String code = generateUniqueCode(8);

        Link link = Link.builder()
                .originalUrl(dto.originalUrl())
                .shortCode(code)
                .ownerEmail(email)
                .build();

        return toDto(linkRepo.save(link));
    }


    @Transactional(readOnly = true)
    public LinkResponse findById(Long id, String email) {
        Link l = linkRepo.findById(id)
                .filter(link -> link.getOwnerEmail().equals(email))
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        return toDto(l);
    }


    @Transactional
    public LinkResponse update(Long id, String email, LinkRequest dto) {
        Link link = linkRepo.findById(id)
                .filter(l -> l.getOwnerEmail().equals(email))
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        link.setOriginalUrl(dto.originalUrl());
        return toDto(linkRepo.save(link));
    }


    @Transactional
    public void softDelete(Long id, String email) {
        Link link = linkRepo.findById(id)
                .filter(l -> l.getOwnerEmail().equals(email))
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        linkRepo.delete(link);     // @SQLDelete
    }


    @Transactional(readOnly = true)
    public Page<LinkResponse> findMyLinks(String email,
                                          OffsetDateTime from,
                                          OffsetDateTime to,
                                          Pageable pageable) {
        return linkRepo.findMyLinks(email, from, to, pageable)
                .map(this::toDto);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<LinkResponse> findAllLinksAdmin(String userEmail,
                                                OffsetDateTime from,
                                                OffsetDateTime to,
                                                Pageable pageable) {
        return linkRepo.searchAllAdmin(userEmail, from, to, pageable)
                .map(this::toDto);
    }

    private String generateUniqueCode(int length) {
        return null;
        //TO DO
    }

    private LinkResponse toDto(Link l) {
        return new LinkResponse(
                l.getId(), l.getOriginalUrl(), l.getShortCode(),
                l.getCreatedAt(), l.getUpdatedAt()
        );
    }


}

