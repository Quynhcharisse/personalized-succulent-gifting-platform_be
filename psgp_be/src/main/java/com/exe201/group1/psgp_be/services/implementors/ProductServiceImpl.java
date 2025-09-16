package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.AddWishListItemRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
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
import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.models.SucculentSpecies;
import com.exe201.group1.psgp_be.models.Supplier;
import com.exe201.group1.psgp_be.models.WishlistItem;
import com.exe201.group1.psgp_be.repositories.AccessoryRepo;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.ProductRepo;
import com.exe201.group1.psgp_be.repositories.SucculentRepo;
import com.exe201.group1.psgp_be.repositories.SucculentSpeciesRepo;
import com.exe201.group1.psgp_be.repositories.SupplierRepo;
import com.exe201.group1.psgp_be.repositories.WishListItemRepo;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.ProductService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.EntityResponseBuilder;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ProductRepo productRepo;
    private final JWTService jwtService;
    private final AccountRepo accountRepo;
    private final WishListItemRepo wishListItemRepo;

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

        supplierRepo.save(Supplier.builder().name(request.getName().trim()).contactPerson(request.getContactPerson() == null ? null : request.getContactPerson().trim()).phone(request.getPhone() == null ? null : request.getPhone().trim()).email(request.getEmail() == null ? null : request.getEmail().trim()).address(request.getAddress() == null ? null : request.getAddress().trim()).description(request.getDescription() == null ? null : request.getDescription().trim()).status(Status.ACTIVE).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());

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

        List<Supplier> suppliers = supplierRepo.findAll().stream().sorted(Comparator.comparing(Supplier::getId).reversed()).toList();

        List<Map<String, Object>> data = suppliers.stream().map(EntityResponseBuilder::buildSupplierResponse).toList();
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
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Optional<SucculentSpecies> existingOpt = succulentSpeciesRepo.findBySpeciesNameIgnoreCase(request.getSpeciesName());
        SucculentSpecies species;

        if (existingOpt.isPresent()) {
            species = existingOpt.get();
            species.setDescription(request.getDescription());
            species.setElements(request.getFengShuiList() == null ? new HashSet<>() : new HashSet<>(request.getFengShuiList()));
            species.setZodiacs(request.getZodiacList() == null ? new HashSet<>() : new HashSet<>(request.getZodiacList()));
        } else {
            species = SucculentSpecies.builder().speciesName(request.getSpeciesName()).description(request.getDescription()).elements(request.getFengShuiList() == null ? new HashSet<>() : new HashSet<>(request.getFengShuiList())).zodiacs(request.getZodiacList() == null ? new HashSet<>() : new HashSet<>(request.getZodiacList())).build();
        }

        species = succulentSpeciesRepo.save(species);

        for (SizeDetailRequest size : request.getSizeDetailRequests()) {
            Succulent variant = Succulent.builder()
                    .species(species)
                    .size(getSizeFromName(size.getName()))
                    .priceSell(size.getPriceSell())
                    .quantity(0)
                    .status(Status.OUT_OF_STOCK)
                    .imageUrl(request.getImageUrl())
                    .build();
            succulentRepo.save(variant);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Tạo catalog loài sen đá thành công", null);
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

        if (request.getImageUrl() == null || request.getImageUrl().trim().isEmpty()) {
            return "Image URL is required";
        }
        if (!request.getImageUrl().matches("^(http|https)://.*$")) {
            return "Invalid Image URL format";
        }
        if (!request.getImageUrl().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            return "Image URL must end with a valid image file extension (jpg, jpeg, png, gif)";
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
            if (size.getPriceSell() == null || size.getPriceSell().compareTo(BigDecimal.ZERO) <= 0) {
                return "Cần nhập giá bán " + request.getSpeciesName() + " cho kích thước " + size.getName() + " phải lớn hơn 0";
            }
            // Bỏ check quantity > 0 vì tạo catalog không cần quantity
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

    @Override
    public ResponseEntity<ResponseObject> viewSucculentList() {
        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách catalog sen đá thành công", buildListSucculent(succulentRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))));
    }


    @Override
    public ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request) {

        if (succulentRepo.findById(request.getId()).isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy mặt hàng với id: " + request.getId(), null);
        }

        Succulent succulent = succulentRepo.findById(request.getId()).get();

        String error = validateUpdateSucculent(request, succulent);

        if (!error.isBlank()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        if (succulent.getStatus().equals(Status.UNAVAILABLE)) {
            if (Status.AVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() == 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Chuyển sang 'Đang còn hàng' cần số lượng > 0.", null);
                }
                succulent.setStatus(Status.AVAILABLE);
                updateSucculentInfo(succulent, request);
                succulentRepo.save(succulent);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }

            if (Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                return ResponseBuilder.build(HttpStatus.CONFLICT, "Không thể chuyển trực tiếp từ 'Ngưng nhập hàng' sang 'Hết hàng'. Vui lòng chuyển sang 'Đang còn hàng' với số lượng mặt hàng lớn hơn 0. Sau đó, nếu cần, cập nhật số lượng về 0 để trở thành 'Hết hàng'.", null);
            }

            return ResponseBuilder.build(HttpStatus.CONFLICT, "Với trạng thái Ngưng nhập hàng, chỉ được chuyển sang trạng thái 'Đang còn hàng' với số lượng lớn hơn 0 mới được cập nhật.", null);
        }

        if (succulent.getStatus().equals(Status.AVAILABLE)) {

            if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Không thể 'Ngưng nhập hàng' khi số lượng > 0. Hãy xả kho về 0 trước.", null);
                }
                succulent.setStatus(Status.UNAVAILABLE);
                updateSucculentInfo(succulent, request);
                succulentRepo.save(succulent);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }

            if (Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Không thể 'Hết hàng' khi số lượng > 0. Hãy xả kho về 0 trước.", null);
                }
                succulent.setStatus(Status.OUT_OF_STOCK);
                updateSucculentInfo(succulent, request);
                succulentRepo.save(succulent);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }

            if (request.getQuantity() == 0) {
                succulent.setStatus(Status.OUT_OF_STOCK);
            }
        }

        if (Status.OUT_OF_STOCK.equals(succulent.getStatus())) {
            if (Status.AVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() == 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Chuyển sang 'Đang còn hàng' cần số lượng > 0.", null);
                }
                succulent.setStatus(Status.AVAILABLE);
                updateSucculentInfo(succulent, request);
                succulentRepo.save(succulent);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }

            if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Không thể 'Ngưng nhập hàng' khi số lượng > 0. ", null);
                }
                succulent.setStatus(Status.UNAVAILABLE);
                updateSucculentInfo(succulent, request);
                succulentRepo.save(succulent);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }

            if (request.getQuantity() > 0) {
                succulent.setStatus(Status.AVAILABLE);
            }
        }

        updateSucculentInfo(succulent, request);
        succulentRepo.save(succulent);
        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
    }

    private void updateSucculentInfo(Succulent succulent, UpdateSucculentRequest request) {
        succulent.setPriceSell(request.getPriceSell());
        succulent.setQuantity(request.getQuantity());
        succulent.setImageUrl(request.getImageUrl());
    }

    @Override
    public ResponseEntity<ResponseObject> updateSucculentQuantity(UpdateSucculentRequest request) {
        if (succulentRepo.findById(request.getId()).isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy sen đá với id: " + request.getId(), null);
        }

        Succulent succulent = succulentRepo.findById(request.getId()).get();

        // Chỉ cập nhật quantity và status
        String error = validateSucculentQuantity(request, succulent);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        // Cập nhật quantity
        succulent.setQuantity(request.getQuantity());

        // Tự động cập nhật status dựa trên quantity
        if (request.getQuantity() > 0) {
            succulent.setStatus(Status.AVAILABLE);
        } else {
            succulent.setStatus(Status.OUT_OF_STOCK);
        }

        succulentRepo.save(succulent);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật số lượng sen đá thành công", buildSucculentDetail(succulent));
    }

    private Map<String, Object> buildSucculentDetail(Succulent succulent) {
        Map<String, Object> response = new HashMap<>();
        var species = succulent.getSpecies();
        response.put("id", succulent.getId());
        response.put("speciesId", species.getId());
        response.put("imageUrl", succulent.getImageUrl());
        response.put("speciesName", species.getSpeciesName());
        response.put("description", species.getDescription());
        response.put("quantity", succulent.getQuantity());
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

    private String validateUpdateSucculent(UpdateSucculentRequest request, Succulent current) {
        if (request.getPriceSell() == null || request.getPriceSell().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá bán lớn hơn 0";
        }
        if (request.getQuantity() < 0) {
            return "Cần nhập số lượng cây lớn hơn hoặc bằng 0";
        }
        String rawStatus = request.getStatus() == null ? "" : request.getStatus().trim();
        if (!Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus) && !Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && !Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái hàng không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: " + Status.AVAILABLE.getValue() + ", " + Status.OUT_OF_STOCK.getValue() + ", " + Status.UNAVAILABLE.getValue() + ".";
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

    private String validateSucculentQuantity(UpdateSucculentRequest request, Succulent current) {
        if (request.getQuantity() < 0) {
            return "Cần nhập số lượng cây lớn hơn hoặc bằng 0";
        }

        String rawStatus = request.getStatus() == null ? "" : request.getStatus().trim();
        if (!rawStatus.isEmpty() &&
                !Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus) &&
                !Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) &&
                !Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái hàng không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: " +
                    Status.AVAILABLE.getValue() + ", " + Status.OUT_OF_STOCK.getValue() + ", " +
                    Status.UNAVAILABLE.getValue() + ".";
        }

        // Edge case validation: Không thể chuyển sang AVAILABLE khi quantity = 0
        if (Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && request.getQuantity() == 0) {
            return "Không thể chuyển sang 'Đang còn hàng' khi số lượng = 0";
        }

        // Edge case validation: Không thể chuyển sang UNAVAILABLE khi quantity > 0
        if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus) && request.getQuantity() > 0) {
            return "Không thể chuyển sang 'Ngưng nhập hàng' khi số lượng > 0. Hãy xả kho về 0 trước";
        }

        return "";
    }

    // =========================== Accessory ========================== \\
    @Override
    public ResponseEntity<ResponseObject> createAccessory(CreateAccessoryRequest request) {
        String error = validateCreateAccessory(request, accessoryRepo);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }
        accessoryRepo.save(Accessory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(getAccessoryCategoryFromName(request.getCategory()))
                .priceSell(request.getPriceSell()) // Sử dụng priceSell trực tiếp
                .quantity(0)
                .status(Status.OUT_OF_STOCK) 
                .imageUrl(request.getImageUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        return ResponseBuilder.build(HttpStatus.OK, "Tạo catalog phụ kiện thành công", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getAccessories() {
        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách catalog phụ kiện thành công", buildListAccessories(accessoryRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))));
    }

    @Override
    public ResponseEntity<ResponseObject> updateAccessory(UpdateAccessoryRequest request) {

        String error = validateUpdateAccessory(request, accessoryRepo);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        if (accessoryRepo.findById(request.getId()).isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy mặt hàng với id: " + request.getId(), null);
        }

        Accessory accessory = accessoryRepo.findById(request.getId()).get();

        // UNAVAILABLE
        if (accessory.getStatus().equals(Status.UNAVAILABLE)) {
            //UNAVAILABLE TO AVAILABLE
            if (Status.AVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() == 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Chuyển sang 'Đang còn hàng' cần số lượng > 0.", null);
                }
                accessory.setStatus(Status.AVAILABLE);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }
            //UNAVAILABLE TO OUT-OF-STOCK
            if (Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                return ResponseBuilder.build(HttpStatus.CONFLICT, "Không thể chuyển trực tiếp từ 'Ngưng nhập hàng' sang 'Hết hàng'. Vui lòng chuyển sang 'Đang còn hàng' với số lượng mặt hàng lớn hơn 0. Sau đó, nếu cần, cập nhật số lượng về 0 để trở thành 'Hết hàng'.", null);
            }
            // NOT CHANGE STATUS
            return ResponseBuilder.build(HttpStatus.CONFLICT, "Với trạng thái Ngưng nhập hàng, chỉ được chuyển sang trạng thái 'Đang còn hàng' với số lượng lớn hơn 0 mới được cập nhật.", null);
        }
        // AVAILABLE
        if (accessory.getStatus().equals(Status.AVAILABLE)) {

            //AVAILABLE TO UNAVAILABLE
            if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Không thể 'Ngưng nhập hàng' khi số lượng > 0. Hãy xả kho về 0 trước.", null);
                }
                accessory.setStatus(Status.UNAVAILABLE);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }
            //AVAILABLE TO OUT-OF-STOCK
            if (Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Không thể 'Hết hàng' khi số lượng > 0. Hãy xả kho về 0 trước.", null);
                }
                accessory.setStatus(Status.OUT_OF_STOCK);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
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
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Chuyển sang 'Đang còn hàng' cần số lượng > 0.", null);
                }
                accessory.setStatus(Status.AVAILABLE);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }
            // OUT-OF-STOCK To UnAvailable
            if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(request.getStatus().trim())) {
                if (request.getQuantity() > 0) {
                    return ResponseBuilder.build(HttpStatus.CONFLICT, "Không thể 'Ngưng nhập hàng' khi số lượng > 0. ", null);
                }
                accessory.setStatus(Status.UNAVAILABLE);
                updateAccessoryInfo(accessory, request);
                accessoryRepo.save(accessory);
                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
            }
            // NOT CHANGE STATUS
            // Khi quantity >0 tự động tahyd đổi tranng thái
            if (request.getQuantity() > 0) {
                accessory.setStatus(Status.AVAILABLE);
            }
        }

        updateAccessoryInfo(accessory, request);
        accessoryRepo.save(accessory);
        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
    }

    @Override
    public ResponseEntity<ResponseObject> updateAccessoryQuantity(UpdateAccessoryRequest request) {
        if (accessoryRepo.findById(request.getId()).isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy phụ kiện với id: " + request.getId(), null);
        }

        Accessory accessory = accessoryRepo.findById(request.getId()).get();

        // Chỉ cập nhật quantity và status
        String error = validateAccessoryQuantity(request, accessory);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        // Cập nhật quantity
        accessory.setQuantity(request.getQuantity());

        // Tự động cập nhật status dựa trên quantity
        if (request.getQuantity() > 0) {
            accessory.setStatus(Status.AVAILABLE);
        } else {
            accessory.setStatus(Status.OUT_OF_STOCK);
        }

        accessory.setUpdatedAt(LocalDateTime.now());
        accessoryRepo.save(accessory);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật số lượng phụ kiện thành công", buildAccessoryDetail(accessory));
    }

    private String validateAccessoryQuantity(UpdateAccessoryRequest request, Accessory current) {
        if (request.getQuantity() < 0) {
            return "Cần nhập số lượng hàng lớn hơn hoặc bằng 0";
        }

        String rawStatus = request.getStatus() == null ? "" : request.getStatus().trim();
        if (!rawStatus.isEmpty() &&
                !Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus) &&
                !Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) &&
                !Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái hàng không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: " +
                    Status.AVAILABLE.getValue() + ", " + Status.OUT_OF_STOCK.getValue() + ", " +
                    Status.UNAVAILABLE.getValue() + ".";
        }

        // Edge case validation: Không thể chuyển sang AVAILABLE khi quantity = 0
        if (Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && request.getQuantity() == 0) {
            return "Không thể chuyển sang 'Đang còn hàng' khi số lượng = 0";
        }

        // Edge case validation: Không thể chuyển sang UNAVAILABLE khi quantity > 0
        if (Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus) && request.getQuantity() > 0) {
            return "Không thể chuyển sang 'Ngưng nhập hàng' khi số lượng > 0. Hãy xả kho về 0 trước";
        }

        return "";
    }

    private void updateAccessoryInfo(Accessory accessory, UpdateAccessoryRequest request) {
        accessory.setName(request.getName());
        accessory.setDescription(request.getDescription());
        accessory.setPriceSell(request.getPriceSell());
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

        if (request.getPriceSell() == null || request.getPriceSell().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá bán mặt hàng lớn hơn 0";
        }

        String rawCategory = request.getCategory() == null ? "" : request.getCategory().trim();

        if (!AccessoryCategory.SOIL.getDisplayName().equalsIgnoreCase(rawCategory) && !AccessoryCategory.PLANT_POT.getDisplayName().equalsIgnoreCase(rawCategory) && !AccessoryCategory.DECOR_ACCESSORY.getDisplayName().equalsIgnoreCase(rawCategory)) {
            return "Phân loại hàng không hợp lệ: '" + rawCategory + "'. Phân loại hàng hợp lệ: " + AccessoryCategory.SOIL.getDisplayName() + ", " + AccessoryCategory.PLANT_POT.getDisplayName() + ", " + AccessoryCategory.DECOR_ACCESSORY.getDisplayName() + ".";
        }

        String rawStatus = request.getStatus() == null ? "" : request.getStatus().trim();

        if (!Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus) && !Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && !Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái hàng không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: " + Status.AVAILABLE.getValue() + ", " + Status.OUT_OF_STOCK.getValue() + ", " + Status.UNAVAILABLE.getValue() + ".";
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

        if (request.getImageUrl() == null || request.getImageUrl().trim().isEmpty()) {
            return "Image URL is required";
        }
        if (!request.getImageUrl().matches("^(http|https)://.*$")) {
            return "Invalid Image URL format";
        }
        if (!request.getImageUrl().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            return "Image URL must end with a valid image file extension (jpg, jpeg, png, gif)";
        }

        // Bỏ check quantity > 0 vì tạo catalog không cần quantity

        if (request.getPriceSell() == null || request.getPriceSell().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá bán mặt hàng lớn hơn 0";
        }

        if (accessoryRepo.existsByNameIgnoreCase(request.getName())) {
            return "Mặt hàng có tên '" + request.getName() + "' đã được tạo trong hệ thống.";
        }

        String rawCategory = request.getCategory() == null ? "" : request.getCategory().trim();

        if (!AccessoryCategory.SOIL.getDisplayName().equalsIgnoreCase(rawCategory) && !AccessoryCategory.PLANT_POT.getDisplayName().equalsIgnoreCase(rawCategory) && !AccessoryCategory.DECOR_ACCESSORY.getDisplayName().equalsIgnoreCase(rawCategory)) {
            return "Phân loại hàng không hợp lệ: '" + rawCategory + "'. Phân loại hàng hợp lệ: " + AccessoryCategory.SOIL.getDisplayName() + ", " + AccessoryCategory.PLANT_POT.getDisplayName() + ", " + AccessoryCategory.DECOR_ACCESSORY.getDisplayName() + ".";
        }
        return "";
    }

    private Map<String, Object> buildAccessoryDetail(Accessory accessory) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", accessory.getId());
        response.put("name", accessory.getName());
        response.put("description", accessory.getDescription());
        response.put("priceSell", accessory.getPriceSell());
        response.put("quantity", accessory.getQuantity());
        response.put("category", accessory.getCategory().getDisplayName());
        response.put("status", accessory.getStatus());
        response.put("createdAt", accessory.getCreatedAt());
        response.put("updatedAt", accessory.getUpdatedAt());
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

    // =========================== Product ========================== \\
    @Override
    public ResponseEntity<ResponseObject> createProduct(ProductCreateRequest request) {
        String error = validateCreateProduct(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        // Tạo sản phẩm mới
        Product product = Product.builder().name(request.getName().trim()).description(request.getDescription().trim()).size(request.getSize().trim()).price(request.getPrice()).status(getStatusFromString(request.getStatus())).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        product = productRepo.save(product);

        return ResponseBuilder.build(HttpStatus.OK, "Tạo sản phẩm thành công", buildProductDetail(product));
    }

    @Override
    public ResponseEntity<ResponseObject> viewProduct() {
        List<Product> products = productRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<Map<String, Object>> data = products.stream().map(this::buildProductDetail).toList();

        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách sản phẩm thành công", data);
    }

    @Override
    public ResponseEntity<ResponseObject> updateProduct(ProductUpdateRequest request) {
        if (request.getId() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "ID sản phẩm là bắt buộc", null);
        }

        Optional<Product> productOpt = productRepo.findById(request.getId());
        if (productOpt.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + request.getId(), null);
        }

        String error = validateUpdateProduct(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Product product = productOpt.get();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription().trim());
        product.setSize(request.getSize().trim());
        product.setPrice(request.getPrice());
        product.setStatus(getStatusFromString(request.getStatus()));
        product.setUpdatedAt(LocalDateTime.now());

        productRepo.save(product);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật sản phẩm thành công", buildProductDetail(product));
    }

    private String validateCreateProduct(ProductCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên sản phẩm là bắt buộc";
        }
        if (request.getName().length() > 200) {
            return "Tên sản phẩm không được vượt quá 200 ký tự";
        }
        if (productRepo.existsByNameIgnoreCase(request.getName())) {
            return "Sản phẩm với tên '" + request.getName() + "' đã tồn tại";
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả sản phẩm là bắt buộc";
        }
        if (request.getDescription().length() > 1000) {
            return "Mô tả sản phẩm không được vượt quá 1000 ký tự";
        }
        if (request.getSize() == null || request.getSize().trim().isEmpty()) {
            return "Kích thước sản phẩm là bắt buộc";
        }
        if (request.getSize().length() > 50) {
            return "Kích thước sản phẩm không được vượt quá 50 ký tự";
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "Giá sản phẩm phải lớn hơn 0";
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            return "Trạng thái sản phẩm là bắt buộc";
        }
        String rawStatus = request.getStatus().trim();
        if (!Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && !Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus) && !Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái sản phẩm không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: " + Status.AVAILABLE.getValue() + ", " + Status.OUT_OF_STOCK.getValue() + ", " + Status.UNAVAILABLE.getValue() + ".";
        }
        return "";
    }

    private String validateUpdateProduct(ProductUpdateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên sản phẩm là bắt buộc";
        }
        if (request.getName().length() > 200) {
            return "Tên sản phẩm không được vượt quá 200 ký tự";
        }
        if (productRepo.existsByNameIgnoreCaseAndIdNot(request.getName(), request.getId())) {
            return "Sản phẩm với tên '" + request.getName() + "' đã tồn tại";
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả sản phẩm là bắt buộc";
        }
        if (request.getDescription().length() > 1000) {
            return "Mô tả sản phẩm không được vượt quá 1000 ký tự";
        }
        if (request.getSize() == null || request.getSize().trim().isEmpty()) {
            return "Kích thước sản phẩm là bắt buộc";
        }
        if (request.getSize().length() > 50) {
            return "Kích thước sản phẩm không được vượt quá 50 ký tự";
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "Giá sản phẩm phải lớn hơn 0";
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            return "Trạng thái sản phẩm là bắt buộc";
        }
        String rawStatus = request.getStatus().trim();
        if (!Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && !Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus) && !Status.UNAVAILABLE.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái sản phẩm không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: " + Status.AVAILABLE.getValue() + ", " + Status.OUT_OF_STOCK.getValue() + ", " + Status.UNAVAILABLE.getValue() + ".";
        }
        return "";
    }

    private Status getStatusFromString(String statusString) {
        if (statusString == null) return Status.OUT_OF_STOCK;
        String trimmed = statusString.trim();
        for (Status status : Status.values()) {
            if (status.getValue().equalsIgnoreCase(trimmed)) {
                return status;
            }
        }
        return Status.OUT_OF_STOCK;
    }

    private Map<String, Object> buildProductDetail(Product product) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", product.getId());
        response.put("name", product.getName());
        response.put("description", product.getDescription());
        response.put("size", product.getSize());
        response.put("price", product.getPrice());
        response.put("status", product.getStatus().getValue());
        response.put("createdAt", product.getCreatedAt());
        response.put("updatedAt", product.getUpdatedAt());

        // Thêm thông tin seller nếu có
        if (product.getSeller() != null) {
            Map<String, Object> sellerInfo = new HashMap<>();
            sellerInfo.put("id", product.getSeller().getId());
            sellerInfo.put("name", product.getSeller().getName());
            response.put("seller", sellerInfo);
        }

        return response;
    }

                        // =========================== BUYER ========================== \\
                      // =========================== WishList ========================== \\
    @Override
    public ResponseEntity<ResponseObject> addItemToWishList(AddWishListItemRequest item) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (productRepo.findById(item.getProductId()).isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm với id: " + item.getProductId(), null);
        }

        Product product = productRepo.findById(item.getProductId()).get();
        if (wishListItemRepo.findByProductIdAndWishlistId(item.getProductId(), account.getUser().getWishlist().getId()).isPresent()) {
            return ResponseBuilder.build(HttpStatus.CONFLICT, "Sản phẩm đã tồn tại trong wishlist", null);
        }
        wishListItemRepo.save(WishlistItem.builder().product(product).wishlist(account.getUser().getWishlist()).build());
        return ResponseBuilder.build(HttpStatus.OK, "Thêm sản phẩm vô wish list thành công", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getItemsFromWishList() {

        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseBuilder.build(HttpStatus.OK, "Hiển thị danh sách sản phẩm trong wish list thành công", buildListItemsFromWishList(wishListItemRepo.findAllByWishlist(account.getUser().getWishlist())));
    }

    @Override
    public ResponseEntity<ResponseObject> removeItemFromWishList(Integer id) {

        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<WishlistItem> item = wishListItemRepo.findByProductIdAndWishlistId(id, account.getUser().getWishlist().getId());
        if (item.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại trong wishlist", null);
        }
        wishListItemRepo.delete(item.get());
        return ResponseBuilder.build(HttpStatus.OK, "Xóa sản phẩm khỏi wishlist thành công", null);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> removeAllItemsFromWishList() {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        wishListItemRepo.removeAllByWishlist(account.getUser().getWishlist());
        return ResponseBuilder.build(HttpStatus.OK, "Xóa toàn bộ sản phẩm khỏi wishlist thành công", null);
    }


    private Map<String, Object> buildItemFromWishList(WishlistItem item) {
        Map<String, Object> result = new HashMap<>();
        if (item.getProduct() != null) {
            result.put("productId", item.getProduct().getId());
            result.put("name", item.getProduct().getName());
            result.put("description", item.getProduct().getDescription());
            result.put("price", item.getProduct().getPrice());
            result.put("status", item.getProduct().getStatus());
        }
        return result;
    }

    private List<Map<String, Object>> buildListItemsFromWishList(List<WishlistItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (WishlistItem item : items) {
            if (!buildItemFromWishList(item).isEmpty()) {
                result.add(buildItemFromWishList(item));
            }
        }
        return result;
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
