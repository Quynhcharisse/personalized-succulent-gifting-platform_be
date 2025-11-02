package com.exe201.group1.psgp_be.controllers;

import com.exe201.group1.psgp_be.dto.requests.CreateDiscountProgramRequest;
import com.exe201.group1.psgp_be.dto.response.ResponseObject;
import com.exe201.group1.psgp_be.services.DiscountProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discountProgram")
@RequiredArgsConstructor
public class DiscountProgramController {

    private final DiscountProgramService discountProgramService;

    @GetMapping("/list")
    public ResponseEntity<ResponseObject> GetDiscountPrograms() {
        return discountProgramService.getDiscountPrograms();
    }

    @PostMapping()
    public ResponseEntity<ResponseObject> CreateDiscountProgram(@RequestBody CreateDiscountProgramRequest request) {
        return discountProgramService.createDiscountPrograms(request);
    }

}
