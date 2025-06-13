package com.thespace.spaceweb.exception;

import com.thespace.config.exception.MainException;
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
