package com.thespace.common.exception;

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
