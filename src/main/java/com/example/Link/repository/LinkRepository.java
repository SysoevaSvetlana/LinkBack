package com.example.Link.repository;


import com.example.Link.model.Link;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.*;

public interface LinkRepository
        extends JpaRepository<Link, Long>, JpaSpecificationExecutor<Link> {

    Optional<Link> findByShortCode(String code);


    @Query("""
        select l from Link l
        where l.ownerEmail = :email
          and (:from is null or l.createdAt >= :from)
          and (:to   is null or l.createdAt <= :to)
    """)
    Page<Link> findMyLinks(@Param("email") String email,
                           @Param("from")  OffsetDateTime from,
                           @Param("to")    OffsetDateTime to,
                           Pageable pageable);

    @Query("""
        select l from Link l
        where (:emailFilter is null or :emailFilter = '' or l.ownerEmail = :emailFilter)
          and (:from is null or l.createdAt >= :from)
          and (:to   is null or l.createdAt <= :to)
    """)
    Page<Link> searchAllAdmin(@Param("emailFilter") String emailFilter,
                              @Param("from")        OffsetDateTime from,
                              @Param("to")          OffsetDateTime to,
                              Pageable pageable);


}

