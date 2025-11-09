package com.exe201.group1.psgp_be.services.implementors;

import com.exe201.group1.psgp_be.dto.requests.CaculateFeeRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.GhnApiService;
import com.exe201.group1.psgp_be.utils.ResponseBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import org.springframework.http.HttpHeaders;

import java.util.HashMap;
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

    public ResponseEntity<ResponseObject> caculateFee(CaculateFeeRequest request){

        String url = BASE_URL + "/v2/shipping-order/fee";
        Map<String, Object> requestGhn = new HashMap<>();
        requestGhn.put("service_id", getService(request.getToDistrictId()));
        requestGhn.put("to_district_id", request.getToDistrictId());
        requestGhn.put("to_ward_code", request.getWardCode());

        // Mặc định mỗi item = 200g / quantity = 1
        int defaultWeight = 300;

        List<Map<String, Object>> items = request.getItemNames().stream()
                .map(name -> Map.<String, Object>of(
                        "name", name,
                        "quantity", 1,
                        "weight", defaultWeight
                ))
                .toList();

        int totalWeight = items.size() * defaultWeight;
        requestGhn.put("weight", totalWeight);
        requestGhn.put("items", items);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestGhn, defaultHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        int totalFee = 0;
        try {
            System.out.println("GHN Response: " + response.getBody());
            root = mapper.readTree(response.getBody());
            totalFee = root.path("data").path("total").asInt(0);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return ResponseBuilder.build(
                HttpStatus.OK,
                "Tính phí thành công",
                totalFee
        );
    }

    public ResponseEntity<ResponseObject> getDistricts(Integer provinceId){
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

    public String getProvinceName(int provinceId) {
        String url = BASE_URL + "/master-data/province";
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);


        List<Map<String, Object>> provinces = (List<Map<String, Object>>) res.getBody().get("data");
        return provinces.stream()
                .filter(p -> (int)p.get("ProvinceID") == provinceId)
                .map(p -> (String)p.get("ProvinceName"))
                .findFirst()
                .orElse("N/A");
    }

    public String getDistrictName(int districtId, int provinceId) {
        String url = BASE_URL + "/master-data/district?province_id=" + provinceId;
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<Map<String, Object>> districts = (List<Map<String, Object>>) res.getBody().get("data");
        return districts.stream()
                .filter(d -> (int)d.get("DistrictID") == districtId)
                .map(d -> (String)d.get("DistrictName"))
                .findFirst()
                .orElse("N/A");
    }

    public String getWardName(String wardCode, int district_id) {
        String url = BASE_URL + "/master-data/ward?district_id=" + district_id;
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<Map> res = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<Map<String, Object>> wards = (List<Map<String, Object>>) res.getBody().get("data");
        return wards.stream()
                .filter(w -> w.get("WardCode").equals(wardCode))
                .map(w -> (String)w.get("WardName"))
                .findFirst()
                .orElse("N/A");
    }

    private Integer getService(Integer toDistrictId) {
        String url = BASE_URL + "/v2/shipping-order/available-services";

        Map<String, Object> body = Map.of(
                "shop_id", Integer.parseInt(SHOP_ID),
                "from_district", 3695,  // Quận Shop
                "to_district", toDistrictId
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        List<Map<String, Object>> services = (List<Map<String, Object>>) response.getBody().get("data");

        return services.stream()
                .filter(s -> "Hàng nhẹ".equalsIgnoreCase((String) s.get("short_name")))
                .findFirst()
                .map(s -> (Integer) s.get("service_id"))
                .orElse(null);
    }
}
