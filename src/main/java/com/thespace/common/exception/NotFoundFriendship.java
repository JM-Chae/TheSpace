package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class NotFoundFriendship extends MainException {

        public static final String message = "Not Found Friendship.";

        public NotFoundFriendship() {
            super(message);
        }

        public int getStatusCode() {
            return 404;
        }
    }
