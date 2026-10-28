package ru.arkhipov.MySecondTestAppSpringBoot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import ru.arkhipov.MySecondTestAppSpringBoot.model.Response;

@RestControllerAdvice
public class JsonExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> invalidJson(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(Response.result(null, "failed",
                "ValidationException", "Ошибка валидации: некорректное тело JSON"));
    }
}
