package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CurrentStockRequest;
import com.exe201.group1.psgp_be.dto.requests.StockAdjustmentRequest;
import com.exe201.group1.psgp_be.dto.requests.StockHistoryDateRangeRequest;
import com.exe201.group1.psgp_be.dto.requests.StockHistoryRequest;
import com.exe201.group1.psgp_be.dto.requests.StockMovementReportRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    //=================== Tồn kho hiện tại =====================\\
    @PostMapping("/stock/current")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getCurrentStock(@RequestBody CurrentStockRequest request, HttpServletRequest httpRequest) {
        return inventoryService.getCurrentStock(request, httpRequest);
    }

    @GetMapping("/stock/current/all")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getAllCurrentStock(HttpServletRequest httpRequest) {
        return inventoryService.getAllCurrentStock(httpRequest);
    }

    //=================== Lịch sử thay đổi tồn kho =====================\\
    @PostMapping("/stock/history")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getStockHistory(@RequestBody StockHistoryRequest request, HttpServletRequest httpRequest) {
        return inventoryService.getStockHistory(request, httpRequest);
    }

    @PostMapping("/stock/history/date/range")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getStockHistoryByDateRange(@RequestBody StockHistoryDateRangeRequest request, HttpServletRequest httpRequest) {
        return inventoryService.getStockHistoryByDateRange(request, httpRequest);
    }

    //=================== Cảnh báo hết hàng =====================\\
    @GetMapping("/stock/alerts/low/stock")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getLowStockAlerts(HttpServletRequest httpRequest) {
        return inventoryService.getLowStockAlerts(httpRequest);
    }

    @GetMapping("/stock/alerts/critical")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getCriticalStockAlerts(HttpServletRequest httpRequest) {
        return inventoryService.getCriticalStockAlerts(httpRequest);
    }

    //=================== Điều chỉnh tồn kho =====================\\
    @PostMapping("/adjust/up")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> adjustStockUp(@RequestBody StockAdjustmentRequest request, HttpServletRequest httpRequest) {
        return inventoryService.adjustStockUp(request, httpRequest);
    }

    @PostMapping("/adjust/down")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> adjustStockDown(@RequestBody StockAdjustmentRequest request, HttpServletRequest httpRequest) {
        return inventoryService.adjustStockDown(request, httpRequest);
    }

    //=================== Báo cáo tồn kho =====================\\
    @GetMapping("/reports/inventory")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getInventoryReport(HttpServletRequest httpRequest) {
        return inventoryService.getInventoryReport(httpRequest);
    }

    @PostMapping("/reports/movements")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getStockMovementReport(@RequestBody StockMovementReportRequest request, HttpServletRequest httpRequest) {
        return inventoryService.getStockMovementReport(request, httpRequest);
    }

    @GetMapping("/reports/performance")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getProductPerformanceReport(HttpServletRequest httpRequest) {
        return inventoryService.getProductPerformanceReport(httpRequest);
    }

    //=================== Thống kê =====================\\
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getInventoryStatistics(HttpServletRequest httpRequest) {
        return inventoryService.getInventoryStatistics(httpRequest);
    }

    @GetMapping("/statistics/top/moving")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getTopMovingItems(HttpServletRequest httpRequest) {
        return inventoryService.getTopMovingItems(httpRequest);
    }

    @GetMapping("/statistics/slow/moving")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getSlowMovingItems(HttpServletRequest httpRequest) {
        return inventoryService.getSlowMovingItems(httpRequest);
    }
}
