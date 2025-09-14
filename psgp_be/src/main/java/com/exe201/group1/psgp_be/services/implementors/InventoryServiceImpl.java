package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CurrentStockRequest;
import com.exe201.group1.psgp_be.dto.requests.StockAdjustmentRequest;
import com.exe201.group1.psgp_be.dto.requests.StockHistoryDateRangeRequest;
import com.exe201.group1.psgp_be.dto.requests.StockHistoryRequest;
import com.exe201.group1.psgp_be.dto.requests.StockMovementReportRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Role;
import com.exe201.group1.psgp_be.enums.StockMovementType;
import com.exe201.group1.psgp_be.models.Accessory;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.StockMovement;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.repositories.AccessoryRepo;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.StockMovementRepo;
import com.exe201.group1.psgp_be.repositories.SucculentRepo;
import com.exe201.group1.psgp_be.services.InventoryService;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final StockMovementRepo stockMovementRepo;
    private final SucculentRepo succulentRepo;
    private final AccessoryRepo accessoryRepo;
    private final JWTService jwtService;
    private final AccountRepo accountRepo;

    // =========================== Tồn kho hiện tại ========================== \\
    @Override
    public ResponseEntity<ResponseObject> getCurrentStock(CurrentStockRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem tồn kho", null);
        }

        Integer currentStock = 0;
        String itemName = "";
        Integer itemId = request.getItemId();
        String itemType = request.getItemType();
        
        if ("SUCCULENT".equals(itemType)) {
            Optional<Succulent> succulentOpt = succulentRepo.findById(itemId);
            if (succulentOpt.isPresent()) {
                currentStock = stockMovementRepo.getTotalQuantityChangeBySucculentId(itemId);
                itemName = succulentOpt.get().getSpecies().getSpeciesName() + " - " + succulentOpt.get().getSize().getDisplayName();
            }
        } else if ("ACCESSORY".equals(itemType)) {
            Optional<Accessory> accessoryOpt = accessoryRepo.findById(itemId);
            if (accessoryOpt.isPresent()) {
                currentStock = stockMovementRepo.getTotalQuantityChangeByAccessoryId(itemId);
                itemName = accessoryOpt.get().getName();
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("itemId", itemId);
        data.put("itemType", itemType);
        data.put("itemName", itemName);
        data.put("currentStock", currentStock);
        data.put("lastUpdated", LocalDateTime.now());

        return ResponseBuilder.build(HttpStatus.OK, "Lấy tồn kho hiện tại thành công", data);
    }

    @Override
    public ResponseEntity<ResponseObject> getAllCurrentStock(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem tồn kho", null);
        }

        List<Map<String, Object>> stockList = new ArrayList<>();

        // Lấy tồn kho sen đá
        List<Succulent> succulents = succulentRepo.findAll();
        for (Succulent succulent : succulents) {
            Integer currentStock = stockMovementRepo.getTotalQuantityChangeBySucculentId(succulent.getId());
            Map<String, Object> stockItem = new HashMap<>();
            stockItem.put("itemId", succulent.getId());
            stockItem.put("itemType", "SUCCULENT");
            stockItem.put("itemName", succulent.getSpecies().getSpeciesName() + " - " + succulent.getSize().getDisplayName());
            stockItem.put("currentStock", currentStock);
            stockItem.put("status", succulent.getStatus().getValue());
            stockItem.put("priceSell", succulent.getPriceSell());
            stockList.add(stockItem);
        }

        // Lấy tồn kho phụ kiện
        List<Accessory> accessories = accessoryRepo.findAll();
        for (Accessory accessory : accessories) {
            Integer currentStock = stockMovementRepo.getTotalQuantityChangeByAccessoryId(accessory.getId());
            Map<String, Object> stockItem = new HashMap<>();
            stockItem.put("itemId", accessory.getId());
            stockItem.put("itemType", "ACCESSORY");
            stockItem.put("itemName", accessory.getName());
            stockItem.put("currentStock", currentStock);
            stockItem.put("status", accessory.getStatus().getValue());
            stockItem.put("priceSell", accessory.getPriceSell());
            stockList.add(stockItem);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách tồn kho thành công", stockList);
    }

    // =========================== Lịch sử thay đổi tồn kho ========================== \\
    @Override
    public ResponseEntity<ResponseObject> getStockHistory(StockHistoryRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem lịch sử tồn kho", null);
        }

        Integer itemId = request.getItemId();
        String itemType = request.getItemType();
        
        List<StockMovement> movements = new ArrayList<>();
        if ("SUCCULENT".equals(itemType)) {
            movements = stockMovementRepo.findBySucculentIdOrderByCreatedAtDesc(itemId);
        } else if ("ACCESSORY".equals(itemType)) {
            movements = stockMovementRepo.findByAccessoryIdOrderByCreatedAtDesc(itemId);
        }

        List<Map<String, Object>> historyData = movements.stream()
                .map(this::buildStockMovementResponse)
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "Lấy lịch sử tồn kho thành công", historyData);
    }

    @Override
    public ResponseEntity<ResponseObject> getStockHistoryByDateRange(StockHistoryDateRangeRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem lịch sử tồn kho", null);
        }

        Integer itemId = request.getItemId();
        String itemType = request.getItemType();
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();
        
        List<StockMovement> movements = new ArrayList<>();
        if ("SUCCULENT".equals(itemType)) {
            movements = stockMovementRepo.findBySucculentIdAndCreatedAtBetweenOrderByCreatedAtDesc(itemId, startDate, endDate);
        } else if ("ACCESSORY".equals(itemType)) {
            movements = stockMovementRepo.findByAccessoryIdAndCreatedAtBetweenOrderByCreatedAtDesc(itemId, startDate, endDate);
        }

        List<Map<String, Object>> historyData = movements.stream()
                .map(this::buildStockMovementResponse)
                .toList();

        return ResponseBuilder.build(HttpStatus.OK, "Lấy lịch sử tồn kho theo khoảng thời gian thành công", historyData);
    }

    // =========================== Cảnh báo hết hàng ========================== \\
    @Override
    public ResponseEntity<ResponseObject> getLowStockAlerts(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem cảnh báo tồn kho", null);
        }

        List<Map<String, Object>> alerts = new ArrayList<>();

        // Kiểm tra sen đá
        List<Succulent> succulents = succulentRepo.findAll();
        for (Succulent succulent : succulents) {
            Integer currentStock = stockMovementRepo.getTotalQuantityChangeBySucculentId(succulent.getId());
            if (currentStock <= 10) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("itemId", succulent.getId());
                alert.put("itemType", "SUCCULENT");
                alert.put("itemName", succulent.getSpecies().getSpeciesName() + " - " + succulent.getSize().getDisplayName());
                alert.put("currentStock", currentStock);
                alert.put("alertLevel", getAlertLevel(currentStock));
                alert.put("priceSell", succulent.getPriceSell());
                alerts.add(alert);
            }
        }

        // Kiểm tra phụ kiện
        List<Accessory> accessories = accessoryRepo.findAll();
        for (Accessory accessory : accessories) {
            Integer currentStock = stockMovementRepo.getTotalQuantityChangeByAccessoryId(accessory.getId());
            if (currentStock <= 10) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("itemId", accessory.getId());
                alert.put("itemType", "ACCESSORY");
                alert.put("itemName", accessory.getName());
                alert.put("currentStock", currentStock);
                alert.put("alertLevel", getAlertLevel(currentStock));
                alert.put("priceSell", accessory.getPriceSell());
                alerts.add(alert);
            }
        }

        return ResponseBuilder.build(HttpStatus.OK, "Lấy cảnh báo tồn kho thành công", alerts);
    }

    @Override
    public ResponseEntity<ResponseObject> getCriticalStockAlerts(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem cảnh báo tồn kho", null);
        }

        List<Map<String, Object>> criticalAlerts = new ArrayList<>();

        // Kiểm tra sen đá hết hàng
        List<Succulent> succulents = succulentRepo.findAll();
        for (Succulent succulent : succulents) {
            Integer currentStock = stockMovementRepo.getTotalQuantityChangeBySucculentId(succulent.getId());
            if (currentStock == 0) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("itemId", succulent.getId());
                alert.put("itemType", "SUCCULENT");
                alert.put("itemName", succulent.getSpecies().getSpeciesName() + " - " + succulent.getSize().getDisplayName());
                alert.put("currentStock", currentStock);
                alert.put("alertLevel", "CRITICAL");
                alert.put("priceSell", succulent.getPriceSell());
                criticalAlerts.add(alert);
            }
        }

        // Kiểm tra phụ kiện hết hàng
        List<Accessory> accessories = accessoryRepo.findAll();
        for (Accessory accessory : accessories) {
            Integer currentStock = stockMovementRepo.getTotalQuantityChangeByAccessoryId(accessory.getId());
            if (currentStock == 0) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("itemId", accessory.getId());
                alert.put("itemType", "ACCESSORY");
                alert.put("itemName", accessory.getName());
                alert.put("currentStock", currentStock);
                alert.put("alertLevel", "CRITICAL");
                alert.put("priceSell", accessory.getPriceSell());
                criticalAlerts.add(alert);
            }
        }

        return ResponseBuilder.build(HttpStatus.OK, "Lấy cảnh báo tồn kho nghiêm trọng thành công", criticalAlerts);
    }

    // =========================== Điều chỉnh tồn kho ========================== \\
    @Override
    public ResponseEntity<ResponseObject> adjustStockUp(StockAdjustmentRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền điều chỉnh tồn kho", null);
        }

        Integer itemId = request.getItemId();
        String itemType = request.getItemType();
        Integer quantity = request.getQuantity();
        String reason = request.getReason();

        if (quantity <= 0) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Số lượng điều chỉnh phải lớn hơn 0", null);
        }

        // Tạo StockMovement
        StockMovement movement = StockMovement.builder()
                .movementType(StockMovementType.ADJUSTMENT_IN)
                .itemType(itemType)
                .quantityChange(quantity)
                .note("Điều chỉnh tăng: " + reason)
                .createdBy(account.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        if ("SUCCULENT".equals(itemType)) {
            Optional<Succulent> succulentOpt = succulentRepo.findById(itemId);
            if (succulentOpt.isPresent()) {
                movement.setSucculent(succulentOpt.get());
                // Cập nhật tồn kho
                Succulent succulent = succulentOpt.get();
                succulent.setQuantity(succulent.getQuantity() + quantity);
                succulentRepo.save(succulent);
            }
        } else if ("ACCESSORY".equals(itemType)) {
            Optional<Accessory> accessoryOpt = accessoryRepo.findById(itemId);
            if (accessoryOpt.isPresent()) {
                movement.setAccessory(accessoryOpt.get());
                // Cập nhật tồn kho
                Accessory accessory = accessoryOpt.get();
                accessory.setQuantity(accessory.getQuantity() + quantity);
                accessoryRepo.save(accessory);
            }
        }

        stockMovementRepo.save(movement);

        return ResponseBuilder.build(HttpStatus.OK, "Điều chỉnh tăng tồn kho thành công", null);
    }

    @Override
    public ResponseEntity<ResponseObject> adjustStockDown(StockAdjustmentRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền điều chỉnh tồn kho", null);
        }

        Integer itemId = request.getItemId();
        String itemType = request.getItemType();
        Integer quantity = request.getQuantity();
        String reason = request.getReason();

        if (quantity <= 0) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Số lượng điều chỉnh phải lớn hơn 0", null);
        }

        // Kiểm tra tồn kho hiện tại
        Integer currentStock = 0;
        if ("SUCCULENT".equals(itemType)) {
            currentStock = stockMovementRepo.getTotalQuantityChangeBySucculentId(itemId);
        } else if ("ACCESSORY".equals(itemType)) {
            currentStock = stockMovementRepo.getTotalQuantityChangeByAccessoryId(itemId);
        }

        if (currentStock < quantity) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, 
                "Không thể điều chỉnh giảm. Tồn kho hiện tại: " + currentStock + ", yêu cầu giảm: " + quantity, null);
        }

        // Tạo StockMovement
        StockMovement movement = StockMovement.builder()
                .movementType(StockMovementType.ADJUSTMENT_OUT)
                .itemType(itemType)
                .quantityChange(-quantity)
                .note("Điều chỉnh giảm: " + reason)
                .createdBy(account.getEmail())
                .createdAt(LocalDateTime.now())
                .build();

        if ("SUCCULENT".equals(itemType)) {
            Optional<Succulent> succulentOpt = succulentRepo.findById(itemId);
            if (succulentOpt.isPresent()) {
                movement.setSucculent(succulentOpt.get());
                // Cập nhật tồn kho
                Succulent succulent = succulentOpt.get();
                succulent.setQuantity(succulent.getQuantity() - quantity);
                succulentRepo.save(succulent);
            }
        } else if ("ACCESSORY".equals(itemType)) {
            Optional<Accessory> accessoryOpt = accessoryRepo.findById(itemId);
            if (accessoryOpt.isPresent()) {
                movement.setAccessory(accessoryOpt.get());
                // Cập nhật tồn kho
                Accessory accessory = accessoryOpt.get();
                accessory.setQuantity(accessory.getQuantity() - quantity);
                accessoryRepo.save(accessory);
            }
        }

        stockMovementRepo.save(movement);

        return ResponseBuilder.build(HttpStatus.OK, "Điều chỉnh giảm tồn kho thành công", null);
    }

    // =========================== Báo cáo tồn kho ========================== \\
    @Override
    public ResponseEntity<ResponseObject> getInventoryReport(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem báo cáo tồn kho", null);
        }

        // Lấy tất cả tồn kho hiện tại
        ResponseEntity<ResponseObject> allStockResponse = getAllCurrentStock(httpRequest);
        List<Map<String, Object>> stockList = (List<Map<String, Object>>) allStockResponse.getBody().getData();

        // Tính tổng giá trị tồn kho
        BigDecimal totalValue = BigDecimal.ZERO;
        int totalItems = 0;
        int outOfStockItems = 0;
        int lowStockItems = 0;

        for (Map<String, Object> item : stockList) {
            Integer currentStock = (Integer) item.get("currentStock");
            BigDecimal priceSell = (BigDecimal) item.get("priceSell");
            
            totalValue = totalValue.add(priceSell.multiply(BigDecimal.valueOf(currentStock)));
            totalItems++;
            
            if (currentStock == 0) {
                outOfStockItems++;
            } else if (currentStock <= 10) {
                lowStockItems++;
            }
        }

        Map<String, Object> report = new HashMap<>();
        report.put("totalItems", totalItems);
        report.put("outOfStockItems", outOfStockItems);
        report.put("lowStockItems", lowStockItems);
        report.put("totalValue", totalValue);
        report.put("stockDetails", stockList);
        report.put("generatedAt", LocalDateTime.now());

        return ResponseBuilder.build(HttpStatus.OK, "Lấy báo cáo tồn kho thành công", report);
    }

    @Override
    public ResponseEntity<ResponseObject> getStockMovementReport(StockMovementReportRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem báo cáo tồn kho", null);
        }

        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();
        
        List<StockMovement> movements = stockMovementRepo.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
        List<Object[]> summary = stockMovementRepo.getMovementSummaryByType(startDate, endDate);

        Map<String, Object> report = new HashMap<>();
        report.put("period", startDate + " to " + endDate);
        report.put("totalMovements", movements.size());
        report.put("movementSummary", summary);
        report.put("movementDetails", movements.stream().map(this::buildStockMovementResponse).toList());
        report.put("generatedAt", LocalDateTime.now());

        return ResponseBuilder.build(HttpStatus.OK, "Lấy báo cáo nhập xuất kho thành công", report);
    }

    @Override
    public ResponseEntity<ResponseObject> getProductPerformanceReport(HttpServletRequest httpRequest) {
        // TODO: Implement product performance report
        return ResponseBuilder.build(HttpStatus.OK, "Báo cáo hiệu suất sản phẩm chưa được implement", null);
    }

    // =========================== Thống kê ========================== \\
    @Override
    public ResponseEntity<ResponseObject> getInventoryStatistics(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có SELLER mới có quyền xem thống kê tồn kho", null);
        }

        // Lấy tất cả tồn kho hiện tại
        ResponseEntity<ResponseObject> allStockResponse = getAllCurrentStock(httpRequest);
        List<Map<String, Object>> stockList = (List<Map<String, Object>>) allStockResponse.getBody().getData();

        int totalItems = stockList.size();
        int outOfStockItems = 0;
        int lowStockItems = 0;
        int availableItems = 0;

        for (Map<String, Object> item : stockList) {
            Integer currentStock = (Integer) item.get("currentStock");
            if (currentStock == 0) {
                outOfStockItems++;
            } else if (currentStock <= 10) {
                lowStockItems++;
            } else {
                availableItems++;
            }
        }

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalItems", totalItems);
        statistics.put("availableItems", availableItems);
        statistics.put("lowStockItems", lowStockItems);
        statistics.put("outOfStockItems", outOfStockItems);
        statistics.put("lowStockPercentage", totalItems > 0 ? (double) lowStockItems / totalItems * 100 : 0);
        statistics.put("outOfStockPercentage", totalItems > 0 ? (double) outOfStockItems / totalItems * 100 : 0);

        return ResponseBuilder.build(HttpStatus.OK, "Lấy thống kê tồn kho thành công", statistics);
    }

    @Override
    public ResponseEntity<ResponseObject> getTopMovingItems(HttpServletRequest httpRequest) {
        // TODO: Implement top moving items
        return ResponseBuilder.build(HttpStatus.OK, "Sản phẩm bán chạy chưa được implement", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getSlowMovingItems(HttpServletRequest httpRequest) {
        // TODO: Implement slow moving items
        return ResponseBuilder.build(HttpStatus.OK, "Sản phẩm bán chậm chưa được implement", null);
    }

    // =========================== Helper Methods ========================== \\
    private Map<String, Object> buildStockMovementResponse(StockMovement movement) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", movement.getId());
        response.put("movementType", movement.getMovementType().name());
        response.put("itemType", movement.getItemType());
        response.put("quantityChange", movement.getQuantityChange());
        response.put("unitCost", movement.getUnitCost());
        response.put("referenceCode", movement.getReferenceCode());
        response.put("note", movement.getNote());
        response.put("createdAt", movement.getCreatedAt());
        response.put("createdBy", movement.getCreatedBy());
        
        if (movement.getSucculent() != null) {
            response.put("itemName", movement.getSucculent().getSpecies().getSpeciesName() + " - " + movement.getSucculent().getSize().getDisplayName());
        } else if (movement.getAccessory() != null) {
            response.put("itemName", movement.getAccessory().getName());
        }
        
        return response;
    }

    private String getAlertLevel(Integer currentStock) {
        if (currentStock == 0) return "CRITICAL";
        if (currentStock <= 5) return "HIGH";
        if (currentStock <= 10) return "MEDIUM";
        return "LOW";
    }
}
