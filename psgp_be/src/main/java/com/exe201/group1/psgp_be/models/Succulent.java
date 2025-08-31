package com.exe201.group1.psgp_be.models;

import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Size;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.enums.Zodiac;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "`succulent`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Succulent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "species_name", length = 100)
    String speciesName;

    @Column(name = "description", length = 300)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    Size size;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    Status status;

    @Column(precision = 10)
    int quantity;

    @Column(precision = 10, scale = 2)
    BigDecimal priceBuy;

    @Column(precision = 10, scale = 2)
    BigDecimal priceSell;

    @Enumerated(EnumType.STRING)
    @Column(name = "feng_shui", length = 10)
    FengShui fengShui;

    @Enumerated(EnumType.STRING)
    @Column(name = "zodiac", length = 15)
    Zodiac zodiac;

    @OneToMany(mappedBy = "succulent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<ProductSucculent> productSucculents;

    @OneToMany(mappedBy = "succulent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<CustomProductRequestSucculent> customProductRequestSucculents;
} 