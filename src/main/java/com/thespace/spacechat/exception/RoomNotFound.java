package com.thespace.spacechat.exception;

import com.thespace.config.exception.MainException;
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
