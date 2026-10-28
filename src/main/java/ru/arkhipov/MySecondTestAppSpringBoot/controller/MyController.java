package ru.arkhipov.MySecondTestAppSpringBoot.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.arkhipov.MySecondTestAppSpringBoot.model.Request;
import ru.arkhipov.MySecondTestAppSpringBoot.model.Response;
import ru.arkhipov.MySecondTestAppSpringBoot.service.ValidationService;
import ru.arkhipov.MySecondTestAppSpringBoot.exception.*;

@RestController
public class MyController {
    private final ValidationService validationService;

    public MyController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request,
                                              BindingResult result) {
        try {
            validationService.isValid(result);
            if ("123".equals(request.getUid())) {
                throw new UnsupportedCodeException("Не поддерживаемая ошибка");
            }
            return ResponseEntity.ok(Response.result(request, "success", "", ""));
        } catch (ValidationFailedException exception) {
            return ResponseEntity.badRequest().body(Response.result(request,
                    "failed", "ValidationException", exception.getMessage()));
        } catch (UnsupportedCodeException exception) {
            return ResponseEntity.badRequest().body(Response.result(request,
                    "failed", "UnsupportedCodeException", exception.getMessage()));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body(Response.result(request,
                    "failed", "UnknownException", "Произошла непредвиденная ошибка"));
        }
    }
}
