package com.exe201.group1.psgp_be.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "`custom_product`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_request_id")
    CustomProductRequest customProductRequest;

    @Column(name = "final_price", precision = 10, scale = 2)
    BigDecimal finalPrice;

    @Column(name = "image_url", length = 500)
    String imageUrl;

    @Column(columnDefinition = "TEXT")
    String notes;

    @Column(name = "is_public_template")
    Boolean isPublicTemplate;

    @Column(length = 20)
    String status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
} 