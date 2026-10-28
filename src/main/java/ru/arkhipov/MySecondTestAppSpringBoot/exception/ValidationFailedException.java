package ru.arkhipov.MySecondTestAppSpringBoot.exception;

public class ValidationFailedException extends RuntimeException {
    public ValidationFailedException(String message) { super(message); }
}
