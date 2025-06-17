package com.thespace.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class MainException extends RuntimeException
  {
    private final Map<String,String> validation = new HashMap<>();

    public MainException(String message)
      {
        super(message);
      }

    public MainException(String message, Throwable cause)
      {
        super(message, cause);
      }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message)
      {
        validation.put(fieldName, message);
      }
  }
