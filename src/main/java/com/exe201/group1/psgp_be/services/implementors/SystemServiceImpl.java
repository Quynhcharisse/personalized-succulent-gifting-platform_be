package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.UpdateBusinessConfigRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.models.AppConfig;
import com.exe201.group1.psgp_be.repositories.AppConfigRepo;
import com.exe201.group1.psgp_be.services.SystemService;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SystemServiceImpl implements SystemService {

    AppConfigRepo appConfigRepo;

    @Override
    public ResponseEntity<ResponseObject> getBusinessConfig() {
        AppConfig businessConfig = appConfigRepo.findByKey("business").orElse(null);
        assert businessConfig != null;
        return ResponseBuilder.build(HttpStatus.OK, "", (Map<String, Object>) businessConfig.getValue());
    }

    @Override
    public ResponseEntity<ResponseObject> updateBusinessConfig(UpdateBusinessConfigRequest request) {
        AppConfig businessConfig = appConfigRepo.findByKey("business").orElse(null);
        assert businessConfig != null;

        Map<String, Object> businessData = (Map<String, Object>) businessConfig.getValue();

        businessData.replace("sellRate", request.getSellRate());

        businessConfig.setValue(businessData);
        appConfigRepo.save(businessConfig);

        return ResponseBuilder.build(HttpStatus.OK, "Update sell rate successfully", null);
    }
}
