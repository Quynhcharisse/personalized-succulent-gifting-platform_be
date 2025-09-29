package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.AddWishListItemRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierStatusRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Role;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.AppConfig;
import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.models.SucculentSpecies;
import com.exe201.group1.psgp_be.models.Supplier;
import com.exe201.group1.psgp_be.models.WishlistItem;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.AppConfigRepo;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    SucculentRepo succulentRepo;
    SucculentSpeciesRepo succulentSpeciesRepo;
    SupplierRepo supplierRepo;
    ProductRepo productRepo;
    JWTService jwtService;
    AccountRepo accountRepo;
    WishListItemRepo wishListItemRepo;
    AppConfigRepo appConfigRepo;


    // =========================== Supplier ========================== \\
    @Override
    public ResponseEntity<ResponseObject> createSupplier(CreateSupplierRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
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

        Succulent succulent = succulentRepo.save(
                Succulent.builder()
                        .species(species)
                        .size(null)
                        .status(null)
                        .imageUrl(request.getImageUrl())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());

//        {
//                "small": {
//                     "min": 0.1,
//                     "max": 0.9,
//                     "price": 1000,
//                     "quantity": 12,
//                     "status": "AVAILABLE"
//                }
//        }

        Status mainStatus = Status.OUT_OF_STOCK;

        Map<String, Object> sizeRangeMap = new HashMap<>();

        for (CreateSucculentRequest.Size size : request.getSizeList()) {
            if (size.getQuantity() > 0) {
                mainStatus = Status.AVAILABLE;
            }
            Map<String, Object> sizeMap = new HashMap<>();
            sizeMap.put("min", size.getMinDiameter());
            sizeMap.put("max", size.getMaxDiameter());
            sizeMap.put("price", size.getPrice());
            sizeMap.put("quantity", size.getQuantity());
            sizeMap.put("status", size.getQuantity() > 0 ? Status.AVAILABLE.name() : Status.OUT_OF_STOCK.name());

            sizeRangeMap.put(size.getSizeName().toLowerCase(), sizeMap);
        }

        succulent.setSize(sizeRangeMap);
        succulent.setStatus(mainStatus);
        succulentRepo.save(succulent);

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

        if (!validateSizeList(request.getSizeList()).isEmpty()) {
            return validateSizeList(request.getSizeList());
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

    private String validateSizeList(List<CreateSucculentRequest.Size> sizeList) {
        if (sizeList == null || sizeList.isEmpty()) {
            return "Vui lòng chọn ít nhất một kích thước";
        }
        if (sizeList.size() > 5) {
            return "Hệ thống chỉ có tối đa 5 kích thước";
        }

        // Sử dụng Set để kiểm tra tên kích thước trùng lặp
        Set<String> uniqueSizeNames = new HashSet<>();

        for (CreateSucculentRequest.Size size : sizeList) {
            if (size.getSizeName() == null || size.getSizeName().trim().isEmpty()) {
                return "Tên kích thước là bắt buộc";
            }

            // Chuyển tên kích thước sang chữ thường để kiểm tra không phân biệt chữ hoa/thường
            String normalizedSizeName = size.getSizeName().trim().toLowerCase();

            // Kiểm tra xem tên kích thước đã tồn tại trong Set chưa
            if (!uniqueSizeNames.add(normalizedSizeName)) {
                return "Kích thước '" + size.getSizeName() + "' đã bị trùng lặp. Vui lòng sử dụng tên khác.";
            }

            if (size.getMaxDiameter() < size.getMinDiameter()) {
                return "Đường kính tối đa phải lớn hơn hoặc bằng đường kính tối thiểu";
            }

            if (size.getMaxDiameter() <= 0 || size.getMinDiameter() <= 0) {
                return "Cần nhập đường kính lớn hơn 0";
            }

            if (size.getPrice() <= 0) {
                return "Cần nhập giá bán lớn hơn 0";
            }
            if (size.getQuantity() <= 0) {
                return "Cần nhập số lượng cây lớn hơn 0";
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
        Map<String, Object> size = (Map<String, Object>) succulent.getSize();
        Map<String, Object> sizeResponse = new HashMap<>();

        for (String key : size.keySet()) {
            Map<String, Object> sizeDetail = new HashMap<>();

            Map<String, Object> value = (Map<String, Object>) size.get(key);
            sizeDetail.put("minDiameter", value.get("min"));
            sizeDetail.put("maxDiameter", value.get("max"));
            sizeDetail.put("price", value.get("price"));
            sizeDetail.put("quantity", value.get("quantity"));
            sizeDetail.put("status", Status.valueOf(value.get("status").toString()).getValue());

            sizeResponse.put(key, sizeDetail);
        }

        Map<String, Object> response = new HashMap<>();
        SucculentSpecies species = succulent.getSpecies();
        response.put("id", succulent.getId());
        response.put("speciesId", species.getId());
        response.put("imageUrl", succulent.getImageUrl());
        response.put("speciesName", species.getSpeciesName());
        response.put("description", species.getDescription());
        response.put("size", sizeResponse);
        response.put("status", succulent.getStatus().getValue());
        response.put("createdAt", succulent.getCreatedAt());
        response.put("updatedAt", succulent.getUpdatedAt());
        response.put("fengShuiElements", species.getElements());
        response.put("zodiacs", species.getZodiacs());
        return response;
    }

    private List<Map<String, Object>> buildListSucculent(List<Succulent> succulents) {
        return succulents.stream().map(this::buildSucculentDetail).toList();
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);

        if (account == null) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Tài khoản không hợp lệ", null);
        }

        if (succulentRepo.findById(request.getId()).isEmpty()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy mặt hàng ", null);
        }

        Succulent succulent = succulentRepo.findById(request.getId()).orElse(null);
        if (succulent == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy mặt hàng", null);
        }

        String error = validateUpdateSucculent(request, succulent);

        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        SucculentSpecies species = succulent.getSpecies();
        species.setSpeciesName(request.getSpeciesName());
        species.setDescription(request.getDescription());
        species.setElements(request.getFengShuiList() == null ? new HashSet<>() : new HashSet<>(request.getFengShuiList()));
        species.setZodiacs(request.getZodiacList() == null ? new HashSet<>() : new HashSet<>(request.getZodiacList()));
        succulentSpeciesRepo.save(species);

        succulent.setImageUrl(request.getImageUrl());
        succulent.setUpdatedAt(LocalDateTime.now());

        if (succulent.getSize() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Không có size để cập nhật", null);
        }
        Map<String, Object> sizeRangeMap = (Map<String, Object>) succulent.getSize();
        Status mainStatus = Status.OUT_OF_STOCK;
//        {
//                "small": {
//                     "min": 0.1,
//                     "max": 0.9,
//                     "price": 1000,
//                     "quantity": 12,
//                     "status": "AVAILABLE"
//                }
//        }

        for (UpdateSucculentRequest.Size size : request.getSizeList()) {
            Object sizeDetailObject = sizeRangeMap.get(size.getSizeName().toLowerCase());

            if (sizeDetailObject == null) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Kích thước '" + size.getSizeName() + "' không tồn tại trong hệ thống", null);
            }

            Map<String, Object> sizeDetail = (Map<String, Object>) sizeDetailObject;


            if (size.getQuantity() > 0) {
                mainStatus = Status.AVAILABLE;
            }

            sizeDetail.replace("price", size.getPrice());
            sizeDetail.replace("quantity", size.getQuantity());
            sizeDetail.replace("status", size.getQuantity() > 0 ? Status.AVAILABLE.name() : Status.OUT_OF_STOCK.name());
        }

        succulent.setSize(sizeRangeMap);
        succulent.setStatus(mainStatus);
        succulentRepo.save(succulent);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
    }

    private String validateUpdateSucculent(UpdateSucculentRequest request, Succulent current) {
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

        if (!validSize(request.getSizeList()).isEmpty()) {
            return validSize(request.getSizeList());
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

    private String validSize(List<UpdateSucculentRequest.Size> sizeList) {
        if (sizeList == null || sizeList.isEmpty()) {
            return "Vui lòng chọn ít nhất một kích thước";
        }
        if (sizeList.size() > 5) {
            return "Hệ thống chỉ có tối đa 5 kích thước";
        }
        for (UpdateSucculentRequest.Size size : sizeList) {
            if (size.getSizeName() == null || size.getSizeName().trim().isEmpty()) {
                return "Tên kích thước là bắt buộc";
            }
            if (size.getPrice() <= 0) {
                return "Cần nhập giá bán lớn hơn 0";
            }
            // Cho phép quantity bằng 0 khi cập nhật
            if (size.getQuantity() < 0) {
                return "Số lượng cây không được là số âm";
            }
        }
        return "";
    }

    // =========================== Accessory ========================== \\

    @Override
    public ResponseEntity<ResponseObject> createAccessory(CreateAccessoryRequest request) {

        String error = validateCreateAccessory(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        AppConfig accessoryConfig = appConfigRepo.findByKey("accessory").orElse(null);
        Map<String, Object> accessory = new HashMap<>();

        if (accessoryConfig != null && accessoryConfig.getValue() != null) {
            accessory = (Map<String, Object>) accessoryConfig.getValue();
        }

        return request.isCreatePot() ? createPot(request, accessory, accessoryConfig)
                :
                request.isCreateSoil() ? createSoil(request, accessory, accessoryConfig)
                        :
                        request.isCreateDecoration() ? createDecoration(request, accessory, accessoryConfig)
                                :
                                ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid accessory creation method", null);
    }

    private String validateCreateAccessory(CreateAccessoryRequest request) {
        // validate here
        return "";
    }

    private ResponseEntity<ResponseObject> createPot(CreateAccessoryRequest request, Map<String, Object> accessoryData, AppConfig accessoryConfig) {
        CreateAccessoryRequest.PotData potData = request.getPotData();

        if (accessoryData.get("pot") == null) {
            // create new
            Map<String, Object> potDetailMap = createPotDetail(potData);

            accessoryData.put("pot", Map.of(potData.getName(), potDetailMap));

            accessoryConfig.setValue(accessoryData);
            appConfigRepo.save(accessoryConfig);
            return ResponseBuilder.build(HttpStatus.OK, "Create pot successfully", null);
        }

        Map<String, Object> pot = (Map<String, Object>) accessoryData.get("pot");
        Map<String, Object> potDetailMap = createPotDetail(potData);
        if (pot.get(potData.getName()) == null) {
            pot.put(potData.getName(), potDetailMap);
        } else {
            pot.replace(potData.getName(), potDetailMap);
        }

        accessoryData.replace("pot", pot);
        accessoryConfig.setValue(accessoryData);
        appConfigRepo.save(accessoryConfig);
        return ResponseBuilder.build(HttpStatus.OK, "Update pot successfully", null);
    }

    private Map<String, Object> createPotDetail(CreateAccessoryRequest.PotData potData) {
        Map<String, Object> potDetailMap = new HashMap<>();
        potDetailMap.put("material", potData.getMaterial());
        potDetailMap.put("color", potData.getColor());
        potDetailMap.put("description", potData.getDescription());
        potDetailMap.put("image", potData.getImages().stream().map(CreateAccessoryRequest.Image::getImage).toList());

        Map<String, Object> sizeDetailMap = new HashMap<>();// pot size detail level

        for (CreateAccessoryRequest.Size size : potData.getSizes()) {
            sizeDetailMap.put(size.getName(),
                    Map.of(
                            "potHeight", size.getPotHeight(),
                            "potUpperCrossSectionArea", size.getPotUpperCrossSectionArea(),
                            "maxSoilMassValue", size.getMaxSoilMassValue(),
                            "availableQty", size.getAvailableQty(),
                            "price", size.getPrice()
                    )
            );
        }

        potDetailMap.put("size", sizeDetailMap);
        return potDetailMap;
    }

    private ResponseEntity<ResponseObject> createSoil(CreateAccessoryRequest request, Map<String, Object> accessoryData, AppConfig accessoryConfig) {
        CreateAccessoryRequest.SoilData soilData = request.getSoilData();

        if (accessoryData.get("soil") == null) {
            // create new
            Map<String, Object> soilDetailMap = createSoilDetail(soilData);

            accessoryData.put("soil", Map.of(soilData.getName(), soilDetailMap));

            accessoryConfig.setValue(accessoryData);
            appConfigRepo.save(accessoryConfig);
            return ResponseBuilder.build(HttpStatus.OK, "Create soil successfully", null);
        }

        Map<String, Object> soil = (Map<String, Object>) accessoryData.get("soil");
        Map<String, Object> soilDetailMap = createSoilDetail(soilData);
        if (soil.get(soilData.getName()) == null) {
            soil.put(soilData.getName(), soilDetailMap);
        } else {
            soil.replace(soilData.getName(), soilDetailMap);
        }

        accessoryData.replace("soil", soil);
        accessoryConfig.setValue(accessoryData);
        appConfigRepo.save(accessoryConfig);
        return ResponseBuilder.build(HttpStatus.OK, "Update soil successfully", null);
    }

    private Map<String, Object> createSoilDetail(CreateAccessoryRequest.SoilData soilData) {
        Map<String, Object> soilDetailMap = new HashMap<>();
        soilDetailMap.put("description", soilData.getDescription());
        soilDetailMap.put("availableMassValue", soilData.getAvailableMassValue());
        soilDetailMap.put("image", soilData.getImages().stream().map(CreateAccessoryRequest.Image::getImage).toList());

        Map<String, Object> basePriceDetailMap = new HashMap<>();// base price detail level
        basePriceDetailMap.put("massValue", soilData.getBasePricing().getMassValue());
        basePriceDetailMap.put("massUnit", soilData.getBasePricing().getMassUnit());
        basePriceDetailMap.put("price", soilData.getBasePricing().getPrice());

        soilDetailMap.put("basePricing", basePriceDetailMap);
        return soilDetailMap;
    }

    private ResponseEntity<ResponseObject> createDecoration(CreateAccessoryRequest request, Map<String, Object> accessoryData, AppConfig accessoryConfig) {
        CreateAccessoryRequest.DecorationData decorationData = request.getDecorationData();

        if (accessoryData.get("decoration") == null) {
            // create new
            Map<String, Object> decorDetailMap = createDecorDetail(decorationData);

            accessoryData.put("decoration", Map.of(decorationData.getName(), decorDetailMap));

            accessoryConfig.setValue(accessoryData);
            appConfigRepo.save(accessoryConfig);
            return ResponseBuilder.build(HttpStatus.OK, "Create decoration successfully", null);
        }

        Map<String, Object> decoration = (Map<String, Object>) accessoryData.get("decoration");
        Map<String, Object> decorDetailMap = createDecorDetail(decorationData);
        if (decoration.get(decorationData.getName()) == null) {
            decoration.put(decorationData.getName(), decorDetailMap);
        } else {
            decoration.replace(decorationData.getName(), decorDetailMap);
        }

        accessoryData.replace("decoration", decoration);
        accessoryConfig.setValue(accessoryData);
        appConfigRepo.save(accessoryConfig);
        return ResponseBuilder.build(HttpStatus.OK, "Update decoration successfully", null);
    }

    private Map<String, Object> createDecorDetail(CreateAccessoryRequest.DecorationData decorationData) {
        Map<String, Object> decorDetailMap = new HashMap<>();
        decorDetailMap.put("description", decorationData.getDescription());
        decorDetailMap.put("price", decorationData.getPrice());
        decorDetailMap.put("availableQty", decorationData.getAvailableQty());
        decorDetailMap.put("image", decorationData.getImages().stream().map(CreateAccessoryRequest.Image::getImage).toList());
        return decorDetailMap;
    }


    @Override
    public ResponseEntity<ResponseObject> getAccessories(String type) {
        Map<String, Object> response = new HashMap<>();
        AppConfig accessoryConfig = appConfigRepo.findByKey("accessory").orElse(null);
        assert accessoryConfig != null;
        Object valueJson = accessoryConfig.getValue();

        if (valueJson != null) {
            switch (type) {
                case "pot":
                    response.put("pots", buildPotResponse((Map<String, Object>) valueJson));
                    break;
                case "soil":
                    response.put("soils", buildSoilResponse((Map<String, Object>) valueJson));
                    break;
                case "decoration":
                    response.put("decorations", buildDecorationResponse((Map<String, Object>) valueJson));
                    break;
                default:
                    response.put("pots", buildPotResponse((Map<String, Object>) valueJson));
                    response.put("soils", buildSoilResponse((Map<String, Object>) valueJson));
                    response.put("decorations", buildDecorationResponse((Map<String, Object>) valueJson));
                    break;
            }
        }

        return ResponseBuilder.build(HttpStatus.OK, "", response);
    }

    private List<Map<String, Object>> buildPotResponse(Map<String, Object> value) {
        List<Map<String, Object>> potResponse = new ArrayList<>();

        Object potJson = value.get("pot");
        if (potJson != null && !((Map<String, Object>) potJson).keySet().isEmpty()) {
            potResponse = ((Map<String, Object>) potJson).keySet().stream().map(
                    key -> {
                        Map<String, Object> potDetail = (Map<String, Object>) ((Map<String, Object>) potJson).get(key);

                        return Map.of(
                                "name", key.toLowerCase(),
                                "material", potDetail.get("material"),
                                "color", potDetail.get("color"),
                                "description", potDetail.get("description"),
                                "image", ((List<String>) potDetail.get("image")).stream().map(img -> Map.of("image", img)).toList(),
                                "size", ((Map<String, Object>) potDetail.get("size")).keySet().stream().map(
                                        sizeKey -> {
                                            Map<String, Object> sizeDetail = (Map<String, Object>) ((Map<String, Object>) potDetail.get("size")).get(sizeKey);
                                            return Map.of(
                                                    "name", sizeKey.toLowerCase(),
                                                    "potHeight", (Double) sizeDetail.get("potHeight"),
                                                    "potUpperCrossSectionArea", (Double) sizeDetail.get("potUpperCrossSectionArea"),
                                                    "maxSoilMassValue", (Double) sizeDetail.get("maxSoilMassValue"),
                                                    "availableQty", (Integer) sizeDetail.get("availableQty"),
                                                    "price", ((Integer) sizeDetail.get("price")).longValue()
                                            );
                                        }
                                ).toList()
                        );
                    }
            ).toList();
        }
        return potResponse;
    }

    private List<Map<String, Object>> buildSoilResponse(Map<String, Object> value) {
        List<Map<String, Object>> soilResponse = new ArrayList<>();

        Object soilJson = value.get("soil");
        if (soilJson != null && !((Map<String, Object>) soilJson).keySet().isEmpty()) {
            soilResponse = ((Map<String, Object>) soilJson).keySet().stream().map(
                    key -> {
                        Map<String, Object> soilDetail = (Map<String, Object>) ((Map<String, Object>) soilJson).get(key);

                        return Map.of(
                                "name", key.toLowerCase(),
                                "description", soilDetail.get("description"),
                                "availableMassValue", soilDetail.get("availableMassValue"),
                                "basePricing", ((Map<String, Object>) soilDetail.get("basePricing")),
                                "image", ((List<String>) soilDetail.get("image")).stream().map(img -> Map.of("image", img)).toList()
                        );
                    }
            ).toList();
        }

        return soilResponse;
    }

    private List<Map<String, Object>> buildDecorationResponse(Map<String, Object> value) {
        List<Map<String, Object>> decorationResponse = new ArrayList<>();

        Object decorationJson = value.get("decoration");
        if (decorationJson != null && !((Map<String, Object>) decorationJson).keySet().isEmpty()) {
            decorationResponse = ((Map<String, Object>) decorationJson).keySet().stream().map(
                    key -> {
                        Map<String, Object> decorationDetail = (Map<String, Object>) ((Map<String, Object>) decorationJson).get(key);

                        return Map.of(
                                "name", key.toLowerCase(),
                                "description", decorationDetail.get("description"),
                                "price", decorationDetail.get("price"),
                                "availableQty", decorationDetail.get("availableQty"),
                                "image", ((List<String>) decorationDetail.get("image")).stream().map(img -> Map.of("image", img)).toList()
                        );
                    }
            ).toList();
        }

        return decorationResponse;
    }

    // =========================== Product ========================== \\

    @Override
    public ResponseEntity<ResponseObject> createProduct(ProductCreateRequest request, HttpServletRequest httpRequest) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> viewProduct(HttpServletRequest httpRequest) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> updateProduct(ProductUpdateRequest request, HttpServletRequest httpRequest) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> deleteProduct(int id, HttpServletRequest httpRequest) {
        return null;
    }

    // =========================== Wishlist ========================== \\
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
