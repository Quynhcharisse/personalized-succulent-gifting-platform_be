package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CurrentStockRequest;
import com.exe201.group1.psgp_be.dto.requests.StockAdjustmentRequest;
import com.exe201.group1.psgp_be.dto.requests.StockHistoryDateRangeRequest;
import com.exe201.group1.psgp_be.dto.requests.StockHistoryRequest;
import com.exe201.group1.psgp_be.dto.requests.StockMovementReportRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;


public interface InventoryService {
    
    // =========================== Tồn kho hiện tại ========================== \\
    ResponseEntity<ResponseObject> getCurrentStock(CurrentStockRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getAllCurrentStock(HttpServletRequest httpRequest);
    
    // =========================== Lịch sử thay đổi tồn kho ========================== \\
    ResponseEntity<ResponseObject> getStockHistory(StockHistoryRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getStockHistoryByDateRange(StockHistoryDateRangeRequest request, HttpServletRequest httpRequest);
    
    // =========================== Cảnh báo hết hàng ========================== \\
    ResponseEntity<ResponseObject> getLowStockAlerts(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getCriticalStockAlerts(HttpServletRequest httpRequest);
    
    // =========================== Điều chỉnh tồn kho ========================== \\
    ResponseEntity<ResponseObject> adjustStockUp(StockAdjustmentRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> adjustStockDown(StockAdjustmentRequest request, HttpServletRequest httpRequest);
    
    // =========================== Báo cáo tồn kho ========================== \\
    ResponseEntity<ResponseObject> getInventoryReport(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getStockMovementReport(StockMovementReportRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getProductPerformanceReport(HttpServletRequest httpRequest);
    
    // =========================== Thống kê ========================== \\
    ResponseEntity<ResponseObject> getInventoryStatistics(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getTopMovingItems(HttpServletRequest httpRequest);
    
    ResponseEntity<ResponseObject> getSlowMovingItems(HttpServletRequest httpRequest);
}
