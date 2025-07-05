package com.thespace.spaceweb.reply;

import com.thespace.spaceweb.reply.ReplyDTOs.Info;
import com.thespace.spaceweb.reply.ReplyDTOs.Register;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

sealed public interface ReplyDTOs permits Info, Register {

    @Builder
    record Info(Long rno, String replyContent, String replyWriter,
                String replyWriterUuid, String tag, LocalDateTime replyDate, Long childCount, Long taggedCount,
                Long parentRno, Long tagRno, Long vote) implements ReplyDTOs {

    }

    record Register(@NotNull String replyContent,
                    String tag,
                    Long parentRno,
                    Long tagRno) implements ReplyDTOs { // will change path?

    }
}
