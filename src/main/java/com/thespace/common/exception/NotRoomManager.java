package com.thespace.common.exception;

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
