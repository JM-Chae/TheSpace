package com.thespace.spaceweb.exception;

import com.thespace.config.exception.MainException;

public class UserNotFound extends MainException
  {
    public static final String message = "User not found.";

    public UserNotFound()
      {
        super(message);
      }

    @Override
    public int getStatusCode()
      { return 404; }
  }