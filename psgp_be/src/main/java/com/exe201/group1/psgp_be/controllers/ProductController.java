package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.ReceiveGoodsRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierStatusRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    //=================== supplier =====================\\
    @PostMapping("/supplier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createSupplier(@RequestBody CreateSupplierRequest request, HttpServletRequest httpRequest) {
        return productService.createSupplier(request, httpRequest);
    }

    @PutMapping("/supplier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateSupplier(@RequestBody UpdateSupplierRequest request, HttpServletRequest httpRequest) {
        return productService.updateSupplier(request, httpRequest);
    }

    @PutMapping("/supplier/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateSupplierStatus(@RequestBody UpdateSupplierStatusRequest request, HttpServletRequest httpRequest) {
        return productService.updateSupplierStatus(request, httpRequest);
    }

    @GetMapping("/supplier/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getSupplierList(HttpServletRequest httpRequest) {
        return productService.getSupplierList(httpRequest);
    }

    //=================== succulent =====================\\
    @GetMapping("/succulents")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> viewAllSucculents() {
        return productService.getSucculents();
    }

    @PostMapping("/succulent")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createSucculent(@RequestBody CreateSucculentRequest request) {
        return productService.createSucculent(request);
    }

    @PutMapping("/succulent")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateSucculent(@RequestBody UpdateSucculentRequest request) {
        return productService.updateSucculent(request);
    }

    //=================== Accessory =====================\\
    @GetMapping("/accessories")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getAccessories() {
        return productService.getAccessories();
    }

    @PostMapping("/accessory")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createAccessory(@RequestBody CreateAccessoryRequest request) {
        return productService.createAccessory(request);
    }

    @PutMapping("/accessory")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateAccessory(@RequestBody UpdateAccessoryRequest request) {
        return productService.updateAccessory(request);
    }

    //================== Nhập hàng từ NCC ====================\\
    @PostMapping("/receive-goods")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> receiveGoods(@RequestBody ReceiveGoodsRequest request) {
        return productService.receiveGoods(request);
    }

    //=================== Product =====================\\
    @PostMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createProduct(@RequestBody ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> viewProduct() {
        return productService.viewProduct();
    }

    @PutMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateProduct(@RequestBody ProductUpdateRequest request) {
        return productService.updateProduct(request);
    }

    //=================== custom request =====================\\
    @GetMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> customRequestListByBuyer(HttpServletRequest request) {
        return productService.customRequestListByBuyer(request);
    }

    @PostMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> createCustomRequest(@RequestBody CreateCustomRequest request) {
        return productService.createCustomRequest(request);
    }

    @PutMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> updateCustomRequest(@RequestBody UpdateCustomRequestRequest request) {
        return productService.updateCustomRequest(request);
    }

    @DeleteMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> deleteCustomRequest(@RequestBody DeleteCustomRequestRequest request) {
        return productService.deleteCustomRequest(request);
    }
}
