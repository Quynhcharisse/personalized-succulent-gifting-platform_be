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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        CreateOrUpdateProductRequest.Size customSize = CreateOrUpdateProductRequest.Size.builder()
                .name("custom")
                .succulents(request.getSize().getSucculents())
                .pot(request.getSize().getPot())
                .soil(request.getSize().getSoil())
                .decoration(request.getSize().getDecoration())
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
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return ResponseBuilder.build(HttpStatus.CREATED, "Tạo yêu cầu điện cây thành công", null);
    }

    private String validateCreateCustomProductRequest(CreateCustomProductRequestRequest request) {
        String error = "";
        // TODO: validate here
        return error;
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
        List<Map<String, Object>> response = customProductRequestRepo.findAll().stream().map(
                cp -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", cp.getId());
                    data.put("buyer", EntityResponseBuilder.buildUserResponse(cp.getBuyer()));
                    data.put("status", cp.getStatus().getValue());
                    data.put("createdAt", cp.getCreatedAt());
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

        Map<String, Object> response = new HashMap<>();
        response.put("id", request.getId());
        response.put("buyer", EntityResponseBuilder.buildUserResponse(request.getBuyer()));
        response.put("customData", productService.buildProductSizeResponse(MapUtils.getMapFromObject(request.getData())));
        response.put("designImage", request.getDesignImage());
        response.put("status", request.getStatus().getValue());
        response.put("createdAt", request.getCreatedAt());
        return ResponseBuilder.build(HttpStatus.OK, "", response);
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
            customProductRequestRepo.save(customProductRequest);
            return ResponseBuilder.build(HttpStatus.OK, "Custom product rejected", null);
        }

        if (request.getImages().isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Designed image is empty", null);
        }

        Map<String, Object> data = new HashMap<>();

        if (customProductRequest.getStatus().equals(Status.PENDING)) {
            customProductRequest.setStatus(Status.APPROVE);

            data.put("v_1", buildCustomProductRequestDesignImageVersionData(request, 0));

            customProductRequest.setData(data);
            customProductRequestRepo.save(customProductRequest);

            Product product = productRepo.save(
                    Product.builder()
                            .name("Custom product by " + customProductRequest.getBuyer().getName())
                            .description("")
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
        int latestVer = data.keySet().size();

        data.put("v_" + (latestVer + 1), buildCustomProductRequestDesignImageVersionData(request, latestVer));

        customProductRequest.setData(data);
        customProductRequestRepo.save(customProductRequest);
        return ResponseBuilder.build(HttpStatus.OK, "Custom product revision request submitted", null);
    }

    private Map<String, Object> buildCustomProductRequestDesignImageVersionData(UpdateCustomProductRequestDesignImageRequest request, int previousVer) {
        Map<String, Object> data = new HashMap<>();
        data.put("images", request.getImages().stream().map(UpdateCustomProductRequestDesignImageRequest.Image::getUrl).toList());
        data.put("createDate", LocalDateTime.now());
        data.put("status", "pending");
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

        if (!customProductRequest.getStatus().equals(Status.APPROVE)) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Custom product not approved", null);
        }

        Map<String, Object> data = MapUtils.getMapFromObject(customProductRequest.getData());
        int latestVer = data.keySet().size();

        Map<String, Object> latestData = MapUtils.getMapFromObject(data, "v_" + (latestVer + 1));
        latestData.put("revisionContent", request.getComment());
        latestData.put("revisionDate", LocalDateTime.now());

        data.replace("v_" + latestVer, latestData);

        customProductRequest.setData(data);
        customProductRequestRepo.save(customProductRequest);
        return ResponseBuilder.build(HttpStatus.OK, "Submit revision request", null);
    }
}
