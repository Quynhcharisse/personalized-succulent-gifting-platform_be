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
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.engine.internal.Cascade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "`discount_program`")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    String name; // Tên chương trình giảm giá

    @Column(length = 255)
    String description; // Mô tả chi tiết

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal minimumOrderValue;

    @Column(nullable = false, precision = 15, scale = 2)
    BigDecimal discountValue; // Giá trị giảm (theo phần trăm hoặc tiền)

    @Column(nullable = false)
    Boolean isPercentage; // true = giảm theo %, false = giảm theo số tiền

    @Column(nullable = false)
    Boolean active; // Trạng thái chương trình

    @Column(nullable = false)
    Integer usedCount = 0; // Số lần đã sử dụng

    @Column(nullable = false)
    Integer usageLimit; // ✅ tổng số lần dùng tối đa

    @Column(nullable = false)
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "discountProgram", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<DiscountCode> discountCodes;

}
