package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CreateCustomRequest;
import com.exe201.group1.psgp_be.dto.requests.DeleteCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.requests.UpdateCustomRequestRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.CustomRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomRequestServiceImpl implements CustomRequestService {

    // =========================== Custom Request ========================== \\
    @Override
    public ResponseEntity<ResponseObject> customRequestListByBuyer(HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> createCustomRequest(CreateCustomRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> updateCustomRequest(UpdateCustomRequestRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseObject> deleteCustomRequest(DeleteCustomRequestRequest request) {
        return null;
    }
}
