package com.thespace.common.exception;

public class NotOwnerFriendship extends MainException {

    public static final String message = "You are not the owner of this friendship.";

    public NotOwnerFriendship() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
