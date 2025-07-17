package com.example.demo.record.request;

import com.example.demo.enums.RecordStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRecordRequest {

    /**
     * 필드 타입이 String이 아니라면 @NotBlank는 쓸 수 없고,
     * Integer, Boolean, Long 등엔 @NotNull
     * List, Map엔 @NotEmpty 또는 @Size(min=1)을 써야 합니다.
     */

    @NotBlank
    @Size(max = 100, message = "기록물명은 100자 이하로 입력해주세요.")
    private String title;

    @NotBlank
    @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요.")
    private String content;

    @NotNull(message = "상태는 필수입니다.")
    private RecordStatus status;

    @NotNull(message = "공개 여부는 필수입니다.")
    private Boolean isPublic;
}
