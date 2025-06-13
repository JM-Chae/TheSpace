package com.thespace.spaceweb.dto;

import com.thespace.spaceweb.dto.ReplyDTOs.Info;
import com.thespace.spaceweb.dto.ReplyDTOs.Register;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

sealed public interface ReplyDTOs permits Info, Register {

    @Builder
    record Info(Long rno, String replyContent, String replyWriter,
                String replyWriterUuid, String tag, LocalDateTime replyDate, Long isNested,
                String path, Long vote) implements ReplyDTOs {

    }

    record Register(@NotNull String replyContent,
                    String tag,
                    String path ) implements ReplyDTOs { // will change path?

    }
}
