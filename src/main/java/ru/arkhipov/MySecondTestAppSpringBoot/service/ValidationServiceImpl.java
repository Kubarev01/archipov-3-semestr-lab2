package ru.arkhipov.MySecondTestAppSpringBoot.service;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.arkhipov.MySecondTestAppSpringBoot.exception.ValidationFailedException;

@Service
public class ValidationServiceImpl implements ValidationService {
    @Override
    public void isValid(BindingResult result) {
        if (result.hasErrors()) {
            String details = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .distinct().sorted().collect(Collectors.joining("; "));
            throw new ValidationFailedException("Ошибка валидации: " + details);
        }
    }
}
