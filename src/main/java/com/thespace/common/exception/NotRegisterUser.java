package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class NotRegisterUser extends MainException {

    public static final String message = "Your not a register this reply.";

    public NotRegisterUser() {
        super(message);
    }

    public int getStatusCode() {
        return 404;
    }
}
