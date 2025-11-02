package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateShippingAddressRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.models.Account;
import com.exe201.group1.psgp_be.models.ShippingAddress;
import com.exe201.group1.psgp_be.models.User;
import com.exe201.group1.psgp_be.repositories.ShippingAddressRepo;
import com.exe201.group1.psgp_be.repositories.UserRepo;
import com.exe201.group1.psgp_be.services.ShippingAddressService;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShippingAddressServiceImpl implements ShippingAddressService {

    ShippingAddressRepo shippingAddressRepo;
    UserRepo userRepo;

    @Override
    public ResponseEntity<ResponseObject> CreateShippingAddress(CreateShippingAddressRequest request) {

        if (request.getShippingAddress().trim().isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Địa chỉ không được để trống!", null);
        }

        Account currentAccount = (Account) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User currentUser = userRepo.findByAccountId(currentAccount.getId());

        ShippingAddress newAddress = ShippingAddress.builder()
                .user(currentUser)
                .shippingAddress(request.getShippingAddress())
                .shippingDistrictId(request.getShippingDistrictId())
                .shippingWardCode(request.getShippingWardCode())
                .build();

        boolean existingAddresses = shippingAddressRepo.findByUser(currentUser);

        if (!existingAddresses) {
            newAddress.setIsDefault(true);
        }

        shippingAddressRepo.save(newAddress);

        return ResponseBuilder.build(HttpStatus.CREATED,
                "Tạo địa chỉ giao hàng thành công!", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getShippingAddressList() {
        return ResponseBuilder.build(HttpStatus.OK, "Hiển thị toàn bộ địa chỉ giao hàng thành công", buildShippingAddressList(shippingAddressRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    private List<Map<String, Object>> buildShippingAddressList(List<ShippingAddress> shippingAddressList) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ShippingAddress shippingAddress : shippingAddressList) {
            list.add(buildShippingAddress(shippingAddress));
        }
        return list;
    }

    private Map<String, Object> buildShippingAddress(ShippingAddress shippingAddress) {
        Map<String, Object> response = new HashMap<>();
        response.put("shippingAddress", shippingAddress);
        response.put("districtId", shippingAddress.getShippingDistrictId());
        response.put("wardCode", shippingAddress.getShippingWardCode());
        return response;
    }


}
