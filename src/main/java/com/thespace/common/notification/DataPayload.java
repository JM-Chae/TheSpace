package com.thespace.common.notification;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DataPayload(Long fid, String targetNameAndUUID, FriendshipNotificationStatus status) {

    public static DataPayload forNull() {
        return new DataPayload(null, null,null);
    }

    public static DataPayload forFriendShipRequest(Long fid, String targetNameAndUUID) {
        return new DataPayload(fid, targetNameAndUUID, FriendshipNotificationStatus.REQUEST);
    }

    public static DataPayload forFriendShipAccept(String targetNameAndUUID) {
        return new DataPayload(null, targetNameAndUUID, FriendshipNotificationStatus.ACCEPTED);
    }

    public static DataPayload forFinalFriendShipStatus(String targetNameAndUUID, FriendshipNotificationStatus status) {
        return new DataPayload(null, targetNameAndUUID, status);
    }
}

