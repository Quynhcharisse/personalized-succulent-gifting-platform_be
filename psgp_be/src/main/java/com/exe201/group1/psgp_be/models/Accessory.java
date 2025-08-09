package com.exe201.group1.psgp_be.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    String type;

    @Column(length = 100)
    String material;

    @Column(length = 50)
    String color;

    @Column(length = 100)
    String dimension;

    @Column
    Integer quantity;

    @OneToMany(mappedBy = "accessory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<ProductAccessory> productAccessories;

    @OneToMany(mappedBy = "accessory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<CustomProductRequestAccessory> customProductRequestAccessories;
} 