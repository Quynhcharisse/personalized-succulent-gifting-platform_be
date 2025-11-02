package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.AddWishListItemRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateProductRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.enums.Zodiac;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.AppConfig;
import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.models.ProductImage;
import com.exe201.group1.psgp_be.models.ProductSucculent;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.models.SucculentSpecies;
import com.exe201.group1.psgp_be.models.WishlistItem;
import com.exe201.group1.psgp_be.repositories.AppConfigRepo;
import com.exe201.group1.psgp_be.repositories.ProductImageRepo;
import com.exe201.group1.psgp_be.repositories.ProductRepo;
import com.exe201.group1.psgp_be.repositories.ProductSucculentRepo;
import com.exe201.group1.psgp_be.repositories.SucculentRepo;
import com.exe201.group1.psgp_be.repositories.SucculentSpeciesRepo;
import com.exe201.group1.psgp_be.repositories.WishListItemRepo;
import com.exe201.group1.psgp_be.services.ProductService;
import com.exe201.group1.psgp_be.utils.MapUtils;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import com.vladmihalcea.hibernate.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unchecked")
public class ProductServiceImpl implements ProductService {

    SucculentRepo succulentRepo;
    SucculentSpeciesRepo succulentSpeciesRepo;
    ProductRepo productRepo;
    ProductImageRepo productImageRepo;
    WishListItemRepo wishListItemRepo;
    AppConfigRepo appConfigRepo;
    ProductSucculentRepo productSucculentRepo;

    // =========================== Succulent ========================== \\
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request) {

        String error = validateCreateSucculent(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }
        SucculentSpecies species = succulentSpeciesRepo.findBySpeciesNameIgnoreCase(request.getSpeciesName()).orElse(null);

        if (species != null) {
            species.setDescription(request.getDescription());
            species.setElements(
                    request.getFengShuiList() == null ?
                            new HashSet<>()
                            :
                            new HashSet<>(
                                    request.getFengShuiList().stream()
                                            .map(fs -> FengShui.valueOf(fs.toUpperCase()))
                                            .toList()
                            )
            );
            species.setZodiacs(
                    request.getZodiacList() == null ?
                            new HashSet<>()
                            :
                            new HashSet<>(
                                    request.getZodiacList().stream()
                                            .map(z -> Zodiac.valueOf(z.toUpperCase()))
                                            .toList()
                            )
            );
        } else {
            species = SucculentSpecies.builder()
                    .speciesName(request.getSpeciesName())
                    .description(request.getDescription())
                    .elements(
                            request.getFengShuiList() == null ?
                                    new HashSet<>()
                                    :
                                    new HashSet<>(
                                            request.getFengShuiList().stream()
                                                    .map(fs -> FengShui.valueOf(fs.toUpperCase()))
                                                    .toList()
                                    )
                    )
                    .zodiacs(
                            request.getZodiacList() == null ?
                                    new HashSet<>()
                                    :
                                    new HashSet<>(
                                            request.getZodiacList().stream()
                                                    .map(z -> Zodiac.valueOf(z.toUpperCase()))
                                                    .toList()
                                    )
                    )
                    .build();
        }

        species = succulentSpeciesRepo.save(species);

        Succulent succulent = succulentRepo.save(
                Succulent.builder()
                        .species(species)
                        .size(null)
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

        Map<String, Object> sizeRangeMap = new HashMap<>();

        for (CreateSucculentRequest.Size size : request.getSizeList()) {
            Map<String, Object> sizeMap = new HashMap<>();
            sizeMap.put("minArea", size.getMinArea());
            sizeMap.put("maxArea", size.getMaxArea());
            sizeMap.put("price", size.getPrice());
            sizeMap.put("quantity", size.getQuantity());

            sizeRangeMap.put(size.getSizeName().toLowerCase(), sizeMap);
        }

        succulent.setSize(sizeRangeMap);
        succulentRepo.save(succulent);

        return ResponseBuilder.build(HttpStatus.OK, "Tạo catalog loài sen đá thành công", null);
    }

    private String validateCreateSucculent(CreateSucculentRequest request) {

        // speciesName
        if (request.getSpeciesName() == null || request.getSpeciesName().trim().isEmpty()) {
            return "Tên loài là bắt buộc";
        } else if (request.getSpeciesName().length() > 100) {
            return "Tên loài không được vượt quá 100 ký tự";
        }

        // description
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả là bắt buộc";
        }

        // imageUrl
        if (request.getImageUrl() == null || request.getImageUrl().trim().isEmpty()) {
            return "Image URL is required";
        } else {
            if (!request.getImageUrl().matches("^(?i)(http|https)://.*$")) {
                return "Invalid Image URL format";
            }
            if (!request.getImageUrl().matches("(?i).*(\\.jpg|\\.jpeg|\\.png|\\.gif)$")) {
                return "Image URL must end with a valid image file extension (jpg, jpeg, png, gif)";
            }
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
        if (sizeList.size() > 3) {
            return "Hệ thống chỉ có tối đa 3 kích thước";
        }

        Set<String> uniqueSizeNames = new HashSet<>();

        for (CreateSucculentRequest.Size size : sizeList) {
            if (size.getSizeName() == null || size.getSizeName().trim().isEmpty()) {
                return "Tên kích thước là bắt buộc";
            }

            String normalizedSizeName = size.getSizeName().trim().toLowerCase();
            if (!uniqueSizeNames.add(normalizedSizeName)) {
                return "Kích thước '" + size.getSizeName() + "' đã bị trùng lặp. Vui lòng sử dụng tên khác.";
            }

            if (size.getMaxArea() < size.getMinArea()) {
                return "Diện tích tối đa phải lớn hơn hoặc bằng diện tích tối thiểu";
            }
            if (size.getMaxArea() <= 0 || size.getMinArea() <= 0) {
                return "Cần nhập diện tích lớn hơn 0";
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
    public ResponseEntity<ResponseObject> viewSucculentList() {

        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách catalog sen đá thành công", buildListSucculent(succulentRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))));
    }

    private Map<String, Object> buildSucculentDetail(Succulent succulent) {
        Map<String, Object> size = (Map<String, Object>) succulent.getSize();
        Map<String, Object> sizeResponse = new HashMap<>();

        Status mainStatus = Status.OUT_OF_STOCK;

        for (String key : size.keySet()) {
            Map<String, Object> sizeDetail = new HashMap<>();

            Map<String, Object> value = (Map<String, Object>) size.get(key);

            Integer quantity = (Integer) value.get("quantity");

            if (quantity > 0) mainStatus = Status.AVAILABLE;

            sizeDetail.put("minArea", value.get("minArea"));
            sizeDetail.put("maxArea", value.get("maxArea"));
            sizeDetail.put("price", value.get("price"));
            sizeDetail.put("quantity", quantity);
            sizeDetail.put("status", quantity > 0 ? Status.AVAILABLE.getValue() : Status.OUT_OF_STOCK.getValue());

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
        response.put("status", mainStatus.getValue());
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
    public ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request) {

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

        if (succulent.getSize() == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Không có size để cập nhật", null);
        }
        Map<String, Object> sizeRangeMap = (Map<String, Object>) succulent.getSize();
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

            sizeDetail.replace("minArea", size.getMinArea());
            sizeDetail.replace("maxArea", size.getMaxArea());
            sizeDetail.replace("price", size.getPrice());
            sizeDetail.replace("quantity", size.getQuantity());
            sizeDetail.replace("status", size.getQuantity() > 0 ? Status.AVAILABLE.name() : Status.OUT_OF_STOCK.name());
        }

        succulent.setSize(sizeRangeMap);
        succulent.setImageUrl(request.getImageUrl());
        succulent.setUpdatedAt(LocalDateTime.now());
        succulentRepo.save(succulent);

        return ResponseBuilder.build(HttpStatus.OK, "Cập nhật mặt hàng thành công", null);
    }

    private String validateUpdateSucculent(UpdateSucculentRequest request, Succulent current) {
        if (request.getSpeciesName().length() > 100) {
            return "Tên loài không được vượt quá 100 ký tự";
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return "Mô tả là bắt buộc";
        }
        if (request.getImageUrl() == null || request.getImageUrl().trim().isEmpty()) {
            return "Image URL is required";
        }
        // Regex không phân biệt hoa/thường
        if (!request.getImageUrl().matches("^(?i)(http|https)://.*$")) {
            return "Invalid Image URL format";
        }
        if (!request.getImageUrl().matches("(?i).*(\\.(jpg|jpeg|png|gif))$")) {
            return "Image URL must end with a valid image file extension (jpg, jpeg, png, gif)";
        }

        // Gọi validSize đúng 1 lần
        String sizeErr = validSize(request.getSizeList());
        if (!sizeErr.isEmpty()) return sizeErr;

        // Kiểm tra size có tồn tại trong catalog hiện tại
        if (current.getSize() == null) {
            return "Không có size để cập nhật";
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> cur = (Map<String, Object>) current.getSize();

        Set<String> currentKeys = cur.keySet().stream()
                .filter(Objects::nonNull)
                .map(k -> k.trim().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> seen = new HashSet<>();
        for (UpdateSucculentRequest.Size s : request.getSizeList()) {
            if (s.getSizeName() == null || s.getSizeName().trim().isEmpty()) {
                return "Tên kích thước là bắt buộc";
            }
            String key = s.getSizeName().trim().toLowerCase();
            if (!seen.add(key)) {
                return "Kích thước '" + s.getSizeName() + "' bị trùng lặp trong yêu cầu cập nhật";
            }
            if (!currentKeys.contains(key)) {
                return "Kích thước '" + s.getSizeName() + "' không tồn tại trong hệ thống";
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

    private String validSize(List<UpdateSucculentRequest.Size> sizeList) {
        if (sizeList == null || sizeList.isEmpty()) {
            return "Vui lòng chọn ít nhất một kích thước";
        }
        if (sizeList.size() > 5) {
            return "Hệ thống chỉ có tối đa 5 kích thước";
        }

        Set<String> uniqueSizeNames = new HashSet<>();

        for (UpdateSucculentRequest.Size size : sizeList) {
            if (size.getSizeName() == null || size.getSizeName().trim().isEmpty()) {
                return "Tên kích thước là bắt buộc";
            }

            String normalizedSizeName = size.getSizeName().trim().toLowerCase();
            if (!uniqueSizeNames.add(normalizedSizeName)) {
                return "Kích thước '" + size.getSizeName() + "' đã bị trùng lặp. Vui lòng sử dụng tên khác.";
            }

            if (size.getMaxArea() < size.getMinArea()) {
                return "Diện tích tối đa phải lớn hơn hoặc bằng diện tích tối thiểu";
            }
            if (size.getMaxArea() <= 0 || size.getMinArea() <= 0) {
                return "Cần nhập diện tích lớn hơn 0";
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

    // =========================== Accessory ========================== \\

    @Override
    public ResponseEntity<ResponseObject> createOrUpdateAccessory(CreateOrUpdateAccessoryRequest request) {

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

    private String validateCreateAccessory(CreateOrUpdateAccessoryRequest request) {
        if (request == null) {
            return "Request cannot be null";
        }
        // Check that exactly one type of accessory is being created
        int accessoryTypeCount = 0;
        if (request.isCreatePot()) accessoryTypeCount++;
        if (request.isCreateSoil()) accessoryTypeCount++;
        if (request.isCreateDecoration()) accessoryTypeCount++;

        if (accessoryTypeCount != 1) {
            return "Exactly one type of accessory (pot, soil, or decoration) must be specified";
        }
        // Validate Pot
        if (request.isCreatePot()) {
            CreateOrUpdateAccessoryRequest.PotData potData = request.getPotData();
            if (potData == null) {
                return "Pot data cannot be null";
            }
            if (StringUtils.isBlank(potData.getName())) {
                return "Pot name cannot be empty";
            }
            if (StringUtils.isBlank(potData.getMaterial())) {
                return "Pot material cannot be empty";
            }
            if (StringUtils.isBlank(potData.getColor())) {
                return "Pot color cannot be empty";
            }
            if (potData.getImages() == null || potData.getImages().isEmpty()) {
                return "Pot must have at least one image";
            }
            if (potData.getSizes() == null || potData.getSizes().isEmpty()) {
                return "Pot must have at least one size configuration";
            }
            // Validate each size
            for (CreateOrUpdateAccessoryRequest.Size size : potData.getSizes()) {
                if (StringUtils.isBlank(size.getName())) {
                    return "Size name cannot be empty";
                }
                if (size.getPotHeight() <= 0) {
                    return "Pot height must be greater than 0";
                }
                if (size.getPotUpperCrossSectionArea() <= 0) {
                    return "Pot upper cross section area must be greater than 0";
                }
                if (size.getMaxSoilMassValue() <= 0) {
                    return "Maximum soil mass value must be greater than 0";
                }
                if (size.getAvailableQty() < 0) {
                    return "Available quantity cannot be negative";
                }
                if (size.getPrice() <= 0) {
                    return "Price must be greater than 0";
                }
            }
        }

        // Validate Soil
        if (request.isCreateSoil()) {
            CreateOrUpdateAccessoryRequest.SoilData soilData = request.getSoilData();
            if (soilData == null) {
                return "Soil data cannot be null";
            }
            if (StringUtils.isBlank(soilData.getName())) {
                return "Soil name cannot be empty";
            }
            if (StringUtils.isBlank(soilData.getDescription())) {
                return "Soil description cannot be empty";
            }
            if (soilData.getAvailableMassValue() <= 0) {
                return "Available mass value must be greater than 0";
            }
            if (soilData.getImages() == null || soilData.getImages().isEmpty()) {
                return "Soil must have at least one image";
            }
            // Validate base pricing
            CreateOrUpdateAccessoryRequest.BasePricing basePricing = soilData.getBasePricing();
            if (basePricing == null) {
                return "Base pricing cannot be null";
            }
            if (basePricing.getMassValue() <= 0) {
                return "Base pricing mass value must be greater than 0";
            }
            if (StringUtils.isBlank(basePricing.getMassUnit())) {
                return "Base pricing mass unit cannot be empty";
            }
            if (basePricing.getPrice() <= 0) {
                return "Base pricing price must be greater than 0";
            }
        }

        // Validate Decoration
        if (request.isCreateDecoration()) {
            CreateOrUpdateAccessoryRequest.DecorationData decorationData = request.getDecorationData();
            if (decorationData == null) {
                return "Decoration data cannot be null";
            }
            if (StringUtils.isBlank(decorationData.getName())) {
                return "Decoration name cannot be empty";
            }
            if (StringUtils.isBlank(decorationData.getDescription())) {
                return "Decoration description cannot be empty";
            }
            if (decorationData.getPrice() <= 0) {
                return "Decoration price must be greater than 0";
            }
            if (decorationData.getAvailableQty() < 0) {
                return "Available quantity cannot be negative";
            }
            if (decorationData.getImages() == null || decorationData.getImages().isEmpty()) {
                return "Decoration must have at least one image";
            }
        }

        return "";
    }

    private ResponseEntity<ResponseObject> createPot(CreateOrUpdateAccessoryRequest request, Map<String, Object> accessoryData, AppConfig accessoryConfig) {
        boolean create = request.isCreateAction();
        boolean update = !create;
        CreateOrUpdateAccessoryRequest.PotData potData = request.getPotData();

        if (accessoryData.get("pot") == null) {
            if (create) {
                Map<String, Object> potDetailMap = createPotDetail(potData);

                accessoryData.put("pot", Map.of(potData.getName(), potDetailMap));

                accessoryConfig.setValue(accessoryData);
                appConfigRepo.save(accessoryConfig);
                return ResponseBuilder.build(HttpStatus.OK, "Tạo chậu cây thành công", null);
            }

            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Chậu cây không tồn tại", null);
        }

        Map<String, Object> pot = (Map<String, Object>) accessoryData.get("pot");
        Map<String, Object> potDetailMap = createPotDetail(potData);
        if (pot.get(potData.getName()) == null) {
            if (update) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Chậu cây không tồn tại", null);
            }
            pot.put(potData.getName(), potDetailMap);
        } else {
            if (create) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Chậu cây đã tồn tại", null);
            }
            pot.replace(potData.getName(), potDetailMap);
        }

        accessoryData.replace("pot", pot);
        accessoryConfig.setValue(accessoryData);
        appConfigRepo.save(accessoryConfig);
        return ResponseBuilder.build(HttpStatus.OK, (create ? "Tạo " : "Cập nhật ") + "chậu cây thành công", null);
    }

    private Map<String, Object> createPotDetail(CreateOrUpdateAccessoryRequest.PotData potData) {
        Map<String, Object> potDetailMap = new HashMap<>();
        potDetailMap.put("material", potData.getMaterial());
        potDetailMap.put("color", potData.getColor());
        potDetailMap.put("description", potData.getDescription());
        potDetailMap.put("image", potData.getImages().stream().map(CreateOrUpdateAccessoryRequest.Image::getImage).toList());

        Map<String, Object> sizeDetailMap = new HashMap<>();// pot size detail level

        for (CreateOrUpdateAccessoryRequest.Size size : potData.getSizes()) {
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

    private ResponseEntity<ResponseObject> createSoil(CreateOrUpdateAccessoryRequest request, Map<String, Object> accessoryData, AppConfig accessoryConfig) {
        boolean create = request.isCreateAction();
        boolean update = !create;
        CreateOrUpdateAccessoryRequest.SoilData soilData = request.getSoilData();

        if (accessoryData.get("soil") == null) {
            if (create) {
                Map<String, Object> soilDetailMap = createSoilDetail(soilData);

                accessoryData.put("soil", Map.of(soilData.getName(), soilDetailMap));

                accessoryConfig.setValue(accessoryData);
                appConfigRepo.save(accessoryConfig);
                return ResponseBuilder.build(HttpStatus.OK, "Tạo đất thành công", null);
            }

            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Đất không tồn tại", null);
        }

        Map<String, Object> soil = (Map<String, Object>) accessoryData.get("soil");
        Map<String, Object> soilDetailMap = createSoilDetail(soilData);
        if (soil.get(soilData.getName()) == null) {
            if (update) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Đất không tồn tại", null);
            }
            soil.put(soilData.getName(), soilDetailMap);
        } else {
            if (create) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Đất đã tồn tại", null);
            }
            soil.replace(soilData.getName(), soilDetailMap);
        }

        accessoryData.replace("soil", soil);
        accessoryConfig.setValue(accessoryData);
        appConfigRepo.save(accessoryConfig);
        return ResponseBuilder.build(HttpStatus.OK, (create ? "Tạo " : "Cập nhật ") + "đất thành công", null);
    }

    private Map<String, Object> createSoilDetail(CreateOrUpdateAccessoryRequest.SoilData soilData) {
        Map<String, Object> soilDetailMap = new HashMap<>();
        soilDetailMap.put("description", soilData.getDescription());
        soilDetailMap.put("availableMassValue", soilData.getAvailableMassValue());
        soilDetailMap.put("image", soilData.getImages().stream().map(CreateOrUpdateAccessoryRequest.Image::getImage).toList());

        Map<String, Object> basePriceDetailMap = new HashMap<>();// base price detail level
        basePriceDetailMap.put("massValue", soilData.getBasePricing().getMassValue());
        basePriceDetailMap.put("massUnit", soilData.getBasePricing().getMassUnit());
        basePriceDetailMap.put("price", soilData.getBasePricing().getPrice());

        soilDetailMap.put("basePricing", basePriceDetailMap);
        return soilDetailMap;
    }

    private ResponseEntity<ResponseObject> createDecoration(CreateOrUpdateAccessoryRequest request, Map<String, Object> accessoryData, AppConfig accessoryConfig) {
        boolean create = request.isCreateAction();
        boolean update = !create;
        CreateOrUpdateAccessoryRequest.DecorationData decorationData = request.getDecorationData();

        if (accessoryData.get("decoration") == null) {
            if (create) {
                Map<String, Object> decorDetailMap = createDecorDetail(decorationData);

                accessoryData.put("decoration", Map.of(decorationData.getName(), decorDetailMap));

                accessoryConfig.setValue(accessoryData);
                appConfigRepo.save(accessoryConfig);
                return ResponseBuilder.build(HttpStatus.OK, "Tạo đồ trang trí thành công", null);
            }

            return ResponseBuilder.build(HttpStatus.OK, "Đồ trang trí không tồn tại", null);
        }

        Map<String, Object> decoration = (Map<String, Object>) accessoryData.get("decoration");
        Map<String, Object> decorDetailMap = createDecorDetail(decorationData);
        if (decoration.get(decorationData.getName()) == null) {
            if (update) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Đồ trang trí không tồn tại", null);
            }
            decoration.put(decorationData.getName(), decorDetailMap);
        } else {
            if (create) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Đồ trang trí đã tồn tại", null);
            }
            decoration.replace(decorationData.getName(), decorDetailMap);
        }

        accessoryData.replace("decoration", decoration);
        accessoryConfig.setValue(accessoryData);
        appConfigRepo.save(accessoryConfig);
        return ResponseBuilder.build(HttpStatus.OK, (create ? "Tạo " : "Cập nhật ") + "đồ trang trí thành công", null);
    }

    private Map<String, Object> createDecorDetail(CreateOrUpdateAccessoryRequest.DecorationData decorationData) {
        Map<String, Object> decorDetailMap = new HashMap<>();
        decorDetailMap.put("description", decorationData.getDescription());
        decorDetailMap.put("price", decorationData.getPrice());
        decorDetailMap.put("availableQty", decorationData.getAvailableQty());
        decorDetailMap.put("image", decorationData.getImages().stream().map(CreateOrUpdateAccessoryRequest.Image::getImage).toList());
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
                                "image", ((List<String>) potDetail.get("image")).stream().map(img -> Map.of("url", img)).toList(),
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
                                "basePricing", soilDetail.get("basePricing"),
                                "image", ((List<String>) soilDetail.get("image")).stream().map(img -> Map.of("url", img)).toList()
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
                                "image", ((List<String>) decorationDetail.get("image")).stream().map(img -> Map.of("url", img)).toList()
                        );
                    }
            ).toList();
        }

        return decorationResponse;
    }

    // =========================== Product ========================== \\

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createOrUpdateProduct(CreateOrUpdateProductRequest request) {
        String error = validateCreateProduct(request);
        if (!error.isEmpty()) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);

        Map<String, Map<String, Object>> sizes = new HashMap<>();

        Set<Integer> succulentIDs = new LinkedHashSet<>();

        for (CreateOrUpdateProductRequest.Size size : request.getSizes()) {
            Map<String, Object> data = new HashMap<>(buildSizeMap(size));

            Set<Integer> idUsedList = (Set<Integer>) ((Map<String, Object>) data.get("succulents")).get("ids");

            succulentIDs.addAll(idUsedList);

            Map<String, Object> succulentData = (Map<String, Object>) data.get("succulents");

            succulentData.remove("ids");

            List<Map<String, Object>> succulentDataMap = (List<Map<String, Object>>) ((Map<String, Object>) data.get("succulents")).get("list");

            succulentData.remove("list");

            data.replace("succulents", succulentDataMap);

            sizes.put(size.getName(), data);
        }

        LocalDateTime now = LocalDateTime.now();

        Product product;

        if (request.isCreateAction()) {
            product = productRepo.save(
                    Product.builder()
                            .name(request.getName())
                            .description(request.getDescription())
                            .createdAt(now)
                            .updatedAt(now)
                            .privacy(true)
                            .createdBy("LaNhoBenThem")
                            .status(null)
                            .size(sizes)
                            .build()
            );

            for (CreateOrUpdateProductRequest.Image image : request.getImages()) {
                productImageRepo.save(
                        ProductImage.builder()
                                .imageUrl(image.getUrl())
                                .product(product)
                                .build()
                );
            }
        } else {
            if (request.getProductId() == null || request.getProductId() <= 0) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "ID sản phẩm cần cập nhật không hợp lệ.", null);
            }
            product = productRepo.findById(request.getProductId()).orElse(null);

            if (product == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Sản phẩm không tồn tại", null);
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setUpdatedAt(now);
            product.setStatus(null);
            product.setSize(sizes);
            product.setProductImages(null);
            product = productRepo.save(product);

            productImageRepo.deleteAllByProduct_Id(product.getId());

            for (CreateOrUpdateProductRequest.Image image : request.getImages()) {
                productImageRepo.save(
                        ProductImage.builder()
                                .imageUrl(image.getUrl())
                                .product(product)
                                .build()
                );
            }
        }

        product.setStatus(checkProductStatus(product) ? Status.AVAILABLE : Status.OUT_OF_STOCK);
        product = productRepo.save(product);

        for (Integer id : succulentIDs) {
            Succulent succulent = succulentRepo.findById(id).orElse(null);
            if (succulent == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// make transaction rollback
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Sen đá với id " + id + " không tồn tại", null);
            }

            productSucculentRepo.save(
                    ProductSucculent.builder()
                            .product(product)
                            .succulent(succulent)
                            .build()
            );

        }

        return ResponseBuilder.build(HttpStatus.CREATED, "Tạo sản phẩm thành công", null);
    }

    private String validateCreateProduct(CreateOrUpdateProductRequest request) {
//        if (request.getName() == null || request.getName().trim().isEmpty()) {
//            return "Tên sản phẩm không được để trống.";
//        }
//        if (request.getName().length() > 250) {
//            return "Tên sản phẩm không được vượt quá 250 ký tự.";
//        }
//        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
//            return "Mô tả sản phẩm không được để trống.";
//        }
//        if (!request.isCreateAction()) {
//            if (request.getProductId() == null || request.getProductId() <= 0) {
//                return "ID sản phẩm cần cập nhật không hợp lệ.";
//            }
//        }
//        if (request.getSizes() == null || request.getSizes().isEmpty()) {
//            return "Sản phẩm phải có ít nhất một cấu hình kích cỡ.";
//        }
//
//        Set<String> sizeNames = new HashSet<>();
//        for (CreateOrUpdateProductRequest.Size size : request.getSizes()) {
//            if (size.getName() == null || size.getName().trim().isEmpty()) {
//                return "Tên kích cỡ không được để trống.";
//            }
//            if (!sizeNames.add(size.getName().trim().toLowerCase())) {
//                return "Các tên kích cỡ phải là duy nhất (trùng: " + size.getName() + ").";
//            }
//            if (size.getSucculents() == null || size.getSucculents().isEmpty()) {
//                return "Mỗi kích cỡ phải có ít nhất một loại sen đá.";
//            }
//            for (CreateOrUpdateProductRequest.Succulent succulent : size.getSucculents()) {
//                if (succulent.getId() <= 0) {
//                    return "ID sen đá không hợp lệ.";
//                }
//                if (succulent.getSizes() == null || succulent.getSizes().isEmpty()) {
//                    return "Cấu hình kích cỡ sen đá không được để trống (ID: " + succulent.getId() + ").";
//                }
//                for (CreateOrUpdateProductRequest.SucculentSize succulentSize : succulent.getSizes()) {
//                    if (succulentSize.getSize() == null || succulentSize.getSize().trim().isEmpty()) {
//                        return "Tên kích cỡ sen đá không được để trống (ID: " + succulent.getId() + ").";
//                    }
//                    if (succulentSize.getQuantity() <= 0) {
//                        return "Số lượng sen đá phải lớn hơn 0 (ID: " + succulent.getId() + ").";
//                    }
//                }
//            }
//
//            // Pot validation
//            if (size.getPot() == null) {
//                return "Thông tin chậu không được để trống cho kích cỡ: " + size.getName() + ".";
//            }
//            if (size.getPot().getName() == null || size.getPot().getName().trim().isEmpty()) {
//                return "Tên chậu không được để trống cho kích cỡ: " + size.getName() + ".";
//            }
//            if (size.getPot().getSize() == null || size.getPot().getSize().trim().isEmpty()) {
//                return "Kích cỡ chậu không được để trống cho kích cỡ: " + size.getName() + ".";
//            }
//
//            // Soil validation
//            if (size.getSoil() == null) {
//                return "Thông tin đất không được để trống cho kích cỡ: " + size.getName() + ".";
//            }
//            if (size.getSoil().getName() == null || size.getSoil().getName().trim().isEmpty()) {
//                return "Tên đất không được để trống cho kích cỡ: " + size.getName() + ".";
//            }
//            if (size.getSoil().getMassAmount() <= 0) {
//                return "Khối lượng đất phải lớn hơn 0 cho kích cỡ: " + size.getName() + ".";
//            }
//
//            // Decoration validation
//            if (size.getDecoration() == null) {
//                return "Thông tin vật trang trí không được để trống cho kích cỡ: " + size.getName() + ".";
//            }
//            if (size.getDecoration().isIncluded()) {
//                if (size.getDecoration().getDetails() == null || size.getDecoration().getDetails().isEmpty()) {
//                    return "Nếu có trang trí đi kèm, chi tiết trang trí không được để trống cho kích cỡ: " + size.getName() + ".";
//                }
//                Set<String> decorationNames = new HashSet<>();
//                for (CreateOrUpdateProductRequest.DecorationDetail detail : size.getDecoration().getDetails()) {
//                    if (detail.getName() == null || detail.getName().trim().isEmpty()) {
//                        return "Tên vật trang trí chi tiết không được để trống cho kích cỡ: " + size.getName() + ".";
//                    }
//                    if (detail.getQuantity() <= 0) {
//                        return "Số lượng vật trang trí chi tiết phải lớn hơn 0 cho kích cỡ: " + size.getName() + " (Vật: " + detail.getName() + ").";
//                    }
//                    if (!decorationNames.add(detail.getName().trim().toLowerCase())) {
//                        return "Vật trang trí chi tiết bị lặp lại trong cùng một kích cỡ: " + size.getName() + " (Vật: " + detail.getName() + ").";
//                    }
//                }
//            } else {
//                if (size.getDecoration().getDetails() != null && !size.getDecoration().getDetails().isEmpty()) {
//                    return "Nếu không bao gồm trang trí, danh sách chi tiết trang trí (details) phải rỗng hoặc null cho kích cỡ: " + size.getName() + ".";
//                }
//            }
//        }
        return "";
    }

    @Override
    public Map<String, Object> buildSizeMap(CreateOrUpdateProductRequest.Size size) {
        Map<String, Object> result = new HashMap<>();
        result.put("succulents", buildSucculentsMap(size.getSucculents()));
        result.put("pot", buildPotMap(size.getPot()));
        result.put("soil", buildSoilMap(size.getSoil()));
        result.put("decoration", buildDecorationsMap(size.getDecoration()));

        return result;
    }

    private Map<String, Object> buildSucculentsMap(List<CreateOrUpdateProductRequest.Succulent> succulents) {
        Set<Integer> idUsedList = new LinkedHashSet<>();
        List<Map<String, Object>> succulentList = new ArrayList<>();

        Map<String, Object> result = new HashMap<>();

        for (CreateOrUpdateProductRequest.Succulent succulent : succulents) {
            boolean added = idUsedList.add(succulent.getId());

            if (added) {
                Map<String, Object> sizeDetail = new HashMap<>();
                for (CreateOrUpdateProductRequest.SucculentSize s : succulent.getSizes()) {
                    sizeDetail.put(s.getSize().toLowerCase(), s.getQuantity());
                }

                Map<String, Object> succulentData = new HashMap<>();
                succulentData.put("id", succulent.getId());
                succulentData.put("name", succulent.getName());
                succulentData.put("size", sizeDetail);

                succulentList.add(succulentData);
            }

        }
        result.put("ids", idUsedList);
        result.put("list", succulentList);

        return result;
    }

    private Map<String, Object> buildPotMap(CreateOrUpdateProductRequest.Pot pot) {
        return Map.of(
                "name", pot.getName(),
                "size", pot.getSize()
        );
    }

    private Map<String, Object> buildSoilMap(CreateOrUpdateProductRequest.Soil soil) {
        return Map.of(
                "name", soil.getName(),
                "massAmount", soil.getMassAmount()
        );
    }

    private Map<String, Object> buildDecorationsMap(CreateOrUpdateProductRequest.Decoration decoration) {
        return Map.of(
                "included", decoration.isIncluded(),
                "detail", Objects.requireNonNullElse(buildDecorationDetailMap(decoration.getDetails(), decoration.isIncluded()), "")
        );
    }

    private Map<String, Object> buildDecorationDetailMap
            (List<CreateOrUpdateProductRequest.DecorationDetail> details, boolean included) {
        Map<String, Object> resultMap = new HashMap<>();

        for (CreateOrUpdateProductRequest.DecorationDetail detail : details) {
            resultMap.put(detail.getName(), detail.getQuantity());
        }

        return included ? resultMap : null;
    }

    @Override
    public boolean checkProductStatus(Product product) {
        boolean succulentAvailable = true;
        boolean potAvailable = true;
        boolean soilAvailable = true;
        boolean decorationAvailable = true;

        AppConfig accessoryConfig = appConfigRepo.findByKey("accessory").orElse(null);
        assert accessoryConfig != null;
        Object valueJson = accessoryConfig.getValue();

        List<Map<String, Object>> potConfig = buildPotResponse((Map<String, Object>) valueJson);
        List<Map<String, Object>> soilConfig = buildSoilResponse((Map<String, Object>) valueJson);
        List<Map<String, Object>> decorationConfig = buildDecorationResponse((Map<String, Object>) valueJson);

        Map<String, Object> productSizeData = (Map<String, Object>) product.getSize();

        for (String key : productSizeData.keySet()) {
            Map<String, Object> potData = (Map<String, Object>) ((Map<Object, Object>) productSizeData.get(key)).get("pot");
            if (!checkPotAvailable(potData.get("name").toString(), potData.get("size").toString(), potConfig)) {
                potAvailable = false;
            }

            Map<String, Object> soilData = (Map<String, Object>) ((Map<Object, Object>) productSizeData.get(key)).get("soil");
            if (!checkSoilAvailable(soilData.get("name").toString(), (double) soilData.get("massAmount"), soilConfig)) {
                soilAvailable = false;
            }

            Map<String, Object> productData = (Map<String, Object>) productSizeData.get(key);
            Map<String, Object> decorationData = (Map<String, Object>) productData.get("decoration");

            // Convert detail (Map) to details (List)
            List<Map<String, Object>> details = null;
            if (decorationData.get("details") instanceof List) {
                details = (List<Map<String, Object>>) decorationData.get("details");
            } else if (decorationData.get("detail") instanceof Map) {
                Map<String, Object> detailMap = (Map<String, Object>) decorationData.get("detail");
                details = detailMap.entrySet().stream()
                        .map(entry -> Map.of("name", entry.getKey(), "quantity", entry.getValue()))
                        .toList();
            }

            if (!checkDecorationAvailable(
                    Boolean.parseBoolean(decorationData.get("included").toString()),
                    details,
                    decorationConfig)) {
                decorationAvailable = false;
            }

            List<Map<String, Object>> succulentData = (List<Map<String, Object>>) ((Map<Object, Object>) productSizeData.get(key)).get("succulents");
            for (Map<String, Object> succulent : succulentData) {
                Map<String, Object> sizeData = MapUtils.getMapFromObject(succulent, "size");
                for (String sizeKey : sizeData.keySet()) {
                    if (!checkSucculentAvailable((int) succulent.get("id"), sizeKey, (int) sizeData.get(sizeKey))) {
                        succulentAvailable = false;
                    }
                }
            }
        }
        return succulentAvailable && potAvailable && soilAvailable && decorationAvailable;
    }

    private boolean checkPotAvailable(String name, String size, List<Map<String, Object>> potConfig) {
        Map<String, Object> potData = potConfig.stream().filter(p -> p.get("name").toString().equalsIgnoreCase(name)).findFirst().orElse(null);

        if (potData == null) return false;

        Map<String, Object> sizeData = ((List<Map<String, Object>>) potData.get("size")).stream().filter(s -> s.get("name").toString().equalsIgnoreCase(size)).findFirst().orElse(null);
        if (sizeData == null) return false;

        int availableQty = (int) sizeData.get("availableQty");

        return availableQty >= 1;
    }

    private boolean checkSoilAvailable(String name, double massAmount, List<Map<String, Object>> soilConfig) {
        Map<String, Object> soilData = soilConfig.stream().filter(s -> s.get("name").toString().equalsIgnoreCase(name)).findFirst().orElse(null);

        if (soilData == null) return false;

        double availableMassValue = ((Integer) soilData.get("availableMassValue")).doubleValue();

        return availableMassValue >= massAmount;
    }

    private boolean checkDecorationAvailable(boolean included, List<Map<String, Object>> details, List<Map<String, Object>> decorationConfig) {
        if (!included) return true;
        if (details == null) return true;
        for (Map<String, Object> detailItem : details) {
            String name = detailItem.get("name").toString();
            int requiredQty = (int) detailItem.get("quantity");

            Map<String, Object> configItem = decorationConfig.stream()
                    .filter(d -> d.get("name").toString().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);

            if (configItem == null) return false;

            int availableQty = (int) configItem.get("availableQty");
            if (requiredQty > availableQty) return false;
        }
        return true;
    }

    private boolean checkSucculentAvailable(int id, String size, int qty) {
        List<Map<String, Object>> succulents = succulentRepo.findAll().stream().map(this::buildSucculentDetail).toList();

        Map<String, Object> succulentData = succulents.stream().filter(s -> ((int) s.get("id")) == id).findFirst().orElse(null);
        if (succulentData == null) return false;

        Map<String, Object> succulentSizeData = (Map<String, Object>) ((Map<String, Object>) succulentData.get("size")).get(size);
        if (succulentSizeData == null) return false;

        int availableQty = (int) succulentSizeData.get("quantity");

        return availableQty >= qty;
    }

    @Override
    public ResponseEntity<ResponseObject> viewProduct() {
        List<Map<String, Object>> data = productRepo.findAll().stream()
                .filter(Product::isPrivacy)
                .map(
                        product -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", product.getId());
                            map.put("name", product.getName());
                            map.put("description", product.getDescription());
                            map.put("createAt", product.getCreatedAt());
                            map.put("updateAt", product.getUpdatedAt());
                            map.put("status", product.getStatus().getValue().toLowerCase());
                            map.put("images", buildProductImageResponse(product.getProductImages()));
                            map.put("sizes", buildProductSizeResponse((Map<String, Object>) product.getSize()));
                            return map;
                        }
                ).toList();
        return ResponseBuilder.build(HttpStatus.OK, "", data);
    }

    private List<Map<String, Object>> buildProductImageResponse(List<ProductImage> images) {
        return images.stream().map(
                image -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", image.getId());
                    map.put("url", image.getImageUrl());
                    return map;
                }
        ).toList();
    }

    @Override
    public List<Map<String, Object>> buildProductSizeResponse(Map<String, Object> size) {
        return size.keySet().stream()
                .filter(key -> !key.startsWith("v_")) // Filter out version keys (v_1, v_2, etc.)
                .map(
                        key -> {
                            Map<String, Object> sizeData = (Map<String, Object>) size.get(key);

                            List<Map<String, Object>> succulentData = (List<Map<String, Object>>) sizeData.get("succulents");
                            Map<String, Object> potData = (Map<String, Object>) sizeData.get("pot");
                            Map<String, Object> soilData = (Map<String, Object>) sizeData.get("soil");
                            Map<String, Object> decorationData = (Map<String, Object>) sizeData.get("decoration");

                            AppConfig accessoryConfig = appConfigRepo.findByKey("accessory").orElse(null);
                            assert accessoryConfig != null;

                            Map<String, Object> accessoryConfigData = (Map<String, Object>) accessoryConfig.getValue();

                            Map<String, Object> map = new HashMap<>();
                            map.put("name", key);
                            map.put("succulents", buildProductSucculentListResponse(succulentData));
                            map.put("pot", buildProductPotResponse(accessoryConfigData, potData));
                            map.put("soil", buildProductSoilResponse(accessoryConfigData, soilData));
                            map.put("decorations", buildProductDecorationListResponse(accessoryConfigData, decorationData)
                            );
                            return map;
                        }
                ).toList();
    }

    private List<Map<String, Object>> buildProductSucculentListResponse(List<Map<String, Object>> rawSucculents) {
        return rawSucculents.stream().map(
                succulent -> {
                    int id = (int) succulent.get("id");
                    Succulent s = succulentRepo.findById(id).orElse(null);
                    if (s == null) return null;
                    return buildProductSucculentResponse(s, (Map<String, Object>) succulent.get("size"));
                }
        ).toList();
    }

    private Map<String, Object> buildProductSucculentResponse(Succulent succulent, Map<String, Object> size) {
        List<Map<String, Object>> sizeDetail = size.keySet().stream().map(
                key -> {
                    Map<String, Object> detail = new HashMap<>(buildProductSucculentSizeResponse(succulent.getSize(), key));
                    detail.put("name", key.toLowerCase());
                    detail.put("quantity", size.get(key));
                    return detail;
                }
        ).toList();

        return Map.of(
                "id", succulent.getId(),
                "name", succulent.getSpecies().getSpeciesName(),
                "description", succulent.getSpecies().getDescription(),
                "createAt", succulent.getCreatedAt(),
                "updateAt", succulent.getUpdatedAt(),
                "zodiacs", succulent.getSpecies().getZodiacs(),
                "fengsui", succulent.getSpecies().getElements(),
                "image", succulent.getImageUrl(),
                "size", sizeDetail
        );
    }

    private Map<String, Object> buildProductSucculentSizeResponse(Object succulentSize, String size) {
        Map<String, Object> sizeData = ((Map<String, Object>) ((Map<String, Object>) succulentSize).get(size.toLowerCase().trim()));
        return Map.of(
                "area", Map.of(
                        "min", ((Number) sizeData.get("minArea")).intValue(),
                        "max", ((Number) sizeData.get("maxArea")).intValue()
                ),
                "price", ((Number) sizeData.get("price")).longValue()
        );
    }

    private Map<String, Object> buildProductPotResponse(Map<String, Object> accessoryConfigData, Map<String, Object> potData) {
        return buildPotResponse(accessoryConfigData).stream()
                .filter(pot -> pot.get("name").toString().equalsIgnoreCase(potData.get("name").toString()))
                .findFirst()
                .map(pot -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", pot.get("name"));
                    map.put("material", pot.get("material"));
                    map.put("color", pot.get("color"));
                    map.put("description", pot.get("description"));
                    map.put("image", ((List<Map<String, Object>>) pot.get("image")).stream().map(img -> img.get("url").toString()).toList());
                    map.put("size", ((List<Map<String, Object>>) pot.get("size")).stream()
                            .filter(p -> p.get("name").toString().equalsIgnoreCase(potData.get("size").toString()))
                            .map(
                                    p -> {
                                        Map<String, Object> sizeDetail = new HashMap<>();
                                        sizeDetail.put("name", p.get("name"));
                                        sizeDetail.put("height", p.get("potHeight"));
                                        sizeDetail.put("upperCrossSectionArea", p.get("potUpperCrossSectionArea"));
                                        sizeDetail.put("maxMassValue", p.get("maxSoilMassValue"));
                                        sizeDetail.put("price", p.get("price"));
                                        return sizeDetail;
                                    }
                            ).toList()
                    );
                    return map;
                })
                .orElse(null);
    }

    private Map<String, Object> buildProductSoilResponse(Map<String, Object> accessoryConfigData, Map<String, Object> soilData) {
        Map<String, Object> rawData = buildSoilResponse(accessoryConfigData).stream()
                .filter(soil -> soil.get("name").toString().equalsIgnoreCase(soilData.get("name").toString()))
                .findFirst()
                .orElse(null);

        if (rawData == null) return null;

        rawData = new HashMap<>(rawData);

        rawData.remove("availableMassValue");

        rawData.put("massAmount", soilData.get("massAmount"));

        return rawData;
    }

    private List<Map<String, Object>> buildProductDecorationListResponse(Map<String, Object> accessoryConfigData, Map<String, Object> decorationData) {
        if ((boolean) decorationData.get("included")) {
            Map<String, Object> decorationDataMap = MapUtils.checkIfObjectIsMap(decorationData.get("detail"));

            if (decorationDataMap == null) return null;

            return buildDecorationResponse(accessoryConfigData).stream()
                    .filter(decor -> decorationDataMap.get(decor.get("name").toString()) instanceof Integer)
                    .map(decor -> {
                        int quantity = (int) decorationDataMap.get(decor.get("name").toString());

                        Map<String, Object> map = new HashMap<>();
                        map.put("name", decor.get("name"));
                        map.put("description", decor.get("description"));
                        map.put("quantity", quantity);
                        map.put("unitPrice", decor.get("price"));
                        map.put("totalPrice", ((Integer) decor.get("price")).longValue() * quantity);
                        map.put("image", ((List<Map<String, Object>>) decor.get("image")).stream().map(img -> img.get("url").toString()).toList());
                        return map;
                    })
                    .toList();
        }
        return new ArrayList<>();
    }

    @Override
    public ResponseEntity<ResponseObject> deactivateProduct(int id) {
        Product product = productRepo.findById(id).orElse(null);
        if (product == null) return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Sản phẩm không tồn tại", null);
        if (!product.getStatus().equals(Status.UNAVAILABLE)) {
            product.setStatus(Status.UNAVAILABLE);
        } else {
            product.setStatus(checkProductStatus(product) ? Status.AVAILABLE : Status.OUT_OF_STOCK);
        }

        productRepo.save(product);
        return ResponseBuilder.build(HttpStatus.OK, "Xóa sản phẩm thành công", null);
    }

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
}
