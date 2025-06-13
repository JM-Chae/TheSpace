package com.thespace.spacechat.exception;

import com.thespace.config.exception.MainException;
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
