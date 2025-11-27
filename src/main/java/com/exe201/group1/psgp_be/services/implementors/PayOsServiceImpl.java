package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CheckQuantityInStorageRequest;
import com.exe201.group1.psgp_be.dto.requests.ConfirmPaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomProductRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateProductRequest;
import com.exe201.group1.psgp_be.dto.requests.CreatePaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.enums.Type;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.CustomProductRequest;
import com.exe201.group1.psgp_be.models.Order;
import com.exe201.group1.psgp_be.models.OrderDetail;
import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.models.ShippingAddress;
import com.exe201.group1.psgp_be.models.Transaction;
import com.exe201.group1.psgp_be.models.User;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
import com.exe201.group1.psgp_be.repositories.CustomProductRequestRepo;
import com.exe201.group1.psgp_be.repositories.OrderRepo;
import com.exe201.group1.psgp_be.repositories.ProductRepo;
import com.exe201.group1.psgp_be.repositories.ShippingAddressRepo;
import com.exe201.group1.psgp_be.repositories.TransactionRepo;
import com.exe201.group1.psgp_be.services.JWTService;
import com.exe201.group1.psgp_be.services.PayOsService;
import com.exe201.group1.psgp_be.services.ProductService;
import com.exe201.group1.psgp_be.utils.CookieUtil;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayOsServiceImpl implements PayOsService {

    private final ShippingAddressRepo shippingAddressRepo;
    private final CustomProductRequestRepo customProductRequestRepo;
    @Value("${client.return.url}")
    String clientReturnUrl;

    private final JWTService jwtService;
    private final AccountRepo accountRepo;
    private final  ProductService productService;

    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final TransactionRepo transactionRepo;

    @Override
    public ResponseEntity<?> createPaymentUrl(CreatePaymentUrlRequest request) {

        long totalAmount = 0;

        if(request.isCustomRequest())
        {
          totalAmount = request.getAmount();
        } else {
            for (CreatePaymentUrlRequest.ProductData product : request.getProducts()) {
                totalAmount = product.getPrice() * product.getQuantity() + totalAmount;
            }
        }

        String clientId = "4d90388d-fd71-47c0-a672-778405bbe418";
        String apiKey = "9296bfcc-7c4e-400f-b290-23ce1e9a1c2b";
        String checksumKey = "eb51df336f9a5a174a4a2f15faf6ee433dbc619a83ede6b8a8803a6179445013";

        PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

        long orderCode = System.currentTimeMillis() / 1000;

        if(clientReturnUrl == null || clientReturnUrl.isEmpty()){
            clientReturnUrl = "http://localhost:5173/buyer/payment";
        }
        String returnUrl = clientReturnUrl;

        long finalAmount = (long) Math.ceil(totalAmount + request.getShippingFee());

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .expiredAt((System.currentTimeMillis() / 1000) + (5 * 60)) // ✅ Unix timestamp (seconds)
                .returnUrl(returnUrl)
                .cancelUrl(returnUrl)
                .amount((int) finalAmount)
                .description("Thanh toán đơn hàng")
                .build();

        try {
            CheckoutResponseData result = payOS.createPaymentLink(paymentData);

            String embeddedUrl = result.getCheckoutUrl().replace("/web/", "/embedded/");

            return ResponseEntity.ok(Map.of(
                    "checkoutUrl", embeddedUrl,
                    "paymentLinkId", result.getPaymentLinkId(),
                    "orderCode", result.getOrderCode(),
                    "status", result.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ResponseObject> confirmPayment(ConfirmPaymentUrlRequest request, HttpServletRequest httpServletRequest) {

        if(request.isSuccess()){

            Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
            User buyer = account.getUser();

            if(request.isCustomRequest()) {
                ConfirmPaymentUrlRequest.Size requestSize = request.getSize();

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

               CustomProductRequest customProductRequest = customProductRequestRepo.save(
                        CustomProductRequest.builder()
                                .buyer(account.getUser())
                                .data(data)
                                .designImage(null)
                                .status(Status.PENDING)
                                .occasion(request.getOccasion().trim())
                                .createdAt(LocalDateTime.now())
                                .build()
                );
                Transaction transaction = Transaction.builder()
                        .amount(request.getAmount()) // set tạm, lát nữa cộng lại
                        .type(Type.PAYMENT)      // enum PAYMENT/REFUND...
                        .customProductRequest(customProductRequest)
                        .build();
                transactionRepo.save(transaction);
            } else {
                Order order = Order.builder()
                        .buyer(buyer)
                        .orderCode(request.getOrderCode())
                        .orderDate(LocalDateTime.now())
                        .status(Status.PACKAGING)
                        .shippingFee(BigDecimal.valueOf(request.getShippingFee()))
                        .orderDetailList(new ArrayList<>())
                        .build();

                BigDecimal totalAmount = BigDecimal.ZERO;

                for (ConfirmPaymentUrlRequest.ProductData data : request.getProducts()) {
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
                        .amount(request.getAmount()) // set tạm, lát nữa cộng lại
                        .type(Type.PAYMENT)      // enum PAYMENT/REFUND...
                        .order(order)
                        .build();
                transactionRepo.save(transaction);
            }
        }
        else
        {
            if(request.isCustomRequest()){
                productService.restoreQuantityInStorage(request);
            } else {
                productService.restoreQuantityOfFailedPayment(request.getProducts());
            }
        }
        return ResponseBuilder.build(HttpStatus.OK, "Ok", null);
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

}
