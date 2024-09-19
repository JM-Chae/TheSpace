package com.thespace.thespace.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Builder
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse
  {
    private final String message;
    private final String code;
    private final Map<String,String> validation;
  }
