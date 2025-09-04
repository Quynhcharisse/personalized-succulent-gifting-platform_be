package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.ReceiveGoodsRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface ProductService {

    ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request);

    ResponseEntity<ResponseObject> getSucculents();

    ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request);

    ResponseEntity<ResponseObject> createAccessory(CreateAccessoryRequest request);

    ResponseEntity<ResponseObject> getAccessories();

    ResponseEntity<ResponseObject> updateAccessory(UpdateAccessoryRequest request);

    ResponseEntity<ResponseObject> receiveGoods(ReceiveGoodsRequest request);

    ResponseEntity<ResponseObject> createProduct(ProductCreateRequest request);

    ResponseEntity<ResponseObject> viewProduct();

    ResponseEntity<ResponseObject> updateProduct(ProductUpdateRequest request);

    ResponseEntity<ResponseObject> customRequestListByBuyer(HttpServletRequest request);

    ResponseEntity<ResponseObject> createCustomRequest(CreateCustomRequest request);

    ResponseEntity<ResponseObject> updateCustomRequest(UpdateCustomRequestRequest request);

    ResponseEntity<ResponseObject> deleteCustomRequest(DeleteCustomRequestRequest request);
}
