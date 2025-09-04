package com.exe201.group1.psgp_be.dto.requests;

import com.exe201.group1.psgp_be.enums.FengShui;
import com.exe201.group1.psgp_be.enums.Zodiac;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateSucculentRequest {
    String speciesName;
    String description;
    List<FengShui> fengShuiList;
    List<Zodiac> zodiacList;
    List<SizeDetailRequest> sizeDetailRequests;
}
