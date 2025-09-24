package com.exe201.group1.psgp_be.models;

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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "suppliers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 100)
    String name;

    @Column(name = "contact_person", length = 100)
    String contactPerson;

    @Column(name = "phone", length = 10)
    String phone;

    @Column(name = "email", length = 100)
    String email;

    @Column(name = "address", length = 500)
    String address;

    @Column(name = "description", length = 500)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
