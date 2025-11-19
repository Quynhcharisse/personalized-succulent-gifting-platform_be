package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateOrderRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<ResponseObject> createOrder(@RequestBody CreateOrderRequest request, HttpServletRequest httpServletRequest){
        return orderService.createOrder(request, httpServletRequest);
    }

    @GetMapping()
    public ResponseEntity<ResponseObject> listOrders(HttpServletRequest httpServletRequest){
        return orderService.getOrders(httpServletRequest);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ResponseObject> getOrderDetail(@PathVariable int id){
        return orderService.getOrderDetail(id);
    }
}
