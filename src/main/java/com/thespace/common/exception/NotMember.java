package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class NotMember extends MainException {

    public static final String message = "User is a not member.";

    public NotMember() {
        super(message);
    }

    public int getStatusCode() {
        return 404;
    }
}
