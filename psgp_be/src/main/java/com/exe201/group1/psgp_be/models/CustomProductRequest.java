package com.exe201.group1.psgp_be.models;

import com.exe201.group1.psgp_be.enums.Occasion;
import com.exe201.group1.psgp_be.enums.RequestStatus;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "`custom_product_request`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomProductRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    User buyer;

    @Column(length = 200)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "image_url", length = 500)
    String imageUrl;

    @Column(name = "preferred_colors", length = 200)
    String preferredColors;

    @Column(length = 50)
    String size;

    @Column(name = "budget_range", length = 100)
    String budgetRange;

    @Column(name = "note_to_seller", columnDefinition = "TEXT")
    String noteToSeller;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    RequestStatus status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    Occasion occasion;

    @OneToMany(mappedBy = "customProductRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<CustomProductRequestSucculent> customProductRequestSucculents;

    @OneToMany(mappedBy = "customProductRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<CustomProductRequestAccessory> customProductRequestAccessories;

    @OneToMany(mappedBy = "customProductRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<CustomProduct> customProducts;
} 