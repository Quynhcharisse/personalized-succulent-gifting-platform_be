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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShippingAddressServiceImpl implements ShippingAddressService {

    ShippingAddressRepo shippingAddressRepo;
    UserRepo userRepo;
    private final GhnApiServiceImpl ghnApiServiceImpl;

    @Override
    public ResponseEntity<ResponseObject> createShippingAddress(CreateShippingAddressRequest request) {

        if (request.getShippingAddress().trim().isEmpty()) {
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Địa chỉ không được để trống!", null);
        }

        Account currentAccount = (Account) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User currentUser = userRepo.findByAccountId(currentAccount.getId());

        List<ShippingAddress> existing = shippingAddressRepo.findByUser(currentUser, Sort.by(Sort.Direction.DESC, "createdAt"));

        ShippingAddress newAddress = ShippingAddress.builder()
                .user(currentUser)
                .shippingAddress(request.getShippingAddress())
                .shippingDistrictId(request.getShippingDistrictId())
                .shippingWardCode(request.getShippingWardCode())
                .shipping_province_id(request.getShippingProvinceId())
                .isDefault(existing.isEmpty())
                .createdAt(LocalDateTime.now())
                .build();



        shippingAddressRepo.save(newAddress);

        return ResponseBuilder.build(HttpStatus.CREATED,
                "Tạo địa chỉ giao hàng thành công!", null);
    }

    @Override
    public ResponseEntity<ResponseObject> getShippingAddressList() {

        Account currentAccount = (Account) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User currentUser = userRepo.findByAccountId(currentAccount.getId());

        List<ShippingAddress> addresses = shippingAddressRepo.findByUser(
                currentUser,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseBuilder.build(
                HttpStatus.OK,
                "Hiển thị toàn bộ địa chỉ giao hàng thành công",
                buildShippingAddressList(addresses)
        );
    }

    @Override
    public ResponseEntity<ResponseObject> getDefaultShippingAddress() {
        Account currentAccount = (Account) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User currentUser = userRepo.findByAccountId(currentAccount.getId());

        Optional<ShippingAddress> defaultAddress = shippingAddressRepo
                .findFirstByUserAndIsDefaultTrue(currentUser);

        if (!defaultAddress.isPresent()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Chưa có địa chỉ mặc định!", null);
        }

        return ResponseBuilder.build(
                HttpStatus.OK,
                "Lấy địa chỉ mặc định thành công",
                buildShippingAddress(defaultAddress.get())
        );
    }

    public ResponseEntity<ResponseObject> setDefaultShippingAddress(int selectedAddressId) {
        Account currentAccount = (Account) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User currentUser = userRepo.findByAccountId(currentAccount.getId());
        if (currentUser == null) {
            return ResponseBuilder.build(HttpStatus.UNAUTHORIZED, "Không xác định được người dùng!", null);
        }

        Optional<ShippingAddress> newDefaultOpt = shippingAddressRepo.findById(selectedAddressId);
        if (!newDefaultOpt.isPresent()) {
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ!", null);
        }

        ShippingAddress newDefault = newDefaultOpt.get();

        // ✅ Kiểm tra địa chỉ thuộc đúng user hay không
        if (!newDefault.getUser().getId().equals(currentUser.getId())) {
            return ResponseBuilder.build(HttpStatus.FORBIDDEN, "Địa chỉ không thuộc quyền sở hữu!", null);
        }

        // ✅ Bỏ default cũ (nếu có)
        Optional<ShippingAddress> currentDefaultOpt =
                shippingAddressRepo.findFirstByUserAndIsDefaultTrue(currentUser);

        currentDefaultOpt.ifPresent(addr -> {
            addr.setIsDefault(false);
            shippingAddressRepo.save(addr);
        });

        // ✅ Set địa chỉ mới làm default
        newDefault.setIsDefault(true);
        shippingAddressRepo.save(newDefault);

        return ResponseBuilder.build(
                HttpStatus.OK,
                "Cập nhật địa chỉ mặc định thành công ✅",
                null
        );
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
        response.put("id", shippingAddress.getId());
        response.put("shippingAddress", shippingAddress.getShippingAddress());
        String fullAddress = ghnApiServiceImpl.getWardName(shippingAddress.getShippingWardCode(), shippingAddress.getShippingDistrictId())
                + ", " + ghnApiServiceImpl.getDistrictName(shippingAddress.getShippingDistrictId(), shippingAddress.getShipping_province_id())
                + ", " + ghnApiServiceImpl.getProvinceName(
                shippingAddress.getShipping_province_id()
        );
        response.put("address", fullAddress);
        response.put("isDefault", shippingAddress.getIsDefault());
        response.put("districtId", shippingAddress.getShippingDistrictId());
        response.put("wardCode", shippingAddress.getShippingWardCode());
        return response;
    }


}
