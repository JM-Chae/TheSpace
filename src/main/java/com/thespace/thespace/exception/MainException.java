package com.thespace.thespace.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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
