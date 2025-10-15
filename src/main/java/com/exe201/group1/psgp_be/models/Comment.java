package com.exe201.group1.psgp_be.models;

import com.exe201.group1.psgp_be.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "`comment`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    Post post;

    @Column(columnDefinition = "TEXT")
    String content;

    @Column
    int rating;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    Status status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
} 