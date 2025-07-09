package com.example.Link.controller;

import com.example.Link.model.Link;
import com.example.Link.repository.LinkRepository;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final LinkRepository repo;

    @GetMapping("/r/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        Link link = repo.findByShortCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        repo.incrementClicks(link.getId());      // custom @Modifying query

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(link.getOriginalUrl()))
                .build();
    }


}

