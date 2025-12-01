package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.ConfirmPaymentUrlRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrderRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateOrderRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.enums.Role;
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
import java.util.Optional;

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
    private final GhnApiServiceImpl ghnApiServiceImpl;

    // =========================== Order Detail ========================== \\


    public ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request, HttpServletRequest httpServletRequest) {

        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        User buyer = account.getUser();

        if(request.getStatus().trim().toLowerCase().equals("packaging")) {
            Order order = Order.builder()
                    .buyer(buyer)
                    .orderCode(request.getOrderCode()) // hoặc sinh bằng UUID/random generator
                    .orderDate(LocalDateTime.now())
                    .status(Status.PACKAGING)
                    .shippingFee(BigDecimal.valueOf(request.getShippingFee()))
                    .orderDetailList(new ArrayList<>())
                    .build();

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CreateOrderRequest.ProductData data : request.getProducts()) {
                Product product = productRepo.findById(data.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + data.getProductId()));

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
                    .orderCode(request.getOrderCode())
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
        return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid status (packaging, done)", null);
    }

    @Override
    public ResponseEntity<ResponseObject> updateOrder(UpdateOrderRequest request) {

            // 1. Tìm order
            Optional<Order> optionalOrder = orderRepo.findById(request.getOrderId());
            if (optionalOrder.isEmpty()) {
                return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng!", null);
            }
                Order order = optionalOrder.get();

                // 2. Validate trạng thái mới
                Status newStatus;
                try {
                    newStatus = Status.valueOf(request.getAction());
                } catch (IllegalArgumentException e) {
                    return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ!", null);
                }
                // 3. Update trạng thái
                order.setStatus(newStatus);
                orderRepo.save(order);

                return ResponseBuilder.build(HttpStatus.OK, "Cập nhật trạng thái đơn hàng thành công!", null);

    }

    @Override
    public ResponseEntity<ResponseObject> getOrders(HttpServletRequest httpServletRequest) {

        Account account = CookieUtil.extractAccountFromCookie(httpServletRequest, jwtService, accountRepo);
        if(!account.getRole().equals(Role.BUYER) ) {
            return ResponseBuilder.build(HttpStatus.OK, "View all orders successfully", buildOrderList(orderRepo.findAll()));
        }
        User buyer = account.getUser();
        return ResponseBuilder.build(HttpStatus.OK, "View all orders successfully", buildOrderList(orderRepo.findByBuyerOrderByOrderDateDesc(buyer)));
    }

    @Override
    public ResponseEntity<ResponseObject> getOrderDetail(int orderId) {

        Order order = orderRepo.findById(orderId).get();

        if(order == null) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        return ResponseBuilder.build(HttpStatus.OK, "View order detail successfully", buildOrderDetail(order));
    }

    private Map<String, Object> buildOrderDetail(Order order) {
        Map<String, Object> result = new HashMap<>();

        result.put("orderId", order.getId());

        String buyerName = order.getBuyer() != null
                ? order.getBuyer().getName()
                : "N/A";
        result.put("buyerName", buyerName);

        String phoneNumber = order.getBuyer() != null
                ? order.getBuyer().getPhone()
                : "N/A";
        result.put("buyerPhone", phoneNumber);

        String email = order.getBuyer() != null
                ? order.getBuyer().getAccount().getEmail()
                : "N/A";
        result.put("email", email);

        ShippingAddress shippingAddress = order.getShippingAddress();

        if(shippingAddress == null){
            result.put("address", "N/A");
        }
        else {
            String fullAddress = ghnApiServiceImpl.getWardName(shippingAddress.getShippingWardCode(), shippingAddress.getShippingDistrictId())
                    + ", " + ghnApiServiceImpl.getDistrictName(shippingAddress.getShippingDistrictId(), shippingAddress.getShipping_province_id())
                    + ", " + ghnApiServiceImpl.getProvinceName(
                    shippingAddress.getShipping_province_id()
            );
            result.put("address", fullAddress);
        }

        List<Map<String, Object>> orderItems = new ArrayList<>();

        for(OrderDetail orderDetail : order.getOrderDetailList()){
            Map<String, Object> orderItem = new HashMap<>();
            if (orderDetail.getProduct() == null){
               continue;
            }
            orderItem.put("productName",orderDetail.getProduct().getName());
            orderItem.put("sizeName", orderDetail.getSizeName());
            orderItem.put("quantity", orderDetail.getQuantity());
            orderItem.put("price", orderDetail.getPrice());

            orderItems.add(orderItem);
        }

        result.put("orderItems", orderItems);
        result.put("orderDate", order.getOrderDate());
        result.put("totalAmount", order.getTotalAmount());
        result.put("shippingFee", order.getShippingFee());
        result.put("finalAmount", order.getFinalAmount());

        result.put("status", order.getStatus());
        return result;
    }

    private Map<String, Object> buildOrder(Order order) {
        Map<String, Object> result = new HashMap<>();

        result.put("orderId", order.getId());
        result.put("orderDate", order.getOrderDate());
        result.put("finalAmount", order.getFinalAmount());
        result.put("status", order.getStatus());

        return result;
    }

    private List<Map<String, Object>> buildOrderList(List<Order> orders){
        List<Map<String, Object>> result = new ArrayList<>();

        for (Order order : orders) {
            result.add(buildOrder(order));
        }

        return result;
    }

}
