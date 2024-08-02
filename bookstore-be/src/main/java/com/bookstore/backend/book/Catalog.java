package com.bookstore.backend.book;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("catalogs")
@Getter
@Setter
public class Catalog {

    @Id
    private Long id;
    private String name;
    private Long parentId;
    @CreatedDate
    private Instant createdAt;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Instant lastModifiedAt;
    @LastModifiedBy
    private String lastModifiedBy;
    @Version
    private int version;
}
