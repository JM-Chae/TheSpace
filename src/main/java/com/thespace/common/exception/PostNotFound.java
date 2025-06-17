package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class PostNotFound extends MainException
  {
        public static final String message = "Post not found.";

    public PostNotFound()
      {
        super(message);
      }

    @Override
    public int getStatusCode()
      { return 404; }
  }
