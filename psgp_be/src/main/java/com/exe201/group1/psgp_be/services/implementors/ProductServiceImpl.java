package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.SizeDetailRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.AccessoryCategory;
import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Size;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.enums.Zodiac;
import com.exe201.group1.psgp_be.models.Accessory;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.repositories.AccessoryRepo;
import com.exe201.group1.psgp_be.repositories.SucculentRepo;
import com.exe201.group1.psgp_be.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
public class ProductServiceImpl implements ProductService {

    private final SucculentRepo succulentRepo;
    private final AccessoryRepo accessoryRepo;

                        // =========================== Succulent ========================== \\

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

        for (SizeDetailRequest size : request.getSizeDetailRequests()) {
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
                .data(buildListSucculent(succulentRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))))
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
                updateSucculentInfo(succulent, request);
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
                updateSucculentInfo(succulent, request);
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
                updateSucculentInfo(succulent, request);
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
                updateSucculentInfo(succulent, request);
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

        updateSucculentInfo(succulent, request);
        succulentRepo.save(succulent);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message("Cập nhật mặt hàng thành công")
                .build());
    }

    private void updateSucculentInfo(Succulent succulent, UpdateSucculentRequest request){
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
        response.put("id", succulent.getId());
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

        if (!Status.OUT_OF_STOCK.getDisplayName().equalsIgnoreCase(rawStatus)
                && !Status.AVAILABLE.getDisplayName().equalsIgnoreCase(rawStatus)
                && !Status.UNAVAILABLE.getDisplayName().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái hàng không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: "
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

        if (request.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá cây lớn hơn 0";
        }
        if (request.getQuantity() < 0) {
            return "Cần nhập số lượng cây lớn hơn hoặc bằng 0";
        }
        if (succulentRepo.existsBySpeciesNameIgnoreCaseAndSizeAndIdNot(request.getSpecies_name(), succulent.getSize(), succulent.getId())) {
            return "Loài " + request.getSpecies_name() + " với kích thước " + succulent.getSize() + " đã được tạo trong hệ thống";
        }

        if (!FengShui.HOA.getDisplayName().equalsIgnoreCase(request.getFengShui())
                && !FengShui.KIM.getDisplayName().equalsIgnoreCase(request.getFengShui())
                && !FengShui.THUY.getDisplayName().equalsIgnoreCase(request.getFengShui())
                && !FengShui.MOC.getDisplayName().equalsIgnoreCase(request.getFengShui())
                && !FengShui.THO.getDisplayName().equalsIgnoreCase(request.getFengShui())
                && !request.getFengShui().isEmpty()) {
            return "Giá trị phong thủy không hợp lệ: '" + request.getFengShui() + "'. Giá trị hợp lệ: "
                    + FengShui.HOA.getDisplayName() + ", "
                    + FengShui.KIM.getDisplayName() + ", "
                    + FengShui.MOC.getDisplayName() + ", "
                    + FengShui.HOA.getDisplayName() + ", "
                    + FengShui.THO.getDisplayName() + ".";
        }

        if (!Zodiac.BACH_DUONG.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.KIM_NGUU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.SONG_TU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.CU_GIAI.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.SU_TU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.XU_NU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.THIEN_BINH.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.BO_CAP.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.NHAN_MA.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.MA_KET.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.BAO_BINH.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.SONG_NGU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !request.getZodiac().isEmpty()) {

            return "Cung hoàng đạo không hợp lệ: '" + request.getZodiac() + "'. Giá trị hợp lệ: "
                    + Zodiac.BACH_DUONG.getDisplayName() + ", "
                    + Zodiac.KIM_NGUU.getDisplayName() + ", "
                    + Zodiac.SONG_TU.getDisplayName() + ", "
                    + Zodiac.CU_GIAI.getDisplayName() + ", "
                    + Zodiac.SU_TU.getDisplayName() + ", "
                    + Zodiac.XU_NU.getDisplayName() + ", "
                    + Zodiac.THIEN_BINH.getDisplayName() + ", "
                    + Zodiac.BO_CAP.getDisplayName() + ", "
                    + Zodiac.NHAN_MA.getDisplayName() + ", "
                    + Zodiac.MA_KET.getDisplayName() + ", "
                    + Zodiac.BAO_BINH.getDisplayName() + ", "
                    + Zodiac.SONG_NGU.getDisplayName() + ".";
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

        if (request.getSizeDetailRequests() == null || request.getSizeDetailRequests().isEmpty()) {
            return "Vui lòng chọn ít nhất một kích thước";
        }

        if (request.getSizeDetailRequests().size() > 5) {
            return "Hệ thống chỉ có tối đa 5 kích thước";
        }

        for (SizeDetailRequest size : request.getSizeDetailRequests()) {
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

        if(!FengShui.HOA.getDisplayName().equalsIgnoreCase(request.getFengShui())
                &&!FengShui.KIM.getDisplayName().equalsIgnoreCase(request.getFengShui())
                &&!FengShui.THUY.getDisplayName().equalsIgnoreCase(request.getFengShui())
                && !FengShui.MOC.getDisplayName().equalsIgnoreCase(request.getFengShui())
                && !FengShui.THO.getDisplayName().equalsIgnoreCase(request.getFengShui())
                && !request.getFengShui().isEmpty()){
            return "Giá trị phong thủy không hợp lệ: '" + request.getFengShui() + "'. Giá trị hợp lệ: "
                    + FengShui.HOA.getDisplayName() + ", "
                    + FengShui.KIM.getDisplayName() + ", "
                    + FengShui.MOC.getDisplayName() + ", "
                    + FengShui.HOA.getDisplayName() + ", "
                    + FengShui.THO.getDisplayName() + ".";
        }
        if(request.getZodiac() == null || request.getZodiac().trim().isEmpty()) {
            return "";
        }
        if (!Zodiac.BACH_DUONG.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.KIM_NGUU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.SONG_TU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.CU_GIAI.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.SU_TU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.XU_NU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.THIEN_BINH.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.BO_CAP.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.NHAN_MA.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.MA_KET.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.BAO_BINH.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !Zodiac.SONG_NGU.getDisplayName().equalsIgnoreCase(request.getZodiac())
                && !request.getZodiac().isEmpty()) {

            return "Cung hoàng đạo không hợp lệ: '" + request.getZodiac() + "'. Giá trị hợp lệ: "
                    + Zodiac.BACH_DUONG.getDisplayName() + ", "
                    + Zodiac.KIM_NGUU.getDisplayName() + ", "
                    + Zodiac.SONG_TU.getDisplayName() + ", "
                    + Zodiac.CU_GIAI.getDisplayName() + ", "
                    + Zodiac.SU_TU.getDisplayName() + ", "
                    + Zodiac.XU_NU.getDisplayName() + ", "
                    + Zodiac.THIEN_BINH.getDisplayName() + ", "
                    + Zodiac.BO_CAP.getDisplayName() + ", "
                    + Zodiac.NHAN_MA.getDisplayName() + ", "
                    + Zodiac.MA_KET.getDisplayName() + ", "
                    + Zodiac.BAO_BINH.getDisplayName() + ", "
                    + Zodiac.SONG_NGU.getDisplayName() + ".";
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

    // =========================== Accessory ========================== \\

    @Override
    public ResponseEntity<ResponseObject> createAccessory(CreateAccessoryRequest request) {
        String error = validateCreateAccessory(request, accessoryRepo);
        if(!error.isEmpty()){
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(error)
                            .data(null)
                            .build()
            );
        }
        accessoryRepo.save(Accessory.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .category(getAccessoryCategoryFromName(request.getCategory()))
                        .priceBuy(request.getPriceBuy())
                        .priceSell(caculatePriceSell(request.getPriceBuy()))
                        .quantity(request.getQuantity())
                        .status(Status.AVAILABLE)
                .build());
        return ResponseEntity.ok(ResponseObject.builder()
                        .message("Tạo mặt hàng thành công")
                        .data(null)
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> getAccessories() {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Lấy danh sách hàng trong hệ thống thành công")
                .data(buildListAccessories(accessoryRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))))
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> updateAccessory(UpdateAccessoryRequest request) {

        String error = validateUpdateAccessory(request, accessoryRepo);
        if(!error.isEmpty()){
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(error)
                            .data(null)
                            .build()
            );
        }

        if(!accessoryRepo.findById(request.getId()).isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .data(null)
                            .message("Không tìm thấy mặt hàng với id: " + request.getId())
                            .build()
            );
        }

        Accessory accessory = accessoryRepo.findById(request.getId()).get();

        // UNAVAILABLE
        if(accessory.getStatus().equals(Status.UNAVAILABLE)){
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
                accessory.setStatus(Status.AVAILABLE);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
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
        if(accessory.getStatus().equals(Status.AVAILABLE)){

            //NOT CHANGE STATUS
            if(request.getQuantity() == 0){
                accessory.setStatus(Status.OUT_OF_STOCK);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
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
                accessory.setStatus(Status.UNAVAILABLE);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
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
                accessory.setStatus(Status.OUT_OF_STOCK);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
                return ResponseEntity.ok(ResponseObject.builder()
                        .data(null)
                        .message("Cập nhật mặt hàng thành công")
                        .build());
            }

        }

        // OUT-OF-STOCK
        if(Status.OUT_OF_STOCK.equals(accessory.getStatus())) {
            // NOT CHANGE STATUS

            // Khi quantity >0 tự động tahyd đổi tranng thái
            if (request.getQuantity() > 0) {
                accessory.setStatus(Status.AVAILABLE);
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
                accessory.setStatus(Status.AVAILABLE);
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
                accessory.setStatus(Status.UNAVAILABLE);
            }
        }

        updateAccessoryInfo(accessory, request);
        accessoryRepo.save(accessory);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message("Cập nhật mặt hàng thành công")
                .build());
    }

    private void updateAccessoryInfo(Accessory accessory, UpdateAccessoryRequest request){
        accessory.setName(request.getName());
        accessory.setDescription(request.getDescription());
        accessory.setPriceBuy(request.getPriceBuy());
        accessory.setPriceSell(caculatePriceSell(request.getPriceBuy()));
        accessory.setQuantity(request.getQuantity());
        accessory.setCategory(getAccessoryCategoryFromName(request.getCategory()));
    }

    private String validateUpdateAccessory(UpdateAccessoryRequest request, AccessoryRepo accessoryRepo) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên mặt hàng là bắt buộc";
        }

        if (request.getName().length() > 100) {
            return "Tên mặt hàng không được vượt quá 100 ký tự";
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả là bắt buộc";
        }

        if (request.getDescription().length() > 300) {
            return "Mô tả không được vượt quá 300 ký tự";
        }

        if(request.getQuantity() == null || request.getQuantity() < 1) {
            return "Cần nhập số lượng mặt hàng lớn hơn 0";
        }

        if(request.getPriceBuy() == null || request.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá tiền mặt hàng lớn hơn 0";
        }

        if (accessoryRepo.existsByNameIgnoreCase(request.getName())) {
            return "Mặt hàng có tên '"+ request.getName() +"' đã được tạo trong hệ thống.";
        }

        String rawCategory = request.getCategory() == null ? "" : request.getCategory().trim();

        if (!AccessoryCategory.SOIL.getDisplayName().equalsIgnoreCase(rawCategory)
                && !AccessoryCategory.PLANT_POT.getDisplayName().equalsIgnoreCase(rawCategory)
                && !AccessoryCategory.DECOR_ACCESSORY.getDisplayName().equalsIgnoreCase(rawCategory)) {
            return "Phân loại hàng không hợp lệ: '" + rawCategory + "'. Phân loại hàng hợp lệ: "
                    + AccessoryCategory.SOIL.getDisplayName() + ", "
                    + AccessoryCategory.PLANT_POT.getDisplayName() + ", "
                    + AccessoryCategory.DECOR_ACCESSORY.getDisplayName() + ".";
        }

        String rawStatus = request.getStatus() == null ? "" : request.getStatus().trim();

        if (!Status.OUT_OF_STOCK.getDisplayName().equalsIgnoreCase(rawStatus)
                && !Status.AVAILABLE.getDisplayName().equalsIgnoreCase(rawStatus)
                && !Status.UNAVAILABLE.getDisplayName().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái hàng không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: "
                    + Status.AVAILABLE.getDisplayName() + ", "
                    + Status.OUT_OF_STOCK.getDisplayName() + ", "
                    + Status.UNAVAILABLE.getDisplayName() + ".";
        }

        if (accessoryRepo.existsByNameIgnoreCaseAndIdNot(request.getName(), request.getId())) {
            return "Mặt hàng '" + request.getName() + "' đã được tạo trong hệ thống";
        }

        return "";
    }

    private String validateCreateAccessory(CreateAccessoryRequest request, AccessoryRepo accessoryRepo) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên mặt hàng là bắt buộc";
        }

        if (request.getName().length() > 100) {
            return "Tên mặt hàng không được vượt quá 100 ký tự";
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả là bắt buộc";
        }

        if (request.getDescription().length() > 300) {
            return "Mô tả không được vượt quá 300 ký tự";
        }

        if(request.getQuantity() == null || request.getQuantity() < 1) {
            return "Cần nhập số lượng mặt hàng lớn hơn 0";
        }

        if(request.getPriceBuy() == null || request.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá tiền mặt hàng lớn hơn 0";
        }

        if (accessoryRepo.existsByNameIgnoreCase(request.getName())) {
            return "Mặt hàng có tên '"+ request.getName() +"' đã được tạo trong hệ thống.";
        }

        String rawCategory = request.getCategory() == null ? "" : request.getCategory().trim();

        if (!AccessoryCategory.SOIL.getDisplayName().equalsIgnoreCase(rawCategory)
                && !AccessoryCategory.PLANT_POT.getDisplayName().equalsIgnoreCase(rawCategory)
                && !AccessoryCategory.DECOR_ACCESSORY.getDisplayName().equalsIgnoreCase(rawCategory)) {
            return "Phân loại hàng không hợp lệ: '" + rawCategory + "'. Phân loại hàng hợp lệ: "
                    + AccessoryCategory.SOIL.getDisplayName() + ", "
                    + AccessoryCategory.PLANT_POT.getDisplayName() + ", "
                    + AccessoryCategory.DECOR_ACCESSORY.getDisplayName() + ".";
        }
        return "";
    }

    private Map<String, Object> buildAccessoryDetail(Accessory accessory) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", accessory.getId());
        response.put("name", accessory.getName());
        response.put("description", accessory.getDescription());
        response.put("priceBuy", accessory.getPriceBuy());
        response.put("priceSell", accessory.getPriceSell());
        response.put("quantity", accessory.getQuantity());
        response.put("category", accessory.getCategory());
        response.put("status", accessory.getStatus());
        return response;
    }

    private List<Map<String, Object>> buildListAccessories(List<Accessory> accessories) {
        List<Map<String, Object>> response = new ArrayList<>();
        for (Accessory accessory : accessories ) {
            if(accessory == null){
                continue;
            }
            response.add(buildAccessoryDetail(accessory));
        }
        return response;
    }

    private AccessoryCategory getAccessoryCategoryFromName(String name){
        for (AccessoryCategory category : AccessoryCategory.values()) {
            if (category.getDisplayName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }


                            // =========================== Product ========================== \\

    @Override
    public ResponseEntity<ResponseObject> createProduct(ProductCreateRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> viewProduct() {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> updateProduct(ProductUpdateRequest request) {
        return null;
    }

                            // =========================== Custome Request ========================== \\

    @Override
    public ResponseEntity<ResponseObject> customRequestListByBuyer(HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> createCustomRequest(CreateCustomRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> updateCustomRequest(UpdateCustomRequestRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> deleteCustomRequest(DeleteCustomRequestRequest request) {
        return null;
    }

}