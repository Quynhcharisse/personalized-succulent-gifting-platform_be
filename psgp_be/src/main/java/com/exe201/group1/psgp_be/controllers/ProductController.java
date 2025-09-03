package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
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


                        //=================== Product =====================\\
    @PostMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createProduct(ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> viewProduct() {
        return productService.viewProduct();
    }

    @PutMapping("/")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> updateProduct(ProductUpdateRequest request) {
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
    public ResponseEntity<ResponseObject> createCustomRequest(CreateCustomRequest request) {
        return productService.createCustomRequest(request);
    }

    @PutMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> updateCustomRequest(UpdateCustomRequestRequest request) {
        return productService.updateCustomRequest(request);
    }

    @DeleteMapping("/custom/requests")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ResponseObject> deleteCustomRequest(DeleteCustomRequestRequest request) {
        return productService.deleteCustomRequest(request);
    }
}
