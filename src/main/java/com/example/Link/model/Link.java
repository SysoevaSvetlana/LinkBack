package com.example.Link.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "links")
@SQLDelete(sql = "UPDATE links SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 15)
    private String shortCode;

    @Column(nullable = false, length = 320)
    private String ownerEmail;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false)
    private long clicks = 0;


    @PrePersist
    void prePersist() {
        createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Link() {
    }

    public Link(Long id, String originalUrl, String shortCode,
                String ownerEmail, OffsetDateTime createdAt,
                OffsetDateTime updatedAt, boolean isDeleted) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.ownerEmail = ownerEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Link link = new Link();

        public Builder id(Long id) {
            link.id = id;
            return this;
        }

        public Builder originalUrl(String u) {
            link.originalUrl = u;
            return this;
        }

        public Builder shortCode(String c) {
            link.shortCode = c;
            return this;
        }

        public Builder ownerEmail(String e) {
            link.ownerEmail = e;
            return this;
        }

        public Builder createdAt(OffsetDateTime t) {
            link.createdAt = t;
            return this;
        }

        public Builder updatedAt(OffsetDateTime t) {
            link.updatedAt = t;
            return this;
        }

        public Builder deleted(boolean d) {
            link.isDeleted = d;
            return this;
        }

        public Link build() {
            return link;
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    /* ---------- equals / hashCode / toString ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;
        Link link = (Link) o;
        return Objects.equals(id, link.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Link{" +
               "id=" + id +
               ", shortCode='" + shortCode + '\'' +
               ", ownerEmail='" + ownerEmail + '\'' +
               '}';
    }

}

