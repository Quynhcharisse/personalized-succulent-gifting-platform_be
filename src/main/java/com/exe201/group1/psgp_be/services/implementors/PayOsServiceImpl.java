package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.ConfirmPaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.requests.CreatePaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Status;
import com.exe201.group1.psgp_be.enums.Type;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.Order;
import com.exe201.group1.psgp_be.models.OrderDetail;
import com.exe201.group1.psgp_be.models.Product;
import com.exe201.group1.psgp_be.models.ShippingAddress;
import com.exe201.group1.psgp_be.models.Transaction;
import com.exe201.group1.psgp_be.models.User;
import com.exe201.group1.psgp_be.repositories.AccountRepo;
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
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayOsServiceImpl implements PayOsService {

    private final ShippingAddressRepo shippingAddressRepo;
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

        for( CreatePaymentUrlRequest.ProductData product : request.getProducts())
        {
            totalAmount = product.getPrice() * product.getQuantity() + totalAmount;
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

            Order order = Order.builder()
                    .buyer(buyer)
                    .orderCode(request.getOrderCode())
                    .orderDate(LocalDateTime.now())
                    .status(Status.PACKAGING)
                    .shippingFee(BigDecimal.valueOf(request.getShippingFee()))
                    .orderDetailList(new ArrayList<>())
                    .build();

            BigDecimal totalAmount = BigDecimal.ZERO;

            for(ConfirmPaymentUrlRequest.ProductData data : request.getProducts()){
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

        }
      productService.restoreQuantityOfFailedPayment(request.getProducts());
        return ResponseBuilder.build(HttpStatus.OK, "Ok", null);
    }






}
