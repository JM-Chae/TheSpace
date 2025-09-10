package com.thespace.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public final class Exceptions {

    @Getter
    public abstract static class MainException extends RuntimeException {

        private final Map<String, String> validation = new HashMap<>();

        public MainException(String message) {
            super(message);
        }

        public MainException(String message, Throwable cause) {
            super(message, cause);
        }

        public abstract int getStatusCode();

        public void addValidation(String fieldName, String message) {
            validation.put(fieldName, message);
        }
    }

    @Getter
    public static class NotFoundNotification extends MainException {
        public static final String message = "Not Found Notification.";

        public NotFoundNotification() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class DataPayloadConvertError extends MainException {
        public static final String message = "Failed to convert data payload";

        public DataPayloadConvertError() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 400;
        }
    }

    @Getter
    public static class FcmTokenNotFound extends MainException {

        public static final String message = "Could not find the FCM token for the target.";

        public FcmTokenNotFound() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class AlreadyExists extends MainException {

        public static final String message = "This name already exists.";

        public AlreadyExists() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 409;
        }
    }

    @Getter
    public static class CategoryNotFound extends MainException {

        public static final String message = "Category not found.";

        public CategoryNotFound() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class CommunityNotFound extends MainException {

        public static final String message = "Community not found.";

        public CommunityNotFound() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class HasChild extends MainException {

        public static final String message = "This reply has children and cannot be deleted.";

        public HasChild() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class NotFoundFriendship extends MainException {

        public static final String message = "Not Found Friendship.";

        public NotFoundFriendship() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class NotMember extends MainException {

        public static final String message = "User is a not member.";

        public NotMember() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 403;
        }
    }

    @Getter
    public static class NotOwnerFriendship extends MainException {

        public static final String message = "You are not the owner of this friendship.";

        public NotOwnerFriendship() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 403;
        }
    }

    @Getter
    public static class NotRegisterUser extends MainException {

        public static final String message = "Your not a register this reply.";

        public NotRegisterUser() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class NotRoomManager extends MainException {

        public static final String message = "Your not a room manager.";

        public NotRoomManager() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class PostNotFound extends MainException {

        public static final String message = "Post not found.";

        public PostNotFound() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class ReplyNotFound extends MainException {

        public static final String message = "Reply not found.";

        public ReplyNotFound() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class RoleNotFound extends MainException {

        public static final String message = "Not Found Role.";

        public RoleNotFound() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class RoomNotFound extends MainException {

        public static final String message = "Room not found.";

        public RoomNotFound() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

    @Getter
    public static class UserNotFound extends MainException {

        public static final String message = "User not found.";

        public UserNotFound() {
            super(message);
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }
}
