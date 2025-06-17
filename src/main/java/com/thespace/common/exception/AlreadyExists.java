package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class AlreadyExists extends MainException {
    public static final String message = "This name already exists.";

    public AlreadyExists()
      {
        super(message);
      }

    @Override
    public int getStatusCode()
      { return 409; }
  }
