package com.thespace.spaceweb.reply;

import com.thespace.spaceweb.reply.ReplyDTOs.Info;
import com.thespace.spaceweb.reply.ReplyDTOs.Register;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

sealed public interface ReplyDTOs permits Info, Register {

    @Builder
    record Info(Long rno, String replyContent, String replyWriter,
                String replyWriterUuid, String tag, LocalDateTime replyDate, Long childCount, Long taggedCount,
                Long parentRno, Long tagRno, Long vote, List<Info> children) implements ReplyDTOs {
        public Info setChildren(List<Info> children) {
            return new Info(this.rno, this.replyContent, this.replyWriter, this.replyWriterUuid, this.tag, this.replyDate,
                (long) children.size(), this.taggedCount, this.parentRno, this.tagRno, this.vote, children);
        }

    }

    record Register(@NotNull String replyContent,
                    String tag,
                    Long parentRno,
                    Long tagRno) implements ReplyDTOs { // will change path?

    }
}