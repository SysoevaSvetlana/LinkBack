package com.example.Link.controller;

import com.example.Link.dto.LinkRequest;
import com.example.Link.dto.LinkResponse;
import com.example.Link.service.LinkService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/links") @RequiredArgsConstructor
public class LinkController {

    private final LinkService service;

    @PostMapping
    public LinkResponse create(@AuthenticationPrincipal Jwt jwt,
                               @Valid @RequestBody LinkRequest body) {
        return service.create(jwt, body);
    }

    @GetMapping("/{id}")
    public LinkResponse getOne(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long id) {
        return service.findById(id, jwt.getClaim("email"));
    }

    @PutMapping("/{id}")
    public LinkResponse update(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long id,
                               @Valid @RequestBody LinkRequest body) {
        return service.update(id, jwt.getClaim("email"), body);
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable Long id) {
        service.softDelete(id, jwt.getClaim("email"));
    }

    @GetMapping
    public Page<LinkResponse> myLinks(@AuthenticationPrincipal Jwt jwt,
                                      @RequestParam(required=false) String search,
                                      @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                      @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
                                      Pageable pageable) {
        return service.findMy(jwt.getClaim("email"), search, from, to, pageable);
    }

    /* ---------- admin ---------- */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<LinkResponse> allLinks(@RequestParam(required=false) String userEmail,
                                       @RequestParam(required=false) String search,
                                       @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
                                       @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
                                       Pageable pageable) {
        return service.findAllAdmin(userEmail, search, from, to, pageable);
    }

    @PatchMapping("/{id}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public LinkResponse adminUpdate(@PathVariable Long id,
                                    @Valid @RequestBody LinkRequest body) {
        LinkResponse resp = service.update(id, "__ADMIN__", body); // ownerEmail не проверяется
        return resp;
    }

    /* stats */
//    @GetMapping("/{id}/stats")
//    public Map<String,Object> stats(@AuthenticationPrincipal Jwt jwt,
//                                    @PathVariable Long id) {
//        LinkResponse l = service.findById(id, jwt.getClaim("email"));
//        boolean isAdmin = jwt.getClaim("realm_access", Map.class)
//                .getOrDefault("roles", List.of()).toString().contains("ADMIN");
//        if (!isAdmin && !jwt.getClaim("email").equals(l.ownerEmail()))
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//        return Map.of("clicks", l.clicks(), "createdAt", l.createdAt());
//    }
}

