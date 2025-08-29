package com.exe201.group1.psgp_be.services;

import com.exe201.group1.psgp_be.dto.requests.ProcessAccountRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface AdminService {
     ResponseEntity<ResponseObject> getAllBuyerAccounts();
     ResponseEntity<ResponseObject> getActiveBuyerAccounts();
     ResponseEntity<ResponseObject> getBannedBuyerAccounts();
     ResponseEntity<ResponseObject> getProfileBuyerAccount(int id);
     ResponseEntity<ResponseObject> banBuyerAccount(ProcessAccountRequest request);
     ResponseEntity<ResponseObject> unbanBuyerAccount(ProcessAccountRequest request);
}
