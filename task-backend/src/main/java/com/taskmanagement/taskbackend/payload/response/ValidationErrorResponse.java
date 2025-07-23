package com.taskmanagement.taskbackend.payload.response;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {
    private List<ValidationError> errors = new ArrayList<>();
    
    public List<ValidationError> getErrors() {
        return errors;
    }
    
    public void addError(String field, String message) {
        this.errors.add(new ValidationError(field, message));
    }
    
    public static class ValidationError {
        private String field;
        private String message;
        
        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
    }
}