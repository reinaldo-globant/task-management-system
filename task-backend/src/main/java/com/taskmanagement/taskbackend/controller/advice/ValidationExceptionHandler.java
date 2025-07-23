package com.taskmanagement.taskbackend.controller.advice;

import com.taskmanagement.taskbackend.payload.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errorResponse.addError(fieldName, errorMessage);
        });
        
        return errorResponse;
    }
}