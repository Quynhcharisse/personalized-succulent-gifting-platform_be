package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.AddWishListItemRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateProductImageRequest;
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
import com.exe201.group1.psgp_be.models.ProductAccessory;
import com.exe201.group1.psgp_be.models.ProductImage;
import com.exe201.group1.psgp_be.models.ProductSucculent;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.models.SucculentSpecies;
import com.exe201.group1.psgp_be.models.Supplier;
import com.exe201.group1.psgp_be.models.WishlistItem;
import com.exe201.group1.psgp_be.repositories.AccessoryRepo;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.ProductAccessoryRepo;
import com.exe201.group1.psgp_be.repositories.ProductImageRepo;
import com.exe201.group1.psgp_be.repositories.ProductRepo;
import com.exe201.group1.psgp_be.repositories.ProductSucculentRepo;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProductImageRepo productImageRepo;
    private final ProductSucculentRepo productSucculentRepo;
    private final ProductAccessoryRepo productAccessoryRepo;

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
    @Transactional
    public ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền tạo sen đá", null);
        }

        String error = validateCreateSucculent(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }
        SucculentSpecies species = succulentSpeciesRepo.findBySpeciesNameIgnoreCase(request.getSpeciesName()).orElse(null);

        if (species != null) {
            species.setDescription(request.getDescription());
            species.setElements(request.getFengShuiList() == null ? new HashSet<>() : new HashSet<>(request.getFengShuiList()));
            species.setZodiacs(request.getZodiacList() == null ? new HashSet<>() : new HashSet<>(request.getZodiacList()));
        } else {
            species = SucculentSpecies.builder().speciesName(request.getSpeciesName()).description(request.getDescription()).elements(request.getFengShuiList() == null ? new HashSet<>() : new HashSet<>(request.getFengShuiList())).zodiacs(request.getZodiacList() == null ? new HashSet<>() : new HashSet<>(request.getZodiacList())).build();
        }

        species = succulentSpeciesRepo.save(species);

        for (SizeDetailRequest size : request.getSizeDetailRequests()) {
            Status status = size.getQuantity() > 0 ? Status.AVAILABLE : Status.OUT_OF_STOCK;

            Succulent variant = Succulent.builder()
                    .species(species)
                    .size(Size.fromDisplayName(size.getName()))
                    .priceSell(size.getPriceSell())
                    .quantity(size.getQuantity())
                    .status(status)
                    .imageUrl(request.getImageUrl())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
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
            if (Size.fromDisplayName(size.getName()) == null) {
                return "Kích thước không hợp lệ: " + size.getName() + " không tồn tại trong hệ thống";
            }
            if (size.getPriceSell() == null || size.getPriceSell().compareTo(BigDecimal.ZERO) <= 0) {
                return "Cần nhập giá bán " + request.getSpeciesName() + " cho kích thước " + size.getName() + " phải lớn hơn 0";
            }
            if (size.getQuantity() < 0) {
                return "Số lượng " + request.getSpeciesName() + " cho kích thước " + size.getName() + " phải lớn hơn hoặc bằng 0";
            }
            if (succulentRepo.existsBySpecies_SpeciesNameIgnoreCaseAndSize(request.getSpeciesName(), Size.fromDisplayName(size.getName()))) {
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

    @Override
    public ResponseEntity<ResponseObject> viewSucculentList(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền xem sen đá", null);
        }


        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách catalog sen đá thành công", buildListSucculent(succulentRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))));
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
        response.put("createdAt", succulent.getCreatedAt());
        response.put("updatedAt", succulent.getUpdatedAt());
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

    @Override
    public ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Admin mới có quyền thay đổi sen đá", null);
        }


        if (succulentRepo.findById(request.getId()).isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy mặt hàng ", null);
        }

        Succulent succulent = succulentRepo.findById(request.getId()).get();

        String error = validateUpdateSucculent(request, succulent);

        if (!error.isBlank()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        // Cập nhật thông tin succulent
        updateSucculentInfo(succulent, request);

        // Tự động cập nhật status dựa trên quantity
        if (request.getQuantity() > 0) {
            succulent.setStatus(Status.AVAILABLE);
        } else {
            succulent.setStatus(Status.OUT_OF_STOCK);
        }

        succulent.setUpdatedAt(LocalDateTime.now());
        succulentRepo.save(succulent);
        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
    }

    private void updateSucculentInfo(Succulent succulent, UpdateSucculentRequest request) {
        succulent.setPriceSell(request.getPriceSell());
        succulent.setQuantity(request.getQuantity());
        succulent.setImageUrl(request.getImageUrl());
        // Cập nhật thông tin species
        SucculentSpecies species = succulent.getSpecies();
        species.setSpeciesName(request.getName());
        species.setDescription(request.getDescription());
        succulentSpeciesRepo.save(species);
    }

    private String validateUpdateSucculent(UpdateSucculentRequest request, Succulent current) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return "Tên loài là bắt buộc";
        }
        if (request.getName().length() > 100) {
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
        if (request.getPriceSell() == null || request.getPriceSell().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cần nhập giá bán lớn hơn 0";
        }
        if (request.getQuantity() < 0) {
            return "Cần nhập số lượng cây lớn hơn hoặc bằng 0";
        }

        // Check duplicate species + size khi update (nếu có thay đổi)
        if (succulentRepo.existsBySpecies_SpeciesNameIgnoreCaseAndSizeAndIdNot(current.getSpecies().getSpeciesName(), current.getSize(), current.getId())) {
            return "Loài " + current.getSpecies().getSpeciesName() + " với kích thước " + current.getSize() + " đã được tạo trong hệ thống";
        }
        return "";
    }


    // =========================== Accessory ========================== \\
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createAccessory(CreateAccessoryRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Seller mới có quyền tạo linh kiện sen đá", null);
        }

        String error = validateCreateAccessory(request, accessoryRepo);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        for (CreateAccessoryRequest.Accessory accessory : request.getAccessories()) {
            Status status = accessory.getQuantity() > 0 ? Status.AVAILABLE : Status.OUT_OF_STOCK;

            accessoryRepo.save(Accessory.builder()
                    .name(accessory.getName())
                    .description(accessory.getDescription())
                    .category(AccessoryCategory.getByDisplayName(accessory.getCategory()))
                    .priceSell(accessory.getPriceSell())
                    .quantity(accessory.getQuantity())
                    .status(status)
                    .imageUrl(accessory.getImageUrl())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
        return ResponseBuilder.build(HttpStatus.OK, "Tạo catalog phụ kiện thành công", null);
    }


    private String validateCreateAccessory(CreateAccessoryRequest request, AccessoryRepo accessoryRepo) {

        for (CreateAccessoryRequest.Accessory accessory : request.getAccessories()) {
            if (accessory.getName() == null || accessory.getName().trim().isEmpty()) {
                return "Tên mặt hàng là bắt buộc";
            }

            if (accessory.getName().length() > 100) {
                return "Tên mặt hàng không được vượt quá 100 ký tự";
            }
            if (accessory.getDescription() == null || accessory.getDescription().trim().isEmpty()) {
                return "Mô tả là bắt buộc";
            }

            if (accessory.getDescription().length() > 300) {
                return "Mô tả không được vượt quá 300 ký tự";
            }

            if (accessory.getImageUrl() == null || accessory.getImageUrl().trim().isEmpty()) {
                return "Image URL is required";
            }
            if (!accessory.getImageUrl().matches("^(http|https)://.*$")) {
                return "Invalid Image URL format";
            }
            if (!accessory.getImageUrl().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                return "Image URL must end with a valid image file extension (jpg, jpeg, png, gif)";
            }

            if (accessory.getPriceSell() <= 0) {
                return "Cần nhập giá bán mặt hàng lớn hơn 0";
            }

            if (accessory.getQuantity() < 0) {
                return "Số lượng mặt hàng phải lớn hơn hoặc bằng 0";
            }

            if (accessory.getWeight() < 0) {
                return "Cân nặng mặt hàng phải lớn hơn hoặc bằng 0";
            }

            if (accessoryRepo.existsByNameIgnoreCase(accessory.getName())) {
                return "Mặt hàng có tên '" + accessory.getName() + "' đã được tạo trong hệ thống.";
            }

            String rawCategory = accessory.getCategory() == null ? "" : accessory.getCategory().trim();

            if (!AccessoryCategory.SOIL.getDisplayName().equalsIgnoreCase(rawCategory) && !AccessoryCategory.PLANT_POT.getDisplayName().equalsIgnoreCase(rawCategory) && !AccessoryCategory.DECOR_ACCESSORY.getDisplayName().equalsIgnoreCase(rawCategory)) {
                return "Phân loại hàng không hợp lệ: '" + rawCategory + "'. Phân loại hàng hợp lệ: " + AccessoryCategory.SOIL.getDisplayName() + ", " + AccessoryCategory.PLANT_POT.getDisplayName() + ", " + AccessoryCategory.DECOR_ACCESSORY.getDisplayName() + ".";
            }
        }
        return "";
    }


    @Override
    public ResponseEntity<ResponseObject> getAccessories(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Seller mới có xem tạo linh kiện sen đá", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách catalog phụ kiện thành công", buildListAccessories(accessoryRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))));
    }


    private Map<String, Object> buildAccessoryDetail(Accessory accessory) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", accessory.getId());
        response.put("name", accessory.getName());
        response.put("description", accessory.getDescription());
        response.put("priceSell", accessory.getPriceSell());
        response.put("imageUrl", accessory.getImageUrl());
        response.put("quantity", accessory.getQuantity());
        response.put("category", accessory.getCategory().getDisplayName());
        response.put("status", accessory.getStatus().getValue()); // Sửa để consistent với succulent
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

    @Override
    public ResponseEntity<ResponseObject> updateAccessory(UpdateAccessoryRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Seller mới có quyền thay đổi linh kiện sen đá", null);
        }


        String error = validateUpdateAccessory(request, accessoryRepo);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        if (accessoryRepo.findById(request.getId()).isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy mặt hàng", null);
        }

        Accessory accessory = accessoryRepo.findById(request.getId()).get();

        // Cập nhật thông tin accessory
        updateAccessoryInfo(accessory, request);

        // Tự động cập nhật status dựa trên quantity
        if (request.getQuantity() > 0) {
            accessory.setStatus(Status.AVAILABLE);
        } else {
            accessory.setStatus(Status.OUT_OF_STOCK);
        }

        accessory.setUpdatedAt(LocalDateTime.now());
        accessoryRepo.save(accessory);
        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
    }

    private void updateAccessoryInfo(Accessory accessory, UpdateAccessoryRequest request) {
        accessory.setName(request.getName());
        accessory.setDescription(request.getDescription());
        accessory.setPriceSell(request.getPriceSell());
        accessory.setWeight(request.getWeight());
        accessory.setQuantity(request.getQuantity());
        accessory.setCategory(AccessoryCategory.getByDisplayName(request.getCategory()));
        accessory.setImageUrl(request.getImageUrl());
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

        if (request.getImageUrl() == null || request.getImageUrl().trim().isEmpty()) {
            return "Image URL is required";
        }
        if (!request.getImageUrl().matches("^(http|https)://.*$")) {
            return "Invalid Image URL format";
        }
        if (!request.getImageUrl().matches(".*\\.(jpg|jpeg|png|gif)$")) {
            return "Image URL must end with a valid image file extension (jpg, jpeg, png, gif)";
        }

        if (request.getQuantity() < 0) {
            return "Cần nhập số lượng hàng lớn hơn hoặc bằng 0";
        }

        if (request.getPriceSell() <= 0) {
            return "Cần nhập giá bán mặt hàng lớn hơn 0";
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            return "Phân loại hàng là bắt buộc";
        }
        String rawCategory = request.getCategory().trim();

        if (!AccessoryCategory.SOIL.getDisplayName().equalsIgnoreCase(rawCategory) && !AccessoryCategory.PLANT_POT.getDisplayName().equalsIgnoreCase(rawCategory) && !AccessoryCategory.DECOR_ACCESSORY.getDisplayName().equalsIgnoreCase(rawCategory)) {
            return "Phân loại hàng không hợp lệ: '" + rawCategory + "'. Phân loại hàng hợp lệ: " + AccessoryCategory.SOIL.getDisplayName() + ", " + AccessoryCategory.PLANT_POT.getDisplayName() + ", " + AccessoryCategory.DECOR_ACCESSORY.getDisplayName() + ".";
        }

        if (accessoryRepo.existsByNameIgnoreCaseAndIdNot(request.getName(), request.getId())) {
            return "Mặt hàng '" + request.getName() + "' đã được tạo trong hệ thống";
        }

        return "";
    }

    // =========================== Product ========================== \\
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createProduct(ProductCreateRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Seller mới có quyền tạo sản phẩm", null);
        }

        String error = validateCreateProduct(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        // Tạo sản phẩm mới
        Product product = productRepo.save(
                Product.builder()
                        .seller(account.getUser())
                        .name(request.getName().trim())
                        .description(request.getDescription().trim())
                        .size(Size.fromDisplayName(request.getSize().trim()))
                        .price(request.getPrice())
                        .quantity(request.getQuantityInStock())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status(Status.getByValue(request.getStatus()))
                        .build());

        if (request.getSucculentIds() != null && !request.getSucculentIds().isEmpty()) {
            for (Integer succulentId : request.getSucculentIds()) {
                Optional<Succulent> succulentOpt = succulentRepo.findById(succulentId);
                succulentOpt.ifPresent(succulent -> productSucculentRepo.save(
                        ProductSucculent.builder()
                                .product(product)
                                .succulent(succulent)
                                .build()));
            }
        }

        if (request.getAccessoryIds() != null && !request.getAccessoryIds().isEmpty()) {
            for (Integer accessoryId : request.getAccessoryIds()) {
                Optional<Accessory> accessoryOpt = accessoryRepo.findById(accessoryId);
                accessoryOpt.ifPresent(accessory -> productAccessoryRepo.save(ProductAccessory.builder()
                        .product(product)
                        .accessory(accessory)
                        .build()));
            }
        }

        // Tạo các hình ảnh cho sản phẩm
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (CreateProductImageRequest requestImage : request.getImages()) {

                LocalDateTime now = LocalDateTime.now();

                ProductImage productImage = ProductImage.builder()
                        .imageUrl(requestImage.getImageUrl())
                        .altText(requestImage.getAltText())
                        .isPrimary(requestImage.isPrimary())
                        .displayOrder(requestImage.getDisplayOrder() != null ? requestImage.getDisplayOrder() : request.getImages().indexOf(requestImage))
                        .product(product)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

                productImageRepo.save(productImage);
            }
        }

        return ResponseBuilder.build(HttpStatus.CREATED, "Tạo sản phẩm thành công", buildProductDetail(product));
    }

    private String validateProductImageRequest(CreateProductImageRequest imageRequest, int imageIndex) {
        if (imageRequest.getImageUrl() == null || imageRequest.getImageUrl().trim().isEmpty()) {
            return "URL hình ảnh thứ " + imageIndex + " là bắt buộc";
        }
        if (!imageRequest.getImageUrl().matches("^(http|https)://.*$")) {
            return "URL hình ảnh thứ " + imageIndex + " không đúng định dạng";
        }
        if (!imageRequest.getImageUrl().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
            return "Hình ảnh thứ " + imageIndex + " phải có định dạng jpg, jpeg, png, gif hoặc webp";
        }
        if (imageRequest.getAltText() != null && imageRequest.getAltText().length() > 255) {
            return "Mô tả hình ảnh thứ " + imageIndex + " không được vượt quá 255 ký tự";
        }
        return "";
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
        if (Size.fromDisplayName(request.getSize().trim()) == null) {
            return "Kích thước sản phẩm không hợp lệ";
        }
        if (request.getPrice() <= 0) {
            return "Giá sản phẩm phải lớn hơn 0";
        }
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            return "Trạng thái sản phẩm là bắt buộc";
        }
        String rawStatus = request.getStatus().trim();
        if (!Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) && !Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus)) {
            return "Trạng thái sản phẩm không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: " + Status.AVAILABLE.getValue() + ", " + Status.OUT_OF_STOCK.getValue() + ".";
        }

        // Validate succulents và accessories
        if ((request.getSucculentIds() == null || request.getSucculentIds().isEmpty()) &&
                (request.getAccessoryIds() == null || request.getAccessoryIds().isEmpty())) {
            return "Sản phẩm phải chứa ít nhất một sen đá hoặc phụ kiện";
        }

        // Validate succulent IDs
        if (request.getSucculentIds() != null && !request.getSucculentIds().isEmpty()) {
            for (Integer succulentId : request.getSucculentIds()) {
                if (!succulentRepo.existsById(succulentId)) {
                    return "Không tìm thấy sen đá";
                }
                // Kiểm tra sen đá còn hàng
                Optional<Succulent> succulentOpt = succulentRepo.findById(succulentId);
                if (succulentOpt.isPresent() && succulentOpt.get().getStatus() == Status.OUT_OF_STOCK) {
                    return "Sen đá" + " đã hết hàng, không thể tạo sản phẩm";
                }
            }
        }

        // Validate accessory IDs
        if (request.getAccessoryIds() != null && !request.getAccessoryIds().isEmpty()) {
            for (Integer accessoryId : request.getAccessoryIds()) {
                if (!accessoryRepo.existsById(accessoryId)) {
                    return "Không tìm thấy phụ kiện";
                }
                // Kiểm tra phụ kiện còn hàng
                Optional<Accessory> accessoryOpt = accessoryRepo.findById(accessoryId);
                if (accessoryOpt.isPresent() && accessoryOpt.get().getStatus() == Status.OUT_OF_STOCK) {
                    return "Phụ kiện" + " đã hết hàng, không thể tạo sản phẩm";
                }
            }
        }

        // Validate images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (int i = 0; i < request.getImages().size(); i++) {
                CreateProductImageRequest imageRequest = request.getImages().get(i);
                String imageError = validateProductImageRequest(imageRequest, i + 1);
                if (!imageError.isEmpty()) {
                    return imageError;
                }
            }
        }

        return "";
    }


    @Override
    public ResponseEntity<ResponseObject> viewProduct(HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Seller mới có quyền xem sản phẩm", null);
        }

        List<Product> products = productRepo.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .filter(p -> p.getStatus() != Status.INACTIVE)
                .toList();

        List<Map<String, Object>> data = products.stream().map(this::buildProductDetail).toList();

        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách sản phẩm thành công", data);
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

        // Thêm thông tin sen đá trong sản phẩm
        List<ProductSucculent> productSucculents = productSucculentRepo.findByProductId(product.getId());
        List<Map<String, Object>> succulentList = new ArrayList<>();
        for (ProductSucculent ps : productSucculents) {
            Succulent succulent = ps.getSucculent();
            Map<String, Object> succulentInfo = new HashMap<>();
            succulentInfo.put("id", succulent.getId());
            succulentInfo.put("speciesName", succulent.getSpecies().getSpeciesName());
            succulentInfo.put("size", succulent.getSize().getDisplayName());
            succulentInfo.put("priceSell", succulent.getPriceSell());
            succulentInfo.put("quantity", succulent.getQuantity());
            succulentInfo.put("status", succulent.getStatus().getValue());
            succulentInfo.put("imageUrl", succulent.getImageUrl());
            succulentList.add(succulentInfo);
        }
        response.put("succulents", succulentList);

        // Thêm thông tin phụ kiện trong sản phẩm
        List<ProductAccessory> productAccessories = productAccessoryRepo.findByProductId(product.getId());
        List<Map<String, Object>> accessoryList = new ArrayList<>();
        for (ProductAccessory pa : productAccessories) {
            Accessory accessory = pa.getAccessory();
            Map<String, Object> accessoryInfo = new HashMap<>();
            accessoryInfo.put("id", accessory.getId());
            accessoryInfo.put("name", accessory.getName());
            accessoryInfo.put("category", accessory.getCategory().getDisplayName());
            accessoryInfo.put("priceSell", accessory.getPriceSell());
            accessoryInfo.put("quantity", accessory.getQuantity());
            accessoryInfo.put("status", accessory.getStatus().getValue());
            accessoryInfo.put("imageUrl", accessory.getImageUrl());
            accessoryList.add(accessoryInfo);
        }
        response.put("accessories", accessoryList);

        // Thêm thông tin hình ảnh
        List<ProductImage> images = productImageRepo.findByProductIdOrderByDisplayOrderAsc(product.getId());
        List<Map<String, Object>> imageList = new ArrayList<>();
        for (ProductImage image : images) {
            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("id", image.getId());
            imageInfo.put("imageUrl", image.getImageUrl());
            imageInfo.put("altText", image.getAltText());
            imageInfo.put("isPrimary", image.getIsPrimary());
            imageInfo.put("displayOrder", image.getDisplayOrder());
            imageList.add(imageInfo);
        }
        response.put("images", imageList);

        return response;
    }


    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateProduct(ProductUpdateRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Seller mới có quyền thay đổi sản phẩm", null);
        }

        if (request.getId() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "ID sản phẩm là bắt buộc", null);
        }

        Optional<Product> productOpt = productRepo.findById(request.getId());
        if (productOpt.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm", null);
        }

        // Chỉ validate những field được gửi lên
        String error = validateUpdateProduct(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        Product product = productOpt.get();

        // 1. Update basic info - chỉ update các field không null
        if (request.getName() != null) {
            product.setName(request.getName().trim());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription().trim());
        }

        if (request.getSize() != null) {
            product.setSize(Size.fromDisplayName(request.getSize()));
        }

        if (request.getPrice() > 0) {
            product.setPrice(request.getPrice());
        }

        if (request.getStatus() != null) {
            product.setStatus(Status.getByValue(request.getStatus()));
        }

        product.setUpdatedAt(LocalDateTime.now());

        // 2. Update succulents (chỉ khi client gửi succulentIds)
        if (request.getSucculentIds() != null) {
            // Xóa quan hệ cũ
            productSucculentRepo.deleteByProductId(product.getId());

            // Tạo quan hệ mới với sen đá (nếu có)
            if (!request.getSucculentIds().isEmpty()) {
                for (Integer succulentId : request.getSucculentIds()) {
                    Optional<Succulent> succulentOpt = succulentRepo.findById(succulentId);
                    if (succulentOpt.isPresent()) {
                        ProductSucculent productSucculent = ProductSucculent.builder()
                                .product(product)
                                .succulent(succulentOpt.get())
                                .build();
                        productSucculentRepo.save(productSucculent);
                    }
                }
            }
        }

        // 3. Update accessories (chỉ khi client gửi accessoryIds)
        if (request.getAccessoryIds() != null) {
            // Xóa quan hệ cũ
            productAccessoryRepo.deleteByProductId(product.getId());

            // Tạo quan hệ mới với phụ kiện (nếu có)
            if (!request.getAccessoryIds().isEmpty()) {
                for (Integer accessoryId : request.getAccessoryIds()) {
                    Optional<Accessory> accessoryOpt = accessoryRepo.findById(accessoryId);
                    if (accessoryOpt.isPresent()) {
                        ProductAccessory productAccessory = ProductAccessory.builder()
                                .product(product)
                                .accessory(accessoryOpt.get())
                                .build();
                        productAccessoryRepo.save(productAccessory);
                    }
                }
            }
        }

        // 4. Update images (chỉ khi client gửi images)
        if (request.getImages() != null) {
            // Xóa hình ảnh cũ
            productImageRepo.deleteByProductId(product.getId());

            // Tạo hình ảnh mới (nếu có)
            if (!request.getImages().isEmpty()) {
                for (int i = 0; i < request.getImages().size(); i++) {
                    CreateProductImageRequest imageRequest = request.getImages().get(i);

                    ProductImage productImage = ProductImage.builder()
                            .imageUrl(imageRequest.getImageUrl())
                            .altText(imageRequest.getAltText())
                            .isPrimary(imageRequest.isPrimary())
                            .displayOrder(imageRequest.getDisplayOrder() != null ? imageRequest.getDisplayOrder() : i + 1)
                            .product(product)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    productImageRepo.save(productImage);
                }
            }
        }

        productRepo.save(product);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật sản phẩm thành công", buildProductDetail(product));
    }

    private String validateUpdateProduct(ProductUpdateRequest request) {
        // Chỉ validate các field được gửi lên
        if (request.getName() != null) {
            if (request.getName().trim().isEmpty()) {
                return "Tên sản phẩm không được để trống";
            }
            if (request.getName().length() > 200) {
                return "Tên sản phẩm không được vượt quá 200 ký tự";
            }
            if (productRepo.existsByNameIgnoreCaseAndIdNot(request.getName(), request.getId())) {
                return "Sản phẩm với tên '" + request.getName() + "' đã tồn tại";
            }
        }

        if (request.getDescription() != null) {
            if (request.getDescription().trim().isEmpty()) {
                return "Mô tả sản phẩm không được để trống";
            }
            if (request.getDescription().length() > 1000) {
                return "Mô tả sản phẩm không được vượt quá 1000 ký tự";
            }
        }

        if (request.getSize() != null) {
            if (request.getSize().trim().isEmpty()) {
                return "Kích thước sản phẩm không được để trống";
            }
            if (request.getSize().length() > 50) {
                return "Kích thước sản phẩm không được vượt quá 50 ký tự";
            }
            if (Size.fromDisplayName(request.getSize().trim()) == null) {
                return "Kích thước sản phẩm không hợp lệ";
            }
        }

        if (request.getPrice() <= 0) {
            return "Giá sản phẩm phải lớn hơn 0";
        }

        if (request.getStatus() != null) {
            if (request.getStatus().trim().isEmpty()) {
                return "Trạng thái sản phẩm không được để trống";
            }
            String rawStatus = request.getStatus().trim();
            if (!Status.AVAILABLE.getValue().equalsIgnoreCase(rawStatus) &&
                    !Status.OUT_OF_STOCK.getValue().equalsIgnoreCase(rawStatus) &&
                    !Status.INACTIVE.getValue().equalsIgnoreCase(rawStatus)) {
                return "Trạng thái sản phẩm không hợp lệ: '" + rawStatus + "'. Trạng thái hợp lệ: " +
                        Status.AVAILABLE.getValue() + ", " + Status.OUT_OF_STOCK.getValue() + ", " +
                        Status.INACTIVE.getValue() + ".";
            }
        }

        // Validate components nếu có
        if (request.getSucculentIds() != null || request.getAccessoryIds() != null) {
            // Validate ít nhất phải có một component
            if ((request.getSucculentIds() == null || request.getSucculentIds().isEmpty()) &&
                    (request.getAccessoryIds() == null || request.getAccessoryIds().isEmpty())) {
                return "Sản phẩm phải chứa ít nhất một sen đá hoặc phụ kiện";
            }

            // Validate succulent IDs
            if (request.getSucculentIds() != null && !request.getSucculentIds().isEmpty()) {
                for (Integer succulentId : request.getSucculentIds()) {
                    if (!succulentRepo.existsById(succulentId)) {
                        return "Không tìm thấy sen đá";
                    }
                    // Kiểm tra sen đá còn hàng
                    Optional<Succulent> succulentOpt = succulentRepo.findById(succulentId);
                    if (succulentOpt.isPresent() && succulentOpt.get().getStatus() == Status.OUT_OF_STOCK) {
                        return "Sen đá" + " đã hết hàng, không thể cập nhật sản phẩm";
                    }
                }
            }

            // Validate accessory IDs
            if (request.getAccessoryIds() != null && !request.getAccessoryIds().isEmpty()) {
                for (Integer accessoryId : request.getAccessoryIds()) {
                    if (!accessoryRepo.existsById(accessoryId)) {
                        return "Không tìm thấy phụ kiện";
                    }
                    // Kiểm tra phụ kiện còn hàng
                    Optional<Accessory> accessoryOpt = accessoryRepo.findById(accessoryId);
                    if (accessoryOpt.isPresent() && accessoryOpt.get().getStatus() == Status.OUT_OF_STOCK) {
                        return "Phụ kiện" + " đã hết hàng, không thể cập nhật sản phẩm";
                    }
                }
            }
        }

        // Validate images nếu có
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (int i = 0; i < request.getImages().size(); i++) {
                CreateProductImageRequest imageRequest = request.getImages().get(i);
                String imageError = validateProductImageRequest(imageRequest, i + 1);
                if (!imageError.isEmpty()) {
                    return imageError;
                }
            }
        }

        return "";
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> deleteProduct(int id, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (account.getRole() == null || account.getRole() != Role.SELLER) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Chỉ có Seller mới có quyền cập nhật trạng thái sản phẩm", null);
        }

        Product product = productRepo.findById(id).orElse(null);
        if (product == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm", null);
        }

        if (product.getQuantity() > 0) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Chỉ có thể xóa sản phẩm khi số lượng bằng 0", null);
        } else {
            product.setStatus(Status.INACTIVE);
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepo.save(product);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật thành công", null);
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
