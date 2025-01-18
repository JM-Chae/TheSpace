package com.thespace.thespace.dto.reply;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReplyDTO(Long rno, Long bno, String replyContent, String replyWriter,
                       String replyWriterUuid, String tag, LocalDateTime replyDate, Long isNested,
                       String path, Long vote) {

}
