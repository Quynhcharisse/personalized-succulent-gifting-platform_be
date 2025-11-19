package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateOrderRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    //TODO: no usages function
    ResponseEntity<ResponseObject> getOrders(HttpServletRequest httpServletRequest);
    ResponseEntity<ResponseObject> getOrderDetail(int orderId);
    ResponseEntity<ResponseObject> createOrder(CreateOrderRequest request, HttpServletRequest httpServletRequest);
}
