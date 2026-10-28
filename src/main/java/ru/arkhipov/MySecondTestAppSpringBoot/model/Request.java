package ru.arkhipov.MySecondTestAppSpringBoot.model;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @NotBlank(message = "uid обязателен")
    @Size(max = 32, message = "uid не длиннее 32 символов")
    private String uid;

    @NotBlank(message = "operationUid обязателен")
    @Size(max = 32, message = "operationUid не длиннее 32 символов")
    private String operationUid;

    private String systemName;
    @NotBlank(message = "systemTime обязателен")
    private String systemTime;
    private String source;

    @NotNull(message = "communicationId обязателен")
    @Min(value = 1, message = "communicationId не меньше 1")
    @Max(value = 100000, message = "communicationId не больше 100000")
    private Integer communicationId;

    private Integer templateId;
    private Integer productCode;
    private Integer smsCode;
}
