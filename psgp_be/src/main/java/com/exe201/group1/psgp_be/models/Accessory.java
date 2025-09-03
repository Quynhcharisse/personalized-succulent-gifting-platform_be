package com.exe201.group1.psgp_be.models;

import com.exe201.group1.psgp_be.enums.AccessoryCategory;
import com.exe201.group1.psgp_be.enums.Status;
import jakarta.persistence.*;
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
@Table(name = "`accessory`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Accessory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 100)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    AccessoryCategory category;

    @Column(name = "description", length = 300)
    String description;

    @Column
    Integer quantity;

    @Column(precision = 10, scale = 2)
    BigDecimal priceBuy;

    @Column(precision = 10, scale = 2)
    BigDecimal priceSell;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    Status status;

    @OneToMany(mappedBy = "accessory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<ProductAccessory> productAccessories;

    @OneToMany(mappedBy = "accessory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<CustomProductRequestAccessory> customProductRequestAccessories;
} 