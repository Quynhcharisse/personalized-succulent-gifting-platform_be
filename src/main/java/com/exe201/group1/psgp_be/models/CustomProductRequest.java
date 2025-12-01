package com.exe201.group1.psgp_be.models;

import com.exe201.group1.psgp_be.enums.Status;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "`custom_product_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomProductRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    User buyer;

    @Column(columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    Object data;

    @Column(columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    Object designImage;
    // version: list of images, creation date, status (pending / approve / reject), revision content, revision date
    // type (design / re-design), parentVersion

    @Column(name = "reject_reason")
    String reason;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    Status status;

    @Column(name = "occasion", length = 100)
    String occasion;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @OneToOne(mappedBy = "customProductRequest",fetch = FetchType.EAGER)
    OrderDetail orderDetail;


    @OneToOne(mappedBy = "customProductRequest",fetch = FetchType.EAGER)
    Transaction transaction;
} 