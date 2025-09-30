package com.exe201.group1.psgp_be.services;

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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface ProductService {

    //--------------------------------------------SUPPLIER--------------------------------------------//
    ResponseEntity<ResponseObject> createSupplier(CreateSupplierRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getSupplierList(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateSupplier(UpdateSupplierRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateSupplierStatus(UpdateSupplierStatusRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> getTotalSupplierCount(HttpServletRequest httpRequest);


    //--------------------------------------------SUCCULENT--------------------------------------------//
    ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewSucculentList(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request, HttpServletRequest httpRequest);


    //--------------------------------------------ACCESSORY--------------------------------------------//
    ResponseEntity<ResponseObject> createAccessory(CreateAccessoryRequest request);

    ResponseEntity<ResponseObject> getAccessories(String type);

    //--------------------------------------------PRODUCT--------------------------------------------//
    ResponseEntity<ResponseObject> createProduct(ProductCreateRequest request, HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> viewProduct(HttpServletRequest httpRequest);

    ResponseEntity<ResponseObject> updateProduct(ProductUpdateRequest request, HttpServletRequest httpRequest);
    
    ResponseEntity<ResponseObject> deleteProduct(int id, HttpServletRequest httpRequest);


    //--------------------------------------------CUSTOM REQUEST--------------------------------------------//
    ResponseEntity<ResponseObject> customRequestListByBuyer(HttpServletRequest request);

    ResponseEntity<ResponseObject> createCustomRequest(CreateCustomRequest request);

    ResponseEntity<ResponseObject> updateCustomRequest(UpdateCustomRequestRequest request);

    ResponseEntity<ResponseObject> deleteCustomRequest(DeleteCustomRequestRequest request);


    //--------------------------------------------WISHLIST--------------------------------------------//
    ResponseEntity<ResponseObject> addItemToWishList(AddWishListItemRequest item);

    ResponseEntity<ResponseObject> getItemsFromWishList();

    ResponseEntity<ResponseObject> removeItemFromWishList(Integer id);

    ResponseEntity<ResponseObject> removeAllItemsFromWishList();
}
