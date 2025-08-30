package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.SizeDetail;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Size;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.enums.Zodiac;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.repositories.SucculentRepo;
import com.exe201.group1.psgp_be.services.SucculentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SucculentServiceImpl implements SucculentService {

    private final SucculentRepo succulentRepo;

    @Override
    public ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request) {

        String error = validateCreateSucculent(request);

        if (!error.isBlank()) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                   ResponseObject.builder()
                           .data(null)
                           .message(error)
                           .build()
           );
        }

        for (SizeDetail size : request.getSizeDetails()) {
            succulentRepo.save(Succulent.builder()
                            .speciesName(request.getSpecies_name())
                            .description(request.getDescription())
                            .size(getSizeFromName(size.getName()))
                            .priceBuy(size.getPriceBuy())
                            .priceSell(caculatePriceSell(size.getPriceBuy()))
                            .quantity(size.getQuantity())
                            .fengShui(getFengShuiFromName(request.getFengShui()))
                            .zodiac(getZodiacFromName(request.getZodiac()))
                            .status(Status.AVAILABLE)
                    .build());
        }

        return ResponseEntity.ok(ResponseObject.builder()
                        .data(null)
                        .message("Tạo loài hoa sen đá mới thành công")
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> getSucculents() {
        return ResponseEntity.ok(ResponseObject.builder()
                        .message("Lấy danh sách các loài hoa trong hệ thống thành công")
                        .data(buildListSucculent(succulentRepo.findAll()))
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request) {

        if(!succulentRepo.findById(request.getId()).isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .data(null)
                            .message("Không tìm thấy mặt hàng với id: " + request.getId())
                            .build()
            );
        }

        Succulent succulent = succulentRepo.findById(request.getId()).get();

        String error = validateUpdateSucculent(request, succulent);

        if (!error.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(error)
                            .build()
            );
        }

        // UNAVAILABLE
        if(succulent.getStatus().equals(Status.UNAVAILABLE)){
            //UNAVAILABLE TO AVAILABLE
            if (Status.AVAILABLE.getDisplayName().equalsIgnoreCase(request.getStatus().trim())){
                if (request.getQuantity() == 0) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(
                            ResponseObject.builder()
                                    .data(null)
                                    .message("Chuyển sang 'Đang còn hàng' cần số lượng > 0.")
                                    .build()
                    );
                }
                succulent.setStatus(Status.AVAILABLE);
                updateSucculent(succulent, request);
                succulentRepo.save(succulent);
                return ResponseEntity.ok(ResponseObject.builder()
                        .data(null)
                        .message("Cập nhật mặt hàng thành công")
                        .build());
            }
            //UNAVAILABLE TO OUT-OF-STOCK
            if(Status.OUT_OF_STOCK.getDisplayName().equalsIgnoreCase(request.getStatus().trim())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ResponseObject.builder()
                                .data(null)
                                .message("Không thể chuyển trực tiếp từ 'Ngưng nhập hàng' sang 'Hết hàng'. Vui lòng chuyển sang 'Đang còn hàng' với số lượng mặt hàng lớn hơn 0. Sau đó, nếu cần, cập nhật số lượng về 0 để trở thành 'Hết hàng'.")
                                .build()
                );
            }
            // NOT CHANGE STATUS
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseObject.builder()
                            .data(null)
                            .message("Với trạng thái Ngưng nhập hàng, chỉ được chuyển sang trạng thái 'Đang còn hàng' với số lượng lớn hơn 0 mới được cập nhật.")
                            .build()
            );
        }
        // AVAILABLE
        if(succulent.getStatus().equals(Status.AVAILABLE)){

            //NOT CHANGE STATUS
            if(request.getQuantity() == 0){
                succulent.setStatus(Status.OUT_OF_STOCK);
                updateSucculent(succulent, request);
                succulentRepo.save(succulent);
                return ResponseEntity.ok(ResponseObject.builder()
                        .data(null)
                        .message("Cập nhật mặt hàng thành công")
                        .build());
            }

            //AVAILABLE TO UNAVAILABLE
            if(Status.UNAVAILABLE.getDisplayName().equalsIgnoreCase(request.getStatus().trim())) {
                if(request.getQuantity() > 0){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(
                            ResponseObject.builder()
                                    .data(null)
                                    .message("Không thể 'Ngưng nhập hàng' khi số lượng > 0. Hãy xả kho về 0 trước.")
                                    .build()
                    );
                }
                succulent.setStatus(Status.UNAVAILABLE);
                updateSucculent(succulent, request);
                succulentRepo.save(succulent);
                return ResponseEntity.ok(ResponseObject.builder()
                        .data(null)
                        .message("Cập nhật mặt hàng thành công")
                        .build());
            }
            //AVAILABLE TO OUT-OF-STOCK
            if(Status.OUT_OF_STOCK.getDisplayName().equalsIgnoreCase(request.getStatus().trim())) {
                if(request.getQuantity() > 0){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(
                            ResponseObject.builder()
                                    .data(null)
                                    .message("Không thể 'Hết hàng' khi số lượng > 0. Hãy xả kho về 0 trước.")
                                    .build()
                    );
                }
                succulent.setStatus(Status.OUT_OF_STOCK);
                updateSucculent(succulent, request);
                succulentRepo.save(succulent);
                return ResponseEntity.ok(ResponseObject.builder()
                        .data(null)
                        .message("Cập nhật mặt hàng thành công")
                        .build());
            }

        }

        // OUT-OF-STOCK
        if(Status.OUT_OF_STOCK.equals(succulent.getStatus())) {
            // NOT CHANGE STATUS

            // Khi quantity >0 tự động tahyd đổi tranng thái
            if (request.getQuantity() > 0) {
                succulent.setStatus(Status.AVAILABLE);
            }

            // OUT-OF-STOCK To Available

            if (Status.AVAILABLE.getDisplayName().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() == 0) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(
                            ResponseObject.builder()
                                    .data(null)
                                    .message("Chuyển sang 'Đang còn hàng' cần số lượng > 0.")
                                    .build()
                    );
                }
                succulent.setStatus(Status.AVAILABLE);
            }
            // OUT-OF-STOCK To UnAvailable
            if (Status.UNAVAILABLE.getDisplayName().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(
                            ResponseObject.builder()
                                    .data(null)
                                    .message("Không thể 'Ngưng nhập hàng' khi số lượng > 0. ")
                                    .build()
                    );
                }
                succulent.setStatus(Status.UNAVAILABLE);
            }
        }


        //=================

        updateSucculent(succulent, request);
        succulentRepo.save(succulent);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message("Cập nhật mặt hàng thành công")
                .build());
    }

    private void updateSucculent(Succulent succulent, UpdateSucculentRequest request){
        succulent.setSpeciesName(request.getSpecies_name());
        succulent.setDescription(request.getDescription());
        succulent.setPriceBuy(request.getPriceBuy());
        succulent.setPriceSell(caculatePriceSell(request.getPriceBuy()));
        succulent.setQuantity(request.getQuantity());
        succulent.setFengShui(getFengShuiFromName(request.getFengShui()));
        succulent.setZodiac(getZodiacFromName(request.getZodiac()));
    }

    private Map<String, Object> buildSucculentDetail(Succulent succulent){
        Map<String, Object> response = new HashMap<>();
        String fengshui = "";
        String zodiac = "";
        if(succulent.getFengShui() != null){
            fengshui = succulent.getFengShui().getDisplayName();
        }
        if(succulent.getZodiac() != null){
            zodiac = succulent.getZodiac().getDisplayName();
        }
        response.put("speciesName", succulent.getSpeciesName());
        response.put("description", succulent.getDescription());
        response.put("size", succulent.getSize().getDisplayName());
        response.put("status", succulent.getStatus().getDisplayName());
        response.put("quantity", succulent.getQuantity());
        response.put("priceBuy", succulent.getPriceBuy());
        response.put("priceSell", succulent.getPriceSell());
        response.put("fengShui", fengshui);
        response.put("zodiac", zodiac);
        return response;
    }

    private List<Map<String, Object>> buildListSucculent(List<Succulent> succulents){
        List<Map<String, Object>> response = new ArrayList<>();
        for (Succulent succulent : succulents){
            if(succulent == null){
                continue;
            }
            response.add(buildSucculentDetail(succulent));
        }
        return response;
    }

    private BigDecimal caculatePriceSell(BigDecimal priceBuy){
        return priceBuy.multiply(BigDecimal.valueOf(0.1)).add(priceBuy);
    }

    private String validateUpdateSucculent(UpdateSucculentRequest request, Succulent succulent) {

        String rawStatus = request.getStatus() == null ? "" : request.getStatus().trim();

        if(!Status.OUT_OF_STOCK.getDisplayName().equalsIgnoreCase(rawStatus)
        &&!Status.AVAILABLE.getDisplayName().equalsIgnoreCase(rawStatus)
        &&!Status.UNAVAILABLE.getDisplayName().equalsIgnoreCase(rawStatus)){
            return "Trạng thái không hợp lệ: '" + rawStatus + "'. Giá trị hợp lệ: "
                    + Status.AVAILABLE.getDisplayName() + ", "
                    + Status.OUT_OF_STOCK.getDisplayName() + ", "
                    + Status.UNAVAILABLE.getDisplayName() + ".";
        }

        if (request.getSpecies_name() == null || request.getSpecies_name().trim().isEmpty()) {
            return "Tên loài là bắt buộc";
        }

        if (request.getSpecies_name().length() > 100) {
            return "Tên loài không được vượt quá 100 ký tự";
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả là bắt buộc";
        }

        if (request.getDescription().length() > 300) {
            return "Mô tả không được vượt quá 300 ký tự";
        }

        if(request.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá cây lớn hơn 0";
        }
        if(request.getQuantity() < 0){
            return "Cần nhập số lượng cây lớn hơn hoặc bằng 0";
        }
        if(succulentRepo.existsBySpeciesNameIgnoreCaseAndSizeAndIdNot(request.getSpecies_name(), succulent.getSize(), succulent.getId())){
            return "Loài "+ request.getSpecies_name() + " với kích thước "+ succulent.getSize() +" đã được tạo trong hệ thống";
        }
        return "";
    }
    private String validateCreateSucculent(CreateSucculentRequest request) {

        if (request.getSpecies_name() == null || request.getSpecies_name().trim().isEmpty()) {
            return "Tên loài là bắt buộc";
        }

        if (request.getSpecies_name().length() > 100) {
            return "Tên loài không được vượt quá 100 ký tự";
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả là bắt buộc";
        }

        if (request.getDescription().length() > 300) {
            return "Mô tả không được vượt quá 300 ký tự";
        }

        if (request.getSizeDetails() == null || request.getSizeDetails().isEmpty()) {
            return "Vui lòng chọn ít nhất một kích thước";
        }

        if (request.getSizeDetails().size() > 5) {
            return "Hệ thống chỉ có tối đa 5 kích thước";
        }

        for (SizeDetail size : request.getSizeDetails()) {
            if(getSizeFromName(size.getName())==null){
                 return "Kích thước không hợp lệ: " + size.getName() + " không tồn tại trong hệ thống";
            }
            if( size.getPriceBuy() == null || size.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0){
                return "Cần nhập giá cây " + request.getSpecies_name() + " cho kích thước " + size.getName() +" phải lớn hơn 0";
            }
            if(size.getQuantity() == null || size.getQuantity() <= 0){
                return "Cần nhập số lượng cây " + request.getSpecies_name() + " cho kích thước " + size.getName() +" phải lớn hơn 0";
            }
            if(succulentRepo.existsBySpeciesNameIgnoreCaseAndSize(request.getSpecies_name(), getSizeFromName(size.getName()) )){
                return "Loài "+ request.getSpecies_name() + " với kích thước "+ size.getName() +" đã được tạo trong hệ thống";
            }

        }
        return "";
    }
    private Size getSizeFromName(String name) {
        for (Size size : Size.values()) {
            if (size.getDisplayName().equalsIgnoreCase(name)) {
                return size;
            }
        }
        return null;
    }
    private FengShui getFengShuiFromName(String name){
        for (FengShui fengShui : FengShui.values()) {
            if (fengShui.getDisplayName().equalsIgnoreCase(name)) {
                return fengShui;
            }
        }
        return null;
    }

    private Zodiac getZodiacFromName(String name){
        for (Zodiac zodiac : Zodiac.values()) {
            if (zodiac.getDisplayName().equalsIgnoreCase(name)) {
                return zodiac;
            }
        }
        return null;
    }

}
