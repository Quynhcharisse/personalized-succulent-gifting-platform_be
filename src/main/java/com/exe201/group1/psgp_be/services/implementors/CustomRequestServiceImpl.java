package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateCustomProductRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateProductRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateRevisionRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomProductRequestDesignImageRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.CustomProductRequest;
import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.CustomProductRequestRepo;
import com.exe201.group1.psgp_be.repositories.ProductRepo;
import com.exe201.group1.psgp_be.services.CustomRequestService;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.ProductService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.EntityResponseBuilder;
import com.exe201.group1.psgp_be.utils.MapUtils;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unchecked")
public class CustomRequestServiceImpl implements CustomRequestService {

    JWTService jwtService;
    AccountRepo accountRepo;
    CustomProductRequestRepo customProductRequestRepo;
    ProductService productService;
    ProductRepo productRepo;

    // =========================== Custom Product ========================== \\
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createCustomProductRequest(CreateCustomProductRequestRequest request, HttpServletRequest httpRequest) {
        Account account = CookieUtil.extractAccountFromCookie(httpRequest, jwtService, accountRepo);
        if (account == null) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Account not found", null);
        }

        String error = validateCreateCustomProductRequest(request);
        if (!error.isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, error, null);
        }

        CreateCustomProductRequestRequest.Size requestSize = request.getSize();

        CreateOrUpdateProductRequest.Decoration decoration = requestSize.getDecoration();
        if (decoration == null) {
            decoration = CreateOrUpdateProductRequest.Decoration.builder()
                    .included(false)
                    .details(Collections.emptyList())
                    .build();
        } else if (decoration.getDetails() == null) {
            decoration.setDetails(Collections.emptyList());
        }

        CreateOrUpdateProductRequest.Pot pot = requestSize.getPot();
        if (pot == null) {
            pot = CreateOrUpdateProductRequest.Pot.builder()
                    .name("")
                    .size("")
                    .build();
        }

        CreateOrUpdateProductRequest.Soil soil = requestSize.getSoil();
        if (soil == null) {
            soil = CreateOrUpdateProductRequest.Soil.builder()
                    .name("")
                    .massAmount(0)
                    .build();
        }

        List<CreateOrUpdateProductRequest.Succulent> succulents = requestSize.getSucculents() != null
                ? requestSize.getSucculents()
                : Collections.emptyList();

        CreateOrUpdateProductRequest.Size customSize = CreateOrUpdateProductRequest.Size.builder()
                .name("custom")
                .succulents(succulents)
                .pot(pot)
                .soil(soil)
                .decoration(decoration)
                .build();


        CreateOrUpdateProductRequest productRequest = CreateOrUpdateProductRequest.builder()
                .createAction(true)
                .sizes(List.of(customSize))
                .images(request.getImages())
                .build();

        Map<String, Map<String, Object>> data = new HashMap<>(buildSizeData(productRequest));

        customProductRequestRepo.save(
                CustomProductRequest.builder()
                        .buyer(account.getUser())
                        .data(data)
                        .designImage(null)
                        .status(Status.PENDING)
                        .occasion(request.getOccasion().trim())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return ResponseBuilder.build(HttpStatus.CREATED, "Tạo yêu cầu điện cây thành công", null);
    }

    private String validateCreateCustomProductRequest(CreateCustomProductRequestRequest request) {
        if (request.getSize() == null) {
            return "Thông tin cấu hình sản phẩm không được để trống.";
        }

        CreateCustomProductRequestRequest.Size size = request.getSize();

        if (size.getSucculents() == null || size.getSucculents().isEmpty()) {
            return "Danh sách sen đá không được để trống.";
        }

        for (CreateOrUpdateProductRequest.Succulent succulent : size.getSucculents()) {
            if (succulent.getSizes() == null || succulent.getSizes().isEmpty()) {
                return "Thông tin kích cỡ của sen đá không được để trống.";
            }
            for (CreateOrUpdateProductRequest.SucculentSize succulentSize : succulent.getSizes()) {
                if (succulentSize.getQuantity() <= 0) {
                    return "Số lượng của sen đá phải lớn hơn 0.";
                }
            }
        }

        if (size.getPot() == null) {
            return "Thông tin chậu không được để trống.";
        }
        if (size.getPot().getName() == null || size.getPot().getName().trim().isEmpty()) {
            return "Tên chậu không được để trống.";
        }
        if (size.getPot().getSize() == null || size.getPot().getSize().trim().isEmpty()) {
            return "Kích cỡ chậu không được để trống.";
        }

        if (size.getSoil() == null) {
            return "Thông tin đất không được để trống.";
        }
        if (size.getSoil().getName() == null || size.getSoil().getName().trim().isEmpty()) {
            return "Tên đất không được để trống.";
        }
        if (size.getSoil().getMassAmount() <= 0) {
            return "Khối lượng đất phải lớn hơn 0.";
        }

        CreateOrUpdateProductRequest.Decoration decoration = size.getDecoration();
        if (decoration != null && decoration.isIncluded()) {
            if (decoration.getDetails() == null || decoration.getDetails().isEmpty()) {
                return "Nếu trang trí được chọn, danh sách chi tiết không được để trống.";
            }
            for (CreateOrUpdateProductRequest.DecorationDetail detail : decoration.getDetails()) {
                if (detail.getName() == null || detail.getName().trim().isEmpty()) {
                    return "Tên vật trang trí không được để trống.";
                }
                if (detail.getQuantity() <= 0) {
                    return "Số lượng vật trang trí phải lớn hơn 0.";
                }
            }
        }

        if (request.getImages() == null || request.getImages().isEmpty()) {
            return "Hình ảnh tham khảo không được để trống.";
        }

        if (request.getOccasion() == null || request.getOccasion().trim().isEmpty()) {
            return "Dịp đặc biệt không được để trống.";
        }

        if (request.getOccasion().length() > 100) {
            return "Dịp đặc biệt không được vượt quá 100 ký tự.";
        }

        return "";
    }

    private Map<String, Map<String, Object>> buildSizeData(CreateOrUpdateProductRequest request) {
        Map<String, Map<String, Object>> sizes = new HashMap<>();

        for (CreateOrUpdateProductRequest.Size size : request.getSizes()) {
            Map<String, Object> data = new HashMap<>(productService.buildSizeMap(size));

            Map<String, Object> succulentData = (Map<String, Object>) data.get("succulents");

            succulentData.remove("ids");

            List<Map<String, Object>> succulentDataMap = (List<Map<String, Object>>) ((Map<String, Object>) data.get("succulents")).get("list");

            succulentData.remove("list");

            data.replace("succulents", succulentDataMap);

            sizes.put(size.getName(), data);
        }

        return sizes;
    }

    @Override
    public ResponseEntity<ResponseObject> viewCustomProductRequest() {
        return ResponseBuilder.build(HttpStatus.OK, "", viewCustomProduct(customProductRequestRepo.findAll()));
    }

    @Override
    public ResponseEntity<ResponseObject> viewCustomProductRequest(HttpServletRequest request) {

        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);
        assert account != null;
        return ResponseBuilder.build(HttpStatus.OK, "", viewCustomProduct(customProductRequestRepo.findAll()
                .stream().filter(customRequest -> Objects.equals(customRequest.getBuyer().getId(), account.getUser().getId())).toList()
        ));
    }

    @Override
    public ResponseEntity<ResponseObject> getCustomRequestData(HttpServletRequest request) {
        Account account = CookieUtil.extractAccountFromCookie(request, jwtService, accountRepo);

        List<CustomProductRequest> requests;
        if (account == null) {
            requests = customProductRequestRepo.findAll();
        } else {
            requests = customProductRequestRepo.findAll().stream()
                    .filter(customRequest -> Objects.equals(customRequest.getBuyer().getId(), account.getUser().getId()))
                    .toList();
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("succulents", productService.viewSucculentList().getBody().getData());
        payload.put("accessories", productService.getAccessories("all").getBody().getData());
        return ResponseBuilder.build(HttpStatus.OK, "", payload);
    }

    private List<Map<String, Object>> buildCustomProductDataOnly(List<CustomProductRequest> requests) {
        return requests.stream()
                .map(cp -> {
                    Map<String, Object> rawData = MapUtils.getMapFromObject(cp.getData());

                    Map<String, Object> customData = new HashMap<>();
                    for (Map.Entry<String, Object> entry : rawData.entrySet()) {
                        if (!entry.getKey().startsWith("v_")) {
                            customData.put(entry.getKey(), entry.getValue());
                        }
                    }

                    Map<String, Object> response = new HashMap<>();
                    response.put("id", cp.getId());
                    response.put("customData", productService.buildProductSizeResponse(customData));
                    return response;
                })
                .toList();
    }


    private ResponseEntity<ResponseObject> viewCustomProduct(List<CustomProductRequest> requests) {
        List<Map<String, Object>> response = requests.stream().map(
                cp -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", cp.getId());
                    data.put("buyer", EntityResponseBuilder.buildUserResponse(cp.getBuyer()));
                    data.put("customData", productService.buildProductSizeResponse(MapUtils.getMapFromObject(cp.getData())));
                    data.put("status", cp.getStatus().getValue());
                    data.put("occasion", cp.getOccasion());
                    data.put("createdAt", cp.getCreatedAt());
                    Map<String, Object> dataVersions = (Map<String, Object>) cp.getData();

                    int latestVer = dataVersions.keySet().stream()
                            .filter(key -> key.startsWith("v_"))
                            .mapToInt(key -> Integer.parseInt(key.substring(2)))
                            .max()
                            .orElse(0);

                    Map<String, Object> latestData = MapUtils.getMapFromObject(dataVersions, "v_" + latestVer);

                    data.put("latestVersion", latestData);

                    List<Map<String, Object>> versions = new ArrayList<>();
                    for(Map.Entry<String, Object> entry : dataVersions.entrySet()) {
                        if (entry.getKey().startsWith("v_")) {
                            Map<String, Object> versionData = (Map<String, Object>) entry.getValue();
                            versionData.put("version", entry.getKey());
                            versions.add(versionData);
                        }
                    }
                    data.put("versions", versions.isEmpty() ? new ArrayList<>() : versions);
                    return data;
                }
        ).toList();

        return ResponseBuilder.build(HttpStatus.OK, "", response);
    }


    @Override
    public ResponseEntity<ResponseObject> viewCustomProductRequestDetail(int id) {
        CustomProductRequest request = customProductRequestRepo.findById(id).orElse(null);
        if (request == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Custom product not found", null);
        }
        Map<String, Object> data = MapUtils.getMapFromObject(request.getData());
        // Separate customData and design versions
        Map<String, Object> customData = new HashMap<>();
        List<Map<String, Object>> versions = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("v_")) {
                Map<String, Object> versionData = MapUtils.getMapFromObject(entry.getValue());
                versionData.put("version", entry.getKey());
                versions.add(versionData);
            } else {
                customData.put(entry.getKey(), entry.getValue());
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("id", request.getId());
        response.put("buyer", EntityResponseBuilder.buildUserResponse(request.getBuyer()));
        response.put("customData", productService.buildProductSizeResponse(customData));
        response.put("occasion", request.getOccasion());
        if (request.getStatus().equals(Status.REJECT)) {
            response.put("rejectReason", request.getStatus());
        }

        int latestVer = data.keySet().stream()
                .filter(key -> key.startsWith("v_"))
                .mapToInt(key -> Integer.parseInt(key.substring(2)))
                .max()
                .orElse(0);
        Map<String, Object> latestData = MapUtils.getMapFromObject(data, "v_" + latestVer);

        response.put("latestVersion", latestData);
        response.put("versions", versions.isEmpty() ? new ArrayList<>() : versions);
        response.put("status", request.getStatus().getValue());
        response.put("createdAt", request.getCreatedAt());
        return ResponseBuilder.build(HttpStatus.OK, "", response);
    }

    @Override
    public ResponseEntity<ResponseObject> viewCustomProductRequestDetailVersion(int id) {
        CustomProductRequest request = customProductRequestRepo.findById(id).orElse(null);
        if (request == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Custom product not found", null);
        }
        Map<String, Object> data = (Map<String, Object>) request.getData();
        List<Map<String, Object>> versions = new ArrayList<>();
        for(Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("v_")) {
            Map<String, Object> versionData = (Map<String, Object>) entry.getValue();
            versionData.put("version", entry.getKey());
            versions.add(versionData);
            }
        }
        return ResponseBuilder.build(HttpStatus.OK, "View all versions of custom request product", versions);
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateCustomProductRequestDesignImage(UpdateCustomProductRequestDesignImageRequest request, boolean approved) {
        CustomProductRequest customProductRequest = customProductRequestRepo.findById(request.getId()).orElse(null);
        if (customProductRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Custom product not found", null);
        }

        if (!approved) {
            customProductRequest.setStatus(Status.REJECT);
            customProductRequest.setReason(request.getRejectReason());
            customProductRequestRepo.save(customProductRequest);
            return ResponseBuilder.build(HttpStatus.OK, "Custom product rejected", null);
        }

        if (request.getImages().isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Designed image is empty", null);
        }

        Map<String, Object> data = new HashMap<>();

        if (customProductRequest.getStatus().equals(Status.PENDING)) {
            customProductRequest.setStatus(Status.APPROVE);

             //LẤY data cũ TRƯỚC khi thay đổi
            Map<String, Object> originalData = MapUtils.getMapFromObject(customProductRequest.getData());
             //Giữ lại customData để set vào Product
            Map<String, Map<String, Object>> customDataForProduct = (Map<String, Map<String, Object>>) (Object) originalData;

            //Thêm version mới
            data.put("v_1", buildCustomProductRequestDesignImageVersionData(request, 0, "pending"));
              //Merge với data cũ (giữ lại custom: {...})
            data.putAll(originalData);

            customProductRequest.setData(data);
            customProductRequestRepo.save(customProductRequest);

            String productName = "Custom product by " + customProductRequest.getBuyer().getName();
            if (customProductRequest.getOccasion() != null && !customProductRequest.getOccasion().trim().isEmpty()) {
                productName += " - " + customProductRequest.getOccasion();
            }
            
            String description;
            if (customProductRequest.getOccasion() != null && !customProductRequest.getOccasion().trim().isEmpty()) {
                description = "Sản phẩm custom cho dịp: " + customProductRequest.getOccasion();
            } else {
                description = "Sản phẩm custom";
            }

            Product product = productRepo.save(
                    Product.builder()
                            .name(productName)
                            .description(description)
                            .size(customDataForProduct)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .status(null)
                            .privacy(false)
                            .createdBy(customProductRequest.getBuyer().getName())
                            .build()
            );

            product.setStatus(productService.checkProductStatus(product) ? Status.AVAILABLE : Status.OUT_OF_STOCK);
            productRepo.save(product);

            return ResponseBuilder.build(HttpStatus.OK, "Custom product approved", null);
        }

        data = MapUtils.getMapFromObject(customProductRequest.getData());
        
        // Find the latest version by filtering v_* keys
        int latestVer = data.keySet().stream()
                .filter(key -> key.startsWith("v_"))
                .mapToInt(key -> Integer.parseInt(key.substring(2)))
                .max()
                .orElse(0);

        Map<String, Object> latestData = MapUtils.getMapFromObject(data, "v_" + latestVer);
        latestData.replace("status", "fixed");
        data.replace("v_" + latestVer, latestData);
        data.put("v_" + (latestVer + 1), buildCustomProductRequestDesignImageVersionData(request, latestVer, "pending"));

        customProductRequest.setStatus(Status.FIXED);

        customProductRequest.setData(data);
        customProductRequestRepo.save(customProductRequest);
        return ResponseBuilder.build(HttpStatus.OK, "Custom product revision request submitted", null);
    }

    private Map<String, Object> buildCustomProductRequestDesignImageVersionData(UpdateCustomProductRequestDesignImageRequest request, int previousVer, String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("images", request.getImages().stream().map(UpdateCustomProductRequestDesignImageRequest.Image::getUrl).toList());
        data.put("createDate", LocalDateTime.now());
        data.put("status", status);
        data.put("type", previousVer == 0 ? "design" : "re-design");
        data.put("parentVersion", previousVer == 0 ? "" : previousVer + 1);
        return data;
    }

    @Override
    public ResponseEntity<ResponseObject> createRevision(CreateRevisionRequest request) {
        CustomProductRequest customProductRequest = customProductRequestRepo.findById(request.getId()).orElse(null);
        if (customProductRequest == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Custom product not found", null);
        }

        if (!customProductRequest.getStatus().equals(Status.APPROVE) && !customProductRequest.getStatus().equals(Status.FIXED)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Custom product not approved", null);
        }

        Map<String, Object> data = MapUtils.getMapFromObject(customProductRequest.getData());

        int latestVer = data.keySet().stream()
                .filter(key -> key.startsWith("v_"))
                .mapToInt(key -> Integer.parseInt(key.substring(2)))
                .max()
                .orElse(0);

        // Get the latest version data and add revision info
        Map<String, Object> latestData = MapUtils.getMapFromObject(data, "v_" + latestVer);
        latestData.put("revisionContent", request.getComment());
        latestData.put("revisionDate", LocalDateTime.now());
        data.replace("v_" + latestVer, latestData);

        customProductRequest.setData(data);
        customProductRequest.setStatus(Status.FIXING);
        customProductRequestRepo.save(customProductRequest);
        return ResponseBuilder.build(HttpStatus.OK, "Submit revision request", null);
    }

}
