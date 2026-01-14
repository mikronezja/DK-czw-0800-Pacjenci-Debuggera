package com.oot.clinic.exceptions;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " not found");
    }
}
