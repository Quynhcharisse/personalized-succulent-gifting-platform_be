package com.exe201.group1.psgp_be.dto.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReportRequest {
    Integer itemId;
    String itemType; // "SUCCULENT" or "ACCESSORY"
    LocalDateTime startDate;
    LocalDateTime endDate;
    String reportType; // "SUMMARY", "DETAILED", "PERFORMANCE"
}
