package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.AddWishListItemRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<ResponseObject> viewSucculentList(HttpServletRequest httpRequest) {
        return productService.viewSucculentList(httpRequest);
    }

    @PostMapping("/succulent")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createSucculent(@RequestBody CreateSucculentRequest request, HttpServletRequest httpRequest) {
        return productService.createSucculent(request, httpRequest);
    }

    @PutMapping("/succulent")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateSucculent(@RequestBody UpdateSucculentRequest request, HttpServletRequest httpRequest) {
        return productService.updateSucculent(request, httpRequest);
    }

    //=================== Accessory =====================\\
    @GetMapping("/accessories")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> getAccessories(HttpServletRequest httpRequest) {
        return productService.getAccessories(httpRequest);
    }

    @PostMapping("/accessory")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createAccessory(@RequestBody CreateAccessoryRequest request, HttpServletRequest httpRequest) {
        return productService.createAccessory(request, httpRequest);
    }

    @PutMapping("/accessory")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateAccessory(@RequestBody UpdateAccessoryRequest request, HttpServletRequest httpRequest) {
        return productService.updateAccessory(request, httpRequest);
    }

    //=================== Product =====================\\
    @PostMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createProduct(@RequestBody ProductCreateRequest request, HttpServletRequest httpRequest) {
        return productService.createProduct(request, httpRequest);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> viewProduct(HttpServletRequest httpRequest) {
        return productService.viewProduct(httpRequest);
    }

    @PutMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateProduct(@RequestBody ProductUpdateRequest request, HttpServletRequest httpRequest) {
        return productService.updateProduct(request, httpRequest);
    }

    @DeleteMapping("/soft/delete{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> deleteProduct(@RequestParam int id, HttpServletRequest httpRequest) {
        return productService.deleteProduct(id, httpRequest);
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

    @GetMapping("/stats/supplier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTotalSupplierCount(HttpServletRequest httpRequest) {
        return productService.getTotalSupplierCount(httpRequest);
    }

    //=================== wish list =====================\\
    @PostMapping("/wishlist/item")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> addItemToWishList(@RequestBody AddWishListItemRequest request) {
        return productService.addItemToWishList(request);
    }

    @GetMapping("/wishlist/items")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> getItemsFromWishList() {
        return productService.getItemsFromWishList();
    }

    @DeleteMapping("/wishlist/item")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> removeItemFromWishList(@RequestParam Integer productId) {
        return productService.removeItemFromWishList(productId);
    }

    @DeleteMapping("/wishlist/items")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> removeAllItemsFromWishList() {
        return productService.removeAllItemsFromWishList();
    }
}
