package com.exe201.group1.psgp_be.repositories;

import com.exe201.group1.psgp_be.enums.StockMovementType;
import com.exe201.group1.psgp_be.models.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepo extends JpaRepository<StockMovement, Integer> {
    
    // Tìm theo loại item và ID
    List<StockMovement> findBySucculentIdOrderByCreatedAtDesc(Integer succulentId);
    List<StockMovement> findByAccessoryIdOrderByCreatedAtDesc(Integer accessoryId);
    
    // Tìm theo loại movement
    List<StockMovement> findByMovementTypeOrderByCreatedAtDesc(StockMovementType movementType);
    
    // Tìm theo khoảng thời gian
    List<StockMovement> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    
    // Tìm theo succulent và khoảng thời gian
    List<StockMovement> findBySucculentIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Integer succulentId, LocalDateTime start, LocalDateTime end);
    
    // Tìm theo accessory và khoảng thời gian
    List<StockMovement> findByAccessoryIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Integer accessoryId, LocalDateTime start, LocalDateTime end);
    
    // Tìm theo supplier
    List<StockMovement> findBySupplierIdOrderByCreatedAtDesc(Integer supplierId);
    
    // Tìm theo reference code
    List<StockMovement> findByReferenceCodeOrderByCreatedAtDesc(String referenceCode);
    
    // Tính tổng quantity change theo succulent
    @Query("SELECT COALESCE(SUM(sm.quantityChange), 0) FROM StockMovement sm WHERE sm.succulent.id = :succulentId")
    Integer getTotalQuantityChangeBySucculentId(@Param("succulentId") Integer succulentId);
    
    // Tính tổng quantity change theo accessory
    @Query("SELECT COALESCE(SUM(sm.quantityChange), 0) FROM StockMovement sm WHERE sm.accessory.id = :accessoryId")
    Integer getTotalQuantityChangeByAccessoryId(@Param("accessoryId") Integer accessoryId);
    
    // Báo cáo tổng hợp theo movement type
    @Query("SELECT sm.movementType, COUNT(sm), SUM(ABS(sm.quantityChange)) " +
           "FROM StockMovement sm " +
           "WHERE sm.createdAt BETWEEN :start AND :end " +
           "GROUP BY sm.movementType")
    List<Object[]> getMovementSummaryByType(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Tìm các movement gần đây nhất
    @Query("SELECT sm FROM StockMovement sm ORDER BY sm.createdAt DESC")
    List<StockMovement> findRecentMovements();
    
    // Tìm theo order ID (khi có order)
    @Query("SELECT sm FROM StockMovement sm WHERE sm.referenceCode LIKE %:orderId%")
    List<StockMovement> findByOrderId(@Param("orderId") String orderId);
}