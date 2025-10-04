package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.AddWishListItemRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductCreateRequest;
import com.exe201.group1.psgp_be.dto.requests.ProductUpdateRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSupplierStatusRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
@Tag(name = "Product", description = "APIs for product")
public class ProductController {
    private final ProductService productService;

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
    public ResponseEntity<ResponseObject> getAccessories(@RequestParam(name = "t", defaultValue = "all") String type) {
        return productService.getAccessories(type);
    }

    @PostMapping("/accessory")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ResponseObject> createAccessory(@RequestBody CreateAccessoryRequest request) {
        return productService.createAccessory(request);
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
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable int id, HttpServletRequest httpRequest) {
        return productService.deleteProduct(id, httpRequest);
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
