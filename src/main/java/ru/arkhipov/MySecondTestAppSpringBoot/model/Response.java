package ru.arkhipov.MySecondTestAppSpringBoot.model;

import java.time.Instant;
import lombok.*;

@Data
@Builder
public class Response {
    private String uid;
    private String operationUid;
    private String systemTime;
    private String code;
    private String errorCode;
    private String errorMessage;

    public static Response result(Request request, String code,
                                  String errorCode, String errorMessage) {
        return Response.builder()
                .uid(request == null || request.getUid() == null ? "" : request.getUid())
                .operationUid(request == null || request.getOperationUid() == null
                        ? "" : request.getOperationUid())
                .systemTime(Instant.now().toString())
                .code(code).errorCode(errorCode).errorMessage(errorMessage).build();
    }
}
