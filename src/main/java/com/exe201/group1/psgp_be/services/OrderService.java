package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    //TODO: no usages function
    ResponseEntity<ResponseObject> getOrderDetailByOrderId(int orderId);
}
