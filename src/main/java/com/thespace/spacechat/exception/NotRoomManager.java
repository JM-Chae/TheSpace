package com.thespace.spacechat.exception;

import com.thespace.config.exception.MainException;
import lombok.Getter;

@Getter
public class NotRoomManager extends MainException {

    public static final String message = "Your not a room manager.";

    public NotRoomManager() {
        super(message);
    }

    public int getStatusCode() {
        return 404;
    }
}
