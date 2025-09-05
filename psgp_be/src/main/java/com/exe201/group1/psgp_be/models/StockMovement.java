package com.exe201.group1.psgp_be.models;

import com.exe201.group1.psgp_be.enums.StockMovementType;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "stock_movements")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type")
    StockMovementType movementType;

    @Column(name = "item_type", length = 20) // SUCCULENT hoặc ACCESSORY
    String itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "succulent_id")
    Succulent succulent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accessory_id")
    Accessory accessory;

    @Column(name = "quantity_change")
    int quantityChange;

    @Column(name = "unit_cost")
    BigDecimal unitCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    Supplier supplier;

    @Column(name = "reference_code", length = 100)
    String referenceCode; // Mã hóa đơn, mã đơn hàng...

    @Column(name = "note", length = 500)
    String note;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    String createdBy;
}
