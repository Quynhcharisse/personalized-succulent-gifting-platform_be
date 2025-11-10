package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.ConfirmPaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrderRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.enums.Type;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.AppConfig;
import com.exe201.group1.psgp_be.models.Order;
import com.exe201.group1.psgp_be.models.OrderDetail;
import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.models.ShippingAddress;
import com.exe201.group1.psgp_be.models.Succulent;
import com.exe201.group1.psgp_be.models.Transaction;
import com.exe201.group1.psgp_be.models.User;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.AppConfigRepo;
import com.exe201.group1.psgp_be.repositories.OrderDetailRepo;
import com.exe201.group1.psgp_be.repositories.OrderRepo;
import com.exe201.group1.psgp_be.repositories.ProductRepo;
import com.exe201.group1.psgp_be.repositories.ShippingAddressRepo;
import com.exe201.group1.psgp_be.repositories.SucculentRepo;
import com.exe201.group1.psgp_be.repositories.TransactionRepo;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.OrderService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderDetailRepo orderDetailRepo;
    OrderRepo orderRepo;
    AppConfigRepo appConfigRepo;
    AccountRepo accountRepo;
    SucculentRepo succulentRepo;
    JWTService jwtService;
    private final TransactionRepo transactionRepo;
    private final ProductRepo productRepo;
    private final ShippingAddressRepo shippingAddressRepo;

    // =========================== Order Detail ========================== \\


    public ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request, HttpServletRequest httpServletRequest) {

        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        User buyer = account.getUser();
        if(request.getStatus().trim().toLowerCase().equals("shipping")) {
            Order order = Order.builder()
                    .buyer(buyer)
                    .orderCode(request.getOrderCode()) // hoặc sinh bằng UUID/random generator
                    .orderDate(LocalDateTime.now())
                    .status(Status.SHIPPING)
                    .shippingFee(BigDecimal.valueOf(request.getShippingFee()))
                    .orderDetailList(new ArrayList<>())
                    .build();

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CreateOrderRequest.ProductData data : request.getProducts()) {
                Product product = productRepo.findById(data.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + data.getProductId()));

                // 🔹 Tạo chi tiết đơn hàng
                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .product(product)
                        .sizeName(data.getSize())
                        .price(data.getPrice())
                        .quantity(data.getQuantity())
                        .build();

                order.getOrderDetailList().add(detail);

                totalAmount = totalAmount.add(BigDecimal.valueOf(data.getPrice() * data.getQuantity()));
            }
            order.setTotalAmount(totalAmount);
            order.setFinalAmount(totalAmount.add(BigDecimal.valueOf(request.getShippingFee())));

            ShippingAddress shippingAddress = shippingAddressRepo.getById(request.getShippingAddressId());

            order.setShippingAddress(shippingAddress);

            orderRepo.save(order);

            Transaction transaction = Transaction.builder()
                    .amount(totalAmount) // set tạm, lát nữa cộng lại
                    .type(Type.PAYMENT)      // enum PAYMENT/REFUND...
                    .order(order)
                    .build();
            transactionRepo.save(transaction);
            return ResponseBuilder.build(HttpStatus.OK, "Tạo order thành công", null);
        }
        if(request.getStatus().trim().toLowerCase().equals("done")) {

            Order order = Order.builder()
                    .buyer(buyer)
                    .orderCode(request.getOrderCode()) // hoặc sinh bằng UUID/random generator
                    .orderDate(LocalDateTime.now())
                    .status(Status.DONE)
                    .shippingFee(BigDecimal.ZERO)
                    .orderDetailList(new ArrayList<>())
                    .build();

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CreateOrderRequest.ProductData data : request.getProducts()) {
                Product product = productRepo.findById(data.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + data.getProductId()));

                // 🔹 Tạo chi tiết đơn hàng
                OrderDetail detail = OrderDetail.builder()
                        .order(order)
                        .product(product)
                        .sizeName(data.getSize())
                        .price(data.getPrice())
                        .quantity(data.getQuantity())
                        .build();

                order.getOrderDetailList().add(detail);

                totalAmount = totalAmount.add(BigDecimal.valueOf(data.getPrice() * data.getQuantity()));
            }
            order.setTotalAmount(totalAmount);
            order.setFinalAmount(totalAmount);


            orderRepo.save(order);

            Transaction transaction = Transaction.builder()
                    .amount(totalAmount) // set tạm, lát nữa cộng lại
                    .type(Type.PAYMENT)      // enum PAYMENT/REFUND...
                    .order(order)
                    .build();
            transactionRepo.save(transaction);
            return ResponseBuilder.build(HttpStatus.OK, "Tạo order thành công", null);
        }

        return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid status (shipping, done)", null);
    }

    private Map<String, Object> buildOrderDetailProductInfo(Product product) {
        AppConfig accessoryConfig = appConfigRepo.findByKey("accessory").orElse(null);
        assert accessoryConfig != null;
        Map<String, Object> productInfo = new HashMap<>(((Map<String, Object>) product.getSize()));

        Map<String, Object> potConfig = MapUtils.getMapFromObject(accessoryConfig.getValue(), "pot");
        Map<String, Object> soilConfig = MapUtils.getMapFromObject(accessoryConfig.getValue(), "soil");
        Map<String, Object> decoConfig = MapUtils.getMapFromObject(accessoryConfig.getValue(), "decoration");

        Map<String, Object> potData = MapUtils.getMapFromObject(productInfo, "pot");
        Map<String, Object> soilData = MapUtils.getMapFromObject(productInfo, "soil");
        Map<String, Object> decoData = MapUtils.getMapFromObject(productInfo, "decoration");

        List<Map<String, Object>> succulentData = MapUtils.getMapListFromObject(productInfo, "succulents");

        productInfo.replace("pot", buildPotData(potData, potConfig));
        productInfo.replace("soil", buildSoilData(soilData, soilConfig));
        productInfo.replace("decoration", buildDecoData(decoData, decoConfig));
        productInfo.replace("succulents", buildSucculentData(succulentData));
        return productInfo;
    }

    private Map<String, Object> buildPotData(Map<String, Object> potData, Map<String, Object> potConfig) {
        potData = new HashMap<>(potData);

        Map<String, Object> potConfigMap = MapUtils.getMapFromObject(potConfig, potData.get("name").toString());

        potData.put("material", potConfigMap.get("material"));
        potData.put("color", potConfigMap.get("color"));

        Map<String, Object> sizeData = MapUtils.getMapFromObject(potConfigMap, "size", potData.get("size").toString());

        potData.put("height", sizeData.get("potHeight"));
        potData.put("upperCrossSectionArea", sizeData.get("potUpperCrossSectionArea"));
        potData.put("maxSoilMassValue", sizeData.get("maxSoilMassValue"));
        potData.put("price", sizeData.get("price"));

        return potData;
    }

    private Map<String, Object> buildSoilData(Map<String, Object> soilData, Map<String, Object> soilConfig) {
        soilData = new HashMap<>(soilData);
        String soilName = soilData.get("name").toString();
        soilData.remove("name");

        long massAmount = ((Integer) soilData.get("massAmount")).longValue();

        soilData.put(soilName, new HashMap<>(soilData));
        soilData.remove("massAmount");

        soilConfig = MapUtils.getMapFromObject(soilConfig, soilName);
        Map<String, Object> basePricingConfig = MapUtils.getMapFromObject(soilConfig, "basePricing");

        long unitPrice = ((Integer) basePricingConfig.get("price")).longValue();
        int unitMassAmount = (Integer) basePricingConfig.get("massValue");

        soilData.put("totalPrice", unitPrice * (massAmount / unitMassAmount));
        soilData.put("basePricing", basePricingConfig);

        return soilData;
    }

    private Map<String, Object> buildDecoData(Map<String, Object> decoData, Map<String, Object> decoConfig) {
        decoData = new HashMap<>(decoData);

        Map<String, Object> decoDetail = MapUtils.getMapFromObject(decoData, "detail");

        for (String key : decoDetail.keySet()) {
            Integer quantity = (Integer) decoDetail.get(key);
            long configPrice = ((Integer) MapUtils.getMapFromObject(decoConfig, key).get("price")).longValue();
            decoDetail.replace(key, MapUtils.getMapFromObject(Map.of("price", configPrice, "quantity", quantity)));
        }

        return decoData;
    }

    private List<Map<String, Object>> buildSucculentData(List<Map<String, Object>> succulentData) {
        succulentData = succulentData.stream().map(
                succulent -> {
                    Map<String, Object> succulentMap = new HashMap<>(succulent);
                    Map<String, Object> sizeMap = MapUtils.getMapFromObject(succulentMap, "size");

                    for (String key : sizeMap.keySet()) {
                        Integer quantity = (Integer) sizeMap.get(key);

                        Succulent selectedSucculent = succulentRepo.findById((Integer) succulentMap.get("id")).orElse(null);
                        if (selectedSucculent == null) return null;

                        Map<String, Object> sizeData = MapUtils.getMapFromObject(selectedSucculent.getSize(), key);

                        sizeMap.replace(key, MapUtils.getMapFromObject(
                                Map.of(
                                        "quantity", quantity,
                                        "minArea", sizeData.get("minArea"),
                                        "maxArea", sizeData.get("maxArea"),
                                        "price", sizeData.get("price")
                                )
                        ));
                    }

                    succulentMap.replace("size", sizeMap);
                    return succulentMap;
                }
        ).toList();


        return succulentData;
    }

    //TODO: no usages function
    @Override
    public ResponseEntity<ResponseObject> getOrderDetailByOrderId(int orderId) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);

        return ResponseBuilder.build(HttpStatus.OK, "", getOrderDetailByOrder(order));
    }

    private List<Map<String, Object>> getOrderDetailByOrder(Order order) {
        return order.getOrderDetailList().stream().map(
                detail -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", detail.getId());
//                    response.put("product", buildOrderDetailProductResponse(detail));
                    response.put("quantity", detail.getQuantity());
                    response.put("price", detail.getPrice());
                    return response;
                }
        ).toList();
    }

//    private List<Map<String, Object>> buildOrderDetailProductResponse(OrderDetail detail) {
//        Map<String, Object> productInfo = MapUtils.getMapFromObject(detail.getProductInfo());
//
//        return productInfo.keySet().stream().map(
//                key -> {
//                    Map<String, Object> responseData = new HashMap<>();
//                    responseData.put("size", key);
//
//                    Map<String, Object> sizeData = new HashMap<>((Map<String, Object>) productInfo.get(key));
//                    Map<String, Object> potData = MapUtils.getMapFromObject(sizeData.get("pot"));
//                    Map<String, Object> soilData = MapUtils.getMapFromObject(sizeData.get("soil"));
//                    Map<String, Object> decoData = MapUtils.getMapFromObject(sizeData.get("decoration"));
//                    List<Map<String, Object>> succulentData = (List<Map<String, Object>>) sizeData.get("succulents");
//
//                    responseData.put("pot", new HashMap<>(buildODPotResponse(potData)));
//                    responseData.put("soil", new HashMap<>(buildODSoilResponse(soilData)));
//                    responseData.put("decoration", new HashMap<>(buildODDecoResponse(decoData)));
//                    responseData.put("succulents", buildODSucculentResponse(succulentData));
//
//                    return responseData;
//                }
//        ).toList();
//    }

    // OD is Order Detail
    private Map<String, Object> buildODPotResponse(Map<String, Object> potData) {

        return Map.of(
                "name", potData.get("name"),
                "material", potData.get("material"),
                "color", potData.get("color"),
                "size", potData.get("size"),
                "height", potData.get("height"),
                "upperCrossSectionArea", potData.get("upperCrossSectionArea"),
                "maxSoilMassValue", potData.get("maxSoilMassValue"),
                "price", potData.get("price")
        );
    }

    private Map<String, Object> buildODSoilResponse(Map<String, Object> soilData) {
        String soilName = soilData.keySet().stream().findFirst().get();

        Map<String, Object> value = (Map<String, Object>) soilData.get(soilName);

        return Map.of(
                "name", soilName,
                "totalPrice", value.get("totalPrice"),
                "massAmount", value.get("massAmount"),
                "basePricing", value.get("basePricing")
        );
    }

    private Map<String, Object> buildODDecoResponse(Map<String, Object> decoData) {
        if (!((boolean) decoData.get("included"))) {
            return new HashMap<>();
        }

        Map<String, Object> decoDetail = (Map<String, Object>) decoData.get("detail");

        Map<String, Object> response = new HashMap<>();

        response.put("included", decoData.get("included"));
        response.put("detail", decoDetail.keySet().stream().map(
                key -> {
                    Map<String, Object> detail = MapUtils.getMapFromObject(decoDetail.get(key));

                    Map<String, Object> detailMap = new HashMap<>();
                    detailMap.put("name", key);
                    detailMap.put("price", detail.get("price"));
                    detailMap.put("quantity", detail.get("quantity"));
                    return detailMap;
                }
        ).toList());
        return response;
    }

    private List<Map<String, Object>> buildODSucculentResponse(List<Map<String, Object>> succulentData) {
        return succulentData.stream().map(
                succulent -> {
                    Map<String, Object> sizeDetail = MapUtils.getMapFromObject(succulent.get("sizes"));


                    Map<String, Object> response = new HashMap<>(succulent);
                    response.replace("sizes", sizeDetail.keySet().stream().map(
                            key -> {
                                Map<String, Object> detail = MapUtils.getMapFromObject(sizeDetail.get(key));
                                detail.put("name", key);
                                return detail;
                            }
                    ).toList());
                    return response;
                }
        ).toList();
    }
}
