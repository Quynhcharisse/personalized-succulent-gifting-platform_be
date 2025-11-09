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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "`order`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    User buyer;

    @Column(name = "order_code")
    long orderCode;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @Column(name = "total_amount", precision = 10, scale = 2)
    BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    BigDecimal discountAmount;

    @Column(name = "final_amount", precision = 10, scale = 2)
    BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    Status status;

    // =========================== THÔNG TIN GIAO HÀNG ========================== \\

    @Column(name = "shipping_fee", precision = 10, scale = 2)
    BigDecimal shippingFee;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<OrderDetail> orderDetailList;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id")
    Transaction transaction;

} 