package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateDiscountProgramRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.models.DiscountProgram;
import com.exe201.group1.psgp_be.repositories.DiscountProgramRepo;
import com.exe201.group1.psgp_be.services.DiscountProgramService;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountProgramServiceImpl implements DiscountProgramService {

    DiscountProgramRepo discountProgramRepo;

    @Override
    public ResponseEntity<ResponseObject> createDiscountPrograms(CreateDiscountProgramRequest request) {

        String error = validateCreateDiscountProgram(request);
        if(!error.isEmpty()){
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        if(discountProgramRepo.existsByNameIgnoreCase(request.getName())) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Tên chương trình giảm giá đã tồn tại.", null);
        }

        DiscountProgram discountProgram = new DiscountProgram();

        discountProgram.setName(request.getName());
        discountProgram.setDescription(request.getDescription());
        discountProgram.setDiscountValue(request.getDiscountValue());
        discountProgram.setMinimumOrderValue(request.getMinimumOrderValue());
        discountProgram.setUsageLimit(request.getUsageLimit());
        discountProgram.setIsPercentage(request.getIsPercentage());
        discountProgram.setActive(true);
        discountProgram.setCreatedAt(LocalDateTime.now());

        discountProgramRepo.save(discountProgram);

        return ResponseBuilder.build(HttpStatus.OK, "Tạo chương trình giảm giá thành công", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getDiscountPrograms() {
        return ResponseBuilder.build(HttpStatus.OK, "Hiển thị toàn bộ chương trình giảm giá thành công", buildDiscountProgramList(discountProgramRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    private String validateCreateDiscountProgram(CreateDiscountProgramRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên chương trình giảm giá là bắt buộc";
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả chương trình giảm giá là bắt buộc";
        }

        if (request.getDiscountValue() == null || request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            return "Giá trị giảm giá phải lớn hơn 0";
        }

        if (request.getMinimumOrderValue() != null && request.getMinimumOrderValue().compareTo(BigDecimal.ZERO) <= 0) {
            return "Giá trị đơn hàng tối thiểu lớn hơn 0";
        }

        if (request.getUsageLimit() != null && request.getUsageLimit() <= 0) {
            return "Giới hạn sử dụng phải lớn hơn 0";
        }

        if (request.getIsPercentage() == null) {
            return "Cần xác định giá trị giảm là phần trăm hay cố định";
        }
        return "";
    }

    private Map<String, Object> buildDiscountProgram(DiscountProgram discountProgram) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", discountProgram.getName());
        map.put("description", discountProgram.getDescription());
        map.put("discountValue", discountProgram.getDiscountValue());
        map.put("isPercentage", discountProgram.getIsPercentage());
        map.put("minimumOrderValue", discountProgram.getMinimumOrderValue());
        map.put("usageLimit", discountProgram.getUsageLimit());
        map.put("createdAt", discountProgram.getCreatedAt());
        map.put("isActive", discountProgram.getActive());
        return map;
    }

    private List<Map<String, Object>> buildDiscountProgramList(List<DiscountProgram> discountProgramList) {
        List<Map<String, Object>> list = new ArrayList<>();
        for(DiscountProgram discountProgram : discountProgramList){
            list.add(buildDiscountProgram(discountProgram));
        }
        return list;
    }

}
