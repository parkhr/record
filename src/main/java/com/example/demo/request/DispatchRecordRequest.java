package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DispatchRecordRequest {

    @NotBlank
    private Long recordId;

    private Long collectionId;

    private Long seriesId;

    private Long folderId;
}
