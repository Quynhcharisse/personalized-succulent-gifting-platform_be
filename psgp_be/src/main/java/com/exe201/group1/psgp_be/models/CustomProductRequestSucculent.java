package com.exe201.group1.psgp_be.models;

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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "`custom_product_request_succulent`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomProductRequestSucculent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_product_request_id")
    CustomProductRequest customProductRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "succulent_id")
    Succulent succulent;
} 