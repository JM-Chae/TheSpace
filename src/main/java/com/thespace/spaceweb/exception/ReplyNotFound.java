package com.thespace.spaceweb.exception;

import com.thespace.config.exception.MainException;

public class ReplyNotFound extends MainException
  {
    public static final String message = "Reply not found.";

    public ReplyNotFound()
      {
        super(message);
      }

    @Override
    public int getStatusCode()
      { return 404; }
  }
