package com.example.Link.repository;


import com.example.Link.model.Link;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.*;

public interface LinkRepository extends JpaRepository<Link, Long> {

    /* поиск по коду */
    Optional<Link> findByShortCode(String code);

    /* проверка уникальности для генератора */
    boolean existsByShortCode(String code);

    /* лимит 1000/сутки */
    @Query("""
        select count(l) from Link l
        where l.ownerEmail = :email
          and l.createdAt >= :since
    """)
    long countCreatedToday(@Param("email") String email,
                           @Param("since") OffsetDateTime since);

    /* инкремент кликов */
    @Modifying
    @Query("update Link l set l.clicks = l.clicks + 1 where l.id = :id")
    void incrementClicks(@Param("id") Long id);

    /* список «моих» с поиском и датами */
    @Query("""
        select l from Link l
        where l.ownerEmail = :email
          and (:search is null or :search = ''
               or lower(l.originalUrl) like lower(concat('%',:search,'%'))
               or lower(l.shortCode)   like lower(concat('%',:search,'%')))
          and (:from is null or l.createdAt >= :from)
          and (:to   is null or l.createdAt <= :to)
    """)
    Page<Link> findMyLinks(@Param("email") String email,
                           @Param("search") String search,
                           @Param("from") OffsetDateTime from,
                           @Param("to") OffsetDateTime to,
                           Pageable pageable);

    /* список для администратора */
    @Query("""
        select l from Link l
        where (:userEmail is null or :userEmail = '' or l.ownerEmail = :userEmail)
          and (:search is null or :search = ''
               or lower(l.originalUrl) like lower(concat('%',:search,'%'))
               or lower(l.shortCode)   like lower(concat('%',:search,'%')))
          and (:from is null or l.createdAt >= :from)
          and (:to   is null or l.createdAt <= :to)
    """)
    Page<Link> searchAllAdmin(@Param("userEmail") String userEmail,
                              @Param("search")    String search,
                              @Param("from")      OffsetDateTime from,
                              @Param("to")        OffsetDateTime to,
                              Pageable pageable);
}


