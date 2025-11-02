package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.GhnApiService;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GhnApiServiceImpl implements GhnApiService {

    private static final String BASE_URL = "https://online-gateway.ghn.vn/shiip/public-api";
    private static final String TOKEN = "19f67693-ab42-11f0-92ee-ce7c7f98c75e";
    private static final String SHOP_ID = "6068265";

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", TOKEN);
        headers.set("ShopId", SHOP_ID);
        return headers;
    }

    public ResponseEntity<ResponseObject> getProvinces() {
        String url = BASE_URL + "/master-data/province";
        HttpEntity<Void> entity = new HttpEntity<>(null, defaultHeaders());
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        List<Map<String, Object>> result = (List<Map<String, Object>>) res.getBody().get("data");
        return ResponseBuilder.build(HttpStatus.OK, "Hiển thị danh sách các tỉnh/thành Việt Nam thành công", result);
    }

    public ResponseEntity<ResponseObject> getDistricts(Integer provinceId) {
        String url = BASE_URL + "/master-data/district?province_id=" + provinceId;
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        var result = (List<Map<String, Object>>) res.getBody().get("data");
        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách quận/huyện theo province_id thành công", result);
    }

    public ResponseEntity<ResponseObject> getWards(Integer districtId) {
        String url = BASE_URL + "/master-data/ward?district_id=" + districtId;
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        var result = (List<Map<String, Object>>) res.getBody().get("data");
        return ResponseBuilder.build(HttpStatus.OK, "Lấy danh sách phường/xã theo district_id thành công", result);
    }


}
