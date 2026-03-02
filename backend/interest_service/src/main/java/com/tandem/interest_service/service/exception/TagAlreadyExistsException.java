package com.tandem.interest_service.service.exception;

public class TagAlreadyExistsException extends RuntimeException {

    public TagAlreadyExistsException(String name) {
        super("Tag with name '" + name + "' already exists");
    }
}