package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.ReceiveGoodsRequest;
import com.exe201.group1.psgp_be.dto.requests.SizeDetailRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierStatusRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.AccessoryCategory;
import com.exe201.group1.psgp_be.enums.Role;
import com.exe201.group1.psgp_be.enums.Size;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.models.Accessory;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.models.SucculentSpecies;
import com.exe201.group1.psgp_be.models.Supplier;
import com.exe201.group1.psgp_be.models.StockMovement;
import com.exe201.group1.psgp_be.enums.StockMovementType;
import com.exe201.group1.psgp_be.repositories.AccessoryRepo;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.SucculentRepo;
import com.exe201.group1.psgp_be.repositories.SucculentSpeciesRepo;
import com.exe201.group1.psgp_be.repositories.StockMovementRepo;
import com.exe201.group1.psgp_be.repositories.SupplierRepo;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.ProductService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.EntityResponseBuilder;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final SucculentRepo succulentRepo;
    private final AccessoryRepo accessoryRepo;
    private final SucculentSpeciesRepo succulentSpeciesRepo;
    private final SupplierRepo supplierRepo;
    private final StockMovementRepo stockMovementRepo;
    private final JWTService jwtService;
    private final AccountRepo accountRepo;

    // =========================== Supplier ========================== \\
    @Override
    public ResponseEntity<ResponseObject> createSupplier(CreateSupplierRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.ADMIN) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền tạo nhà cung cấp", null);
        }

        String error = validateCreateSupplier(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        supplierRepo.save(Supplier.builder()
                .name(request.getName().trim())
                .contactPerson(request.getContactPerson() == null ? null : request.getContactPerson().trim())
                .phone(request.getPhone() == null ? null : request.getPhone().trim())
                .email(request.getEmail() == null ? null : request.getEmail().trim())
                .address(request.getAddress() == null ? null : request.getAddress().trim())
                .description(request.getDescription() == null ? null : request.getDescription().trim())
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        return ResponseBuilder.build(HttpStatus.OK, "Tạo nhà cung cấp thành công", null);
    }

    private String validateCreateSupplier(CreateSupplierRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên nhà cung cấp là bắt buộc";
        }
        if (request.getName().length() > 100) {
            return "Tên nhà cung cấp không được vượt quá 100 ký tự";
        }
        if (supplierRepo.existsByNameIgnoreCase(request.getName())) {
            return "Nhà cung cấp với tên '" + request.getName() + "' đã tồn tại";
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String phone = request.getPhone().trim();
            if (phone.length() > 10) {
                return "Số điện thoại không được vượt quá 10 ký tự";
            }
            if (!phone.matches("^(0[3|5|7|8|9])\\d{8}$")) {
                return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 03, 05, 07, 08 hoặc 09";
            }
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim();
            if (email.length() > 100) {
                return "Email không được vượt quá 100 ký tự";
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return "Email không đúng định dạng";
            }
        }
        if (request.getAddress() != null && request.getAddress().length() > 500) {
            return "Địa chỉ không được vượt quá 500 ký tự";
        }

        if (request.getDescription() != null && request.getDescription().length() > 500) {
            return "Mô tả không được vượt quá 500 ký tự";
        }

        return "";
    }

    @Override
    public ResponseEntity<ResponseObject> updateSupplier(UpdateSupplierRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.ADMIN) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền xem danh sách nhà cung cấp", null);
        }

        String error = validateUpdateSupplier(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Supplier supplier = supplierRepo.findById(request.getId()).orElse(null);
        if (supplier == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp: " + request.getId(), null);
        }

        supplier.setName(request.getName().trim());
        supplier.setContactPerson(request.getContactPerson() == null ? null : request.getContactPerson().trim());
        supplier.setPhone(request.getPhone() == null ? null : request.getPhone().trim());
        supplier.setEmail(request.getEmail() == null ? null : request.getEmail().trim());
        supplier.setAddress(request.getAddress() == null ? null : request.getAddress().trim());
        supplier.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepo.save(supplier);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật nhà cung cấp thành công", null);
    }

    private String validateUpdateSupplier(UpdateSupplierRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên nhà cung cấp là bắt buộc";
        }

        if (request.getName().length() > 100) {
            return "Tên nhà cung cấp không được vượt quá 100 ký tự";
        }

        if (supplierRepo.existsByNameIgnoreCaseAndIdNot(request.getName(), request.getId())) {
            return "Nhà cung cấp với tên '" + request.getName() + "' đã tồn tại";
        }

        // Validate phone (optional but must be valid when provided)
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String phone = request.getPhone().trim();
            if (phone.length() > 10) {
                return "Số điện thoại không được vượt quá 10 ký tự";
            }
            if (!phone.matches("^(0[3|5|7|8|9])\\d{8}$")) {
                return "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 03, 05, 07, 08 hoặc 09";
            }
        }

        // Validate email (optional but must be valid when provided)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim();
            if (email.length() > 100) {
                return "Email không được vượt quá 100 ký tự";
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return "Email không đúng định dạng";
            }
        }

        if (request.getAddress() != null && request.getAddress().length() > 500) {
            return "Địa chỉ không được vượt quá 500 ký tự";
        }

        if (request.getDescription() != null && request.getDescription().length() > 500) {
            return "Mô tả không được vượt quá 500 ký tự";
        }

        return "";
    }

    @Override
    public ResponseEntity<ResponseObject> updateSupplierStatus(UpdateSupplierStatusRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.ADMIN) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền tạo nhà cung cấp", null);
        }

        Supplier supplier = supplierRepo.findById(request.getId()).orElse(null);
        if (supplier == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp: " + request.getId(), null);
        }

        supplier.setStatus(supplier.getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE);
        supplier.setUpdatedAt(LocalDateTime.now());
        supplierRepo.save(supplier);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật thành công", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getSupplierList(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.ADMIN) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền chỉnh sửa nhà cung cấp", null);
        }

        List<Supplier> suppliers = supplierRepo.findAll().stream()
                .sorted(Comparator.comparing(Supplier::getId).reversed())
                .toList();

        List<Map<String, Object>> data = suppliers.stream()
                .map(EntityResponseBuilder::buildSupplierResponse)
                .toList();
        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách nhà cung cấp thành công", data);
    }

    @Override
    public ResponseEntity<ResponseObject> getTotalSupplierCount(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.ADMIN) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền xem thống kê", null);
        }

        long totalSupplierCount = supplierRepo.count();
        
        Map<String, Object> data = new HashMap<>();
        data.put("totalSupplierCount", totalSupplierCount);
        
        return ResponseBuilder.build(HttpStatus.OK, "Lấy tổng số nhà cung cấp thành công", data);
    }

    // =========================== Succulent ========================== \\
    @Override
    public ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request) {
        String error = validateCreateSucculent(request);
        if (!error.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(error)
                            .build()
            );
        }

        Optional<SucculentSpecies> existingOpt = succulentSpeciesRepo.findBySpeciesNameIgnoreCase(request.getSpeciesName());
        SucculentSpecies species;

        if (existingOpt.isPresent()) {
            species = existingOpt.get();
            species.setDescription(request.getDescription());
            species.setElements(request.getFengShuiList() == null ? new HashSet<>() : new HashSet<>(request.getFengShuiList()));
            species.setZodiacs(request.getZodiacList() == null ? new HashSet<>() : new HashSet<>(request.getZodiacList()));
        } else {
            species = SucculentSpecies.builder()
                    .speciesName(request.getSpeciesName())
                    .description(request.getDescription())
                    .elements(request.getFengShuiList() == null ? new HashSet<>() : new HashSet<>(request.getFengShuiList()))
                    .zodiacs(request.getZodiacList() == null ? new HashSet<>() : new HashSet<>(request.getZodiacList()))
                    .build();
        }

        species = succulentSpeciesRepo.save(species);

        for (SizeDetailRequest size : request.getSizeDetailRequests()) {
            Succulent variant = Succulent.builder()
                    .species(species)
                    .size(getSizeFromName(size.getName()))
                    .priceBuy(size.getPriceBuy())
                    .priceSell(calculatePriceSell(size.getPriceBuy()))
                    .quantity(0)
                    .status(Status.OUT_OF_STOCK)
                    .build();
            succulentRepo.save(variant);
        }

        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message("Tạo catalog loài sen đá thành công. Vui lòng nhập hàng từ nhà cung cấp để bán.")
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> viewSucculentList() {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Lấy danh sách catalog sen đá thành công")
                .data(buildListSucculent(succulentRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))))
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request) {

        if (succulentRepo.findById(request.getId()).isEmpty()) {
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

        if (succulent.getStatus().equals(Status.UNAVAILABLE)) {
            if (Status.AVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
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

            if (Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ResponseObject.builder()
                                .data(null)
                                .message("Không thể chuyển trực tiếp từ 'Ngưng nhập hàng' sang 'Hết hàng'. Vui lòng chuyển sang 'Đang còn hàng' với số lượng mặt hàng lớn hơn 0. Sau đó, nếu cần, cập nhật số lượng về 0 để trở thành 'Hết hàng'.")
                                .build()
                );
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ResponseObject.builder()
                            .data(null)
                            .message("Với trạng thái Ngưng nhập hàng, chỉ được chuyển sang trạng thái 'Đang còn hàng' với số lượng lớn hơn 0 mới được cập nhật.")
                            .build()
            );
        }

        if (succulent.getStatus().equals(Status.AVAILABLE)) {


            if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
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

            if (Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
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

            if (request.getQuantity() == 0) {
                succulent.setStatus(Status.OUT_OF_STOCK);
            }
        }

        if (Status.OUT_OF_STOCK.equals(succulent.getStatus())) {
            if (Status.AVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
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

            if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
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

            if (request.getQuantity() > 0) {
                succulent.setStatus(Status.AVAILABLE);
            }
        }

        updateSucculentInfo(succulent, request);
        succulentRepo.save(succulent);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message("Cập nhật mặt hàng thành công")
                .build());
    }

    private void updateSucculentInfo(Succulent succulent, UpdateSucculentRequest request) {
        succulent.setPriceBuy(request.getPriceBuy());
        succulent.setPriceSell(calculatePriceSell(request.getPriceBuy()));
        succulent.setQuantity(request.getQuantity());
    }

    private Map<String, Object> buildSucculentDetail(Succulent succulent) {
        Map<String, Object> response = new HashMap<>();
        var species = succulent.getSpecies();
        response.put("id", succulent.getId());
        response.put("speciesId", species.getId());
        response.put("speciesName", species.getSpeciesName());
        response.put("description", species.getDescription());
        response.put("quantity", succulent.getQuantity());
        response.put("priceBuy", succulent.getPriceBuy());
        response.put("priceSell", succulent.getPriceSell());
        response.put("size", succulent.getSize().getDisplayName());
        response.put("status", succulent.getStatus().getValue());
        response.put("fengShuiElements", species.getElements());
        response.put("zodiacs", species.getZodiacs());
        return response;
    }

    private List<Map<String, Object>> buildListSucculent(List<Succulent> succulents) {
        List<Map<String, Object>> response = new ArrayList<>();
        for (Succulent succulent : succulents) {
            if (succulent == null) {
                continue;
            }
            response.add(buildSucculentDetail(succulent));
        }
        return response;
    }

    private BigDecimal calculatePriceSell(BigDecimal priceBuy) {
        return priceBuy.multiply(BigDecimal.valueOf(0.1)).add(priceBuy);
    }

    private String validateUpdateSucculent(UpdateSucculentRequest request, Succulent current) {
        if (request.getPriceBuy() == null || request.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá cây lớn hơn 0";
        }
        if (request.getQuantity() < 0) {
            return "Cần nhập số lượng cây lớn hơn hoặc bằng 0";
        }
        String rawStatus = request.getStatus() == null ? "" : request.getStatus().trim();
        if (!Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus)
                && !Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus)
                && !Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái hàng không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: "
                    + Status.AVAILABLE.getValue() + ", "
                    + Status.OUT_OF_STOCK.getValue() + ", "
                    + Status.UNAVAILABLE.getValue() + ".";
        }
        
        // Edge case validation: Không thể chuyển sang AVAILABLE khi quantity = 0
        if (Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && request.getQuantity() == 0) {
            return "Không thể chuyển sang 'Đang còn hàng' khi số lượng = 0";
        }
        
        // Edge case validation: Không thể chuyển sang UNAVAILABLE khi quantity > 0
        if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus) && request.getQuantity() > 0) {
            return "Không thể chuyển sang 'Ngưng nhập hàng' khi số lượng > 0. Hãy xả kho về 0 trước";
        }
        
        // Check duplicate species + size khi update (nếu có thay đổi)
        if (succulentRepo.existsBySpecies_SpeciesNameIgnoreCaseAndSizeAndIdNot(current.getSpecies().getSpeciesName(), current.getSize(), current.getId())) {
            return "Loài " + current.getSpecies().getSpeciesName() + " với kích thước " + current.getSize() + " đã được tạo trong hệ thống";
        }
        return "";
    }

    private String validateCreateSucculent(CreateSucculentRequest request) {

        if (request.getSpeciesName() == null || request.getSpeciesName().trim().isEmpty()) {
            return "Tên loài là bắt buộc";
        }

        if (request.getSpeciesName().length() > 100) {
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
            if (getSizeFromName(size.getName()) == null) {
                return "Kích thước không hợp lệ: " + size.getName() + " không tồn tại trong hệ thống";
            }
            if (size.getPriceBuy() == null || size.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
                return "Cần nhập giá cây " + request.getSpeciesName() + " cho kích thước " + size.getName() + " phải lớn hơn 0";
            }
            // Bỏ check quantity > 0 vì tạo catalog không cần quantity
            // Quantity sẽ được cập nhật khi nhập hàng từ nhà cung cấp
            if (succulentRepo.existsBySpecies_SpeciesNameIgnoreCaseAndSize(request.getSpeciesName(), getSizeFromName(size.getName()))) {
                return "Loài " + request.getSpeciesName() + " với kích thước " + size.getName() + " đã được tạo trong hệ thống";
            }
        }
        // Validate lists if provided
        if (request.getFengShuiList() != null) {
            for (var e : request.getFengShuiList()) {
                if (e == null) {
                    return "Danh sách phong thủy chứa giá trị không hợp lệ";
                }
            }
        }
        if (request.getZodiacList() != null) {
            for (var z : request.getZodiacList()) {
                if (z == null) {
                    return "Danh sách cung hoàng đạo chứa giá trị không hợp lệ";
                }
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

    // =========================== Accessory ========================== \\
    @Override
    public ResponseEntity<ResponseObject> createAccessory(CreateAccessoryRequest request) {
        String error = validateCreateAccessory(request, accessoryRepo);
        if (!error.isEmpty()) {
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
                .priceSell(calculatePriceSell(request.getPriceBuy()))
                .quantity(0) // Tạo catalog trước, chưa có hàng
                .status(Status.OUT_OF_STOCK) // Chưa có hàng để bán
                .build());
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Tạo catalog phụ kiện thành công. Vui lòng nhập hàng từ nhà cung cấp để bán.")
                .data(null)
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> getAccessories() {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Lấy danh sách catalog phụ kiện thành công")
                .data(buildListAccessories(accessoryRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))))
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> receiveGoods(ReceiveGoodsRequest request) {
        String error = validateReceiveGoods(request);
        if (!error.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .data(null)
                            .message(error)
                            .build()
            );
        }

        // Lấy thông tin nhà cung cấp
        Optional<Supplier> supplierOpt = supplierRepo.findById(request.getSupplierId());
        Supplier supplier = supplierOpt.get();

        // Xử lý nhập hàng cho từng item
        for (ReceiveGoodsRequest.GoodsItem item : request.getItems()) {
            if ("SUCCULENT".equals(item.getItemType())) {
                Optional<Succulent> succulentOpt = succulentRepo.findById(item.getSucculentId());
                if (succulentOpt.isPresent()) {
                    Succulent succulent = succulentOpt.get();
                    // Cập nhật số lượng
                    succulent.setQuantity(succulent.getQuantity() + item.getQuantity());
                    // Cập nhật giá mua (có thể thay đổi theo lô hàng mới)
                    succulent.setPriceBuy(item.getPriceBuy());
                    succulent.setPriceSell(calculatePriceSell(item.getPriceBuy()));

                    // Nếu có hàng và đang OUT_OF_STOCK thì chuyển AVAILABLE
                    if (succulent.getQuantity() > 0 && succulent.getStatus() == Status.OUT_OF_STOCK) {
                        succulent.setStatus(Status.AVAILABLE);
                    }

                    succulentRepo.save(succulent);

                    // Tạo StockMovement record
                    StockMovement stockMovement = StockMovement.builder()
                            .movementType(StockMovementType.PURCHASE_IN)
                            .itemType("SUCCULENT")
                            .succulent(succulent)
                            .quantityChange(item.getQuantity())
                            .unitCost(item.getPriceBuy())
                            .supplier(supplier)
                            .referenceCode(request.getReferenceCode())
                            .note(request.getNote())
                            .createdBy(Role.SELLER.name())
                            .build();
                    stockMovementRepo.save(stockMovement);
                }
            } else if ("ACCESSORY".equals(item.getItemType())) {
                Optional<Accessory> accessoryOpt = accessoryRepo.findById(item.getAccessoryId());
                if (accessoryOpt.isPresent()) {
                    Accessory accessory = accessoryOpt.get();
                    // Cập nhật số lượng
                    accessory.setQuantity(accessory.getQuantity() + item.getQuantity());
                    // Cập nhật giá mua
                    accessory.setPriceBuy(item.getPriceBuy());
                    accessory.setPriceSell(calculatePriceSell(item.getPriceBuy()));

                    // Nếu có hàng và đang OUT_OF_STOCK thì chuyển AVAILABLE
                    if (accessory.getQuantity() > 0 && accessory.getStatus() == Status.OUT_OF_STOCK) {
                        accessory.setStatus(Status.AVAILABLE);
                    }

                    accessoryRepo.save(accessory);

                    // Tạo StockMovement record
                    StockMovement stockMovement = StockMovement.builder()
                            .movementType(StockMovementType.PURCHASE_IN)
                            .itemType("ACCESSORY")
                            .accessory(accessory)
                            .quantityChange(item.getQuantity())
                            .unitCost(item.getPriceBuy())
                            .supplier(supplier)
                            .referenceCode(request.getReferenceCode())
                            .note(request.getNote())
                            .createdBy(Role.SELLER.name()) // TODO: Lấy từ JWT token
                            .build();
                    stockMovementRepo.save(stockMovement);
                }
            }
        }

        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message("Nhập hàng từ nhà cung cấp thành công")
                .build());
    }

    @Override
    public ResponseEntity<ResponseObject> updateAccessory(UpdateAccessoryRequest request) {

        String error = validateUpdateAccessory(request, accessoryRepo);
        if (!error.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(error)
                            .data(null)
                            .build()
            );
        }

        if (accessoryRepo.findById(request.getId()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .data(null)
                            .message("Không tìm thấy mặt hàng với id: " + request.getId())
                            .build()
            );
        }

        Accessory accessory = accessoryRepo.findById(request.getId()).get();

        // UNAVAILABLE
        if (accessory.getStatus().equals(Status.UNAVAILABLE)) {
            //UNAVAILABLE TO AVAILABLE
            if (Status.AVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
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
            if (Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(request.getStatus().trim())) {
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
        if (accessory.getStatus().equals(Status.AVAILABLE)) {

            //AVAILABLE TO UNAVAILABLE
            if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
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
            if (Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
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

            //NOT CHANGE STATUS
            if (request.getQuantity() == 0) {
                accessory.setStatus(Status.OUT_OF_STOCK);
            }

        }

        // OUT-OF-STOCK
        if (Status.OUT_OF_STOCK.equals(accessory.getStatus())) {

            // OUT-OF-STOCK To Available

            if (Status.AVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
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
            if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
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
            // NOT CHANGE STATUS
            // Khi quantity >0 tự động tahyd đổi tranng thái
            if (request.getQuantity() > 0) {
                accessory.setStatus(Status.AVAILABLE);
            }
        }

        updateAccessoryInfo(accessory, request);
        accessoryRepo.save(accessory);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message("Cập nhật mặt hàng thành công")
                .build());
    }

    private void updateAccessoryInfo(Accessory accessory, UpdateAccessoryRequest request) {
        accessory.setName(request.getName());
        accessory.setDescription(request.getDescription());
        accessory.setPriceBuy(request.getPriceBuy());
        accessory.setPriceSell(calculatePriceSell(request.getPriceBuy()));
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

        if (request.getQuantity() < 0) {
            return "Cần nhập số lượng hàng lớn hơn hoặc bằng 0";
        }

        if (request.getPriceBuy() == null || request.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá tiền mặt hàng lớn hơn 0";
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

        if (!Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus)
                && !Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus)
                && !Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái hàng không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: "
                    + Status.AVAILABLE.getValue() + ", "
                    + Status.OUT_OF_STOCK.getValue() + ", "
                    + Status.UNAVAILABLE.getValue() + ".";
        }
        
        // Edge case validation: Không thể chuyển sang AVAILABLE khi quantity = 0
        if (Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && request.getQuantity() == 0) {
            return "Không thể chuyển sang 'Đang còn hàng' khi số lượng = 0";
        }
        
        // Edge case validation: Không thể chuyển sang UNAVAILABLE khi quantity > 0
        if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus) && request.getQuantity() > 0) {
            return "Không thể chuyển sang 'Ngưng nhập hàng' khi số lượng > 0. Hãy xả kho về 0 trước";
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

        // Bỏ check quantity > 0 vì tạo catalog không cần quantity
        // Quantity sẽ được cập nhật khi nhập hàng từ nhà cung cấp

        if (request.getPriceBuy() == null || request.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá tiền mặt hàng lớn hơn 0";
        }

        if (accessoryRepo.existsByNameIgnoreCase(request.getName())) {
            return "Mặt hàng có tên '" + request.getName() + "' đã được tạo trong hệ thống.";
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
        response.put("category", accessory.getCategory().getDisplayName());
        response.put("status", accessory.getStatus());
        return response;
    }

    private List<Map<String, Object>> buildListAccessories(List<Accessory> accessories) {
        List<Map<String, Object>> response = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (accessory == null) {
                continue;
            }
            response.add(buildAccessoryDetail(accessory));
        }
        return response;
    }

    private AccessoryCategory getAccessoryCategoryFromName(String name) {
        for (AccessoryCategory category : AccessoryCategory.values()) {
            if (category.getDisplayName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }

    private String validateReceiveGoods(ReceiveGoodsRequest request) {
        if (request.getSupplierId() <= 0) {
            return "ID nhà cung cấp là bắt buộc";
        }

        // Kiểm tra nhà cung cấp có tồn tại không
        if (supplierRepo.findById(request.getSupplierId()).isEmpty()) {
            return "Không tìm thấy nhà cung cấp với ID: " + request.getSupplierId();
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return "Danh sách hàng nhập không được để trống";
        }

        // Edge case: Kiểm tra giới hạn số lượng items
        if (request.getItems().size() > 50) {
            return "Không thể nhập quá 50 loại hàng trong một lần";
        }

        for (ReceiveGoodsRequest.GoodsItem item : request.getItems()) {
            if (item.getItemType() == null || item.getItemType().trim().isEmpty()) {
                return "Loại hàng (SUCCULENT/ACCESSORY) là bắt buộc";
            }

            if (!"SUCCULENT".equals(item.getItemType()) && !"ACCESSORY".equals(item.getItemType())) {
                return "Loại hàng không hợp lệ. Chỉ chấp nhận SUCCULENT hoặc ACCESSORY";
            }

            if (item.getQuantity() <= 0) {
                return "Số lượng nhập phải lớn hơn 0";
            }

            // Edge case: Kiểm tra giới hạn số lượng nhập
            if (item.getQuantity() > 10000) {
                return "Số lượng nhập không được vượt quá 10,000 đơn vị cho một loại hàng";
            }

            if (item.getPriceBuy() == null || item.getPriceBuy().compareTo(BigDecimal.ZERO) <= 0) {
                return "Giá mua phải lớn hơn 0";
            }

            // Edge case: Kiểm tra giới hạn giá mua
            if (item.getPriceBuy().compareTo(BigDecimal.valueOf(10000000)) > 0) {
                return "Giá mua không được vượt quá 10,000,000 VNĐ cho một đơn vị";
            }

            if ("SUCCULENT".equals(item.getItemType())) {
                if (item.getSucculentId() <= 0) {
                    return "ID sen đá là bắt buộc cho loại SUCCULENT";
                }
                if (succulentRepo.findById(item.getSucculentId()).isEmpty()) {
                    return "Không tìm thấy sen đá với ID: " + item.getSucculentId();
                }
                
                // Edge case: Kiểm tra trạng thái sen đá có thể nhập hàng không
                Succulent succulent = succulentRepo.findById(item.getSucculentId()).get();
                if (succulent.getStatus() == Status.UNAVAILABLE) {
                    return "Không thể nhập hàng cho sen đá đang ở trạng thái 'Ngưng nhập hàng'";
                }
            } else {
                if (item.getAccessoryId() <= 0) {
                    return "ID phụ kiện là bắt buộc cho loại ACCESSORY";
                }
                if (accessoryRepo.findById(item.getAccessoryId()).isEmpty()) {
                    return "Không tìm thấy phụ kiện với ID: " + item.getAccessoryId();
                }
                
                // Edge case: Kiểm tra trạng thái phụ kiện có thể nhập hàng không
                Accessory accessory = accessoryRepo.findById(item.getAccessoryId()).get();
                if (accessory.getStatus() == Status.UNAVAILABLE) {
                    return "Không thể nhập hàng cho phụ kiện đang ở trạng thái 'Ngưng nhập hàng'";
                }
            }
        }

        return "";
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

    // =========================== Custom Request ========================== \\
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
