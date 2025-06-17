package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class RoomNotFound extends MainException {
    public static final String message = "Room not found.";

    public RoomNotFound()
    {
        super(message);
    }

    @Override
    public int getStatusCode()
    { return 404; }
}
