package com.thespace.common.notice;

import com.thespace.common.fcm.FcmTopic;

public record NoticeDTO(String title, String body, String url, FcmTopic topic) {

}
