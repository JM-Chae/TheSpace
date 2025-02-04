package com.thespace.thespace.dto.reply;

import jakarta.validation.constraints.NotNull;

public record ReplyRegisterDTO(@NotNull String replyContent,
                               @NotNull String replyWriter,
                               @NotNull String replyWriterUuid,
                               String tag,
                               String path ) { // will change path?

}
