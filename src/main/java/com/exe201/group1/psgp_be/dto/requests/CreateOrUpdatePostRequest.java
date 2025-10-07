package com.exe201.group1.psgp_be.dto.requests;

import com.exe201.group1.psgp_be.enums.Status;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrUpdatePostRequest {

    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    String title;
    String description;
    Status status;
    Integer productId;
    List<CreatePostImageRequest> postImages;
    List<Integer> TagIds;
}
