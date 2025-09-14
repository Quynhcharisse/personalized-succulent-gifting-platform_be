package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProcessSaleRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.ReceiveGoodsRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierStatusRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface ProductService {

    ResponseEntity<ResponseObject> createSupplier(CreateSupplierRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getSupplierList(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateSupplier(UpdateSupplierRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateSupplierStatus(UpdateSupplierStatusRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getTotalSupplierCount(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request);

    ResponseEntity<ResponseObject> viewSucculentList();

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

    // =========================== Inventory Management ========================== \\
    ResponseEntity<ResponseObject> processSale(ProcessSaleRequest request, HttpServletRequest httpRequest);
}
