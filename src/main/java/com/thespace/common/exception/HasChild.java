package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class HasChild extends MainException {

    public static final String message = "This reply has children and cannot be deleted.";

    public HasChild() {
        super(message);
    }

    public int getStatusCode() {
        return 404;
    }
}
