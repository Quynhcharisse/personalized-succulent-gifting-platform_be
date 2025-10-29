package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.AddWishListItemRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateCustomProductRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateAccessoryRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateRevisionRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateSucculentRequest;
import com.exe201.group1.psgp_be.dto.requests.CreateOrUpdateProductRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomProductRequestDesignImageRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateSucculentRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.models.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {

    //--------------------------------------------SUCCULENT--------------------------------------------//
    ResponseEntity<ResponseObject> createSucculent(CreateSucculentRequest request);

    ResponseEntity<ResponseObject> viewSucculentList();

    ResponseEntity<ResponseObject> updateSucculent(UpdateSucculentRequest request);

    //--------------------------------------------ACCESSORY--------------------------------------------//
    ResponseEntity<ResponseObject> createOrUpdateAccessory(CreateOrUpdateAccessoryRequest request);

    ResponseEntity<ResponseObject> getAccessories(String type);

    //--------------------------------------------PRODUCT--------------------------------------------//
    ResponseEntity<ResponseObject> createOrUpdateProduct(CreateOrUpdateProductRequest request);

    Map<String, Object> buildSizeMap(CreateOrUpdateProductRequest.Size size);

    boolean checkProductStatus(Product product);

    ResponseEntity<ResponseObject> viewProduct();

    List<Map<String, Object>> buildProductSizeResponse(Map<String, Object> size);

    ResponseEntity<ResponseObject> deactivateProduct(int id);

    //--------------------------------------------WISHLIST--------------------------------------------//
    ResponseEntity<ResponseObject> addItemToWishList(AddWishListItemRequest item);

    ResponseEntity<ResponseObject> getItemsFromWishList();

    ResponseEntity<ResponseObject> removeItemFromWishList(Integer id);

    ResponseEntity<ResponseObject> removeAllItemsFromWishList();
}
