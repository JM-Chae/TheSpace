package com.thespace.spacechat.message;

import com.thespace.spacechat.message.MessageDTOs.Text;

sealed public interface MessageDTOs permits Text {

    record Text(Long roomId, String content) implements MessageDTOs {
    }

}
