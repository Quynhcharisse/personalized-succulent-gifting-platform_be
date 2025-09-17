package com.exe201.group1.psgp_be.models;

import com.exe201.group1.psgp_be.enums.Size;
import com.exe201.group1.psgp_be.enums.Status;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "`succulent`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Succulent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    SucculentSpecies species;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    Size size;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    Status status;

    int quantity;

    @Column(precision = 10, scale = 2)
    BigDecimal priceSell;

    String imageUrl;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "succulent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<ProductSucculent> productSucculentList;

    @OneToMany(mappedBy = "succulent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<CustomProductRequestSucculent> customProductRequestSucculentList;
}
