package com.exe201.group1.psgp_be.models;

import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Zodiac;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "succulent_species")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SucculentSpecies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "species_name", length = 100)
    String speciesName;

    @Column(name = "description", length = 300)
    String description;

    @ElementCollection(targetClass = FengShui.class)
    @CollectionTable(name = "species_fengshui", joinColumns = @JoinColumn(name = "species_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "element", length = 10)
    Set<FengShui> elements = new HashSet<>();

    @ElementCollection(targetClass = Zodiac.class)
    @CollectionTable(name = "species_zodiac", joinColumns = @JoinColumn(name = "species_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "zodiac", length = 15)
    Set<Zodiac> zodiacs = new HashSet<>();

    @OneToMany(mappedBy = "species", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<Succulent> variantList;
}

