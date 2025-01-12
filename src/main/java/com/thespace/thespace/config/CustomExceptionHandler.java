package com.thespace.thespace.config;

import com.thespace.thespace.exception.ErrorResponse;
import com.thespace.thespace.exception.MainException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestControllerAdvice
public class CustomExceptionHandler
  {
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleBindException(BindException e)
      {
        log.error(e);
        Map<String, String> errorMap = new HashMap<>();
        if (e.hasErrors())
          {
            BindingResult bindingResult = e.getBindingResult();
            bindingResult.getFieldErrors().forEach(fieldError ->
                errorMap.put(fieldError.getField(), fieldError.getCode()));
          }
        return ResponseEntity.badRequest().body(errorMap);
      }

    @ResponseBody
    @ExceptionHandler(MainException.class)
    public ResponseEntity<ErrorResponse> mainException(MainException e)
      {
        int statusCode = e.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
            .code(String.valueOf(statusCode))
            .message(e.getMessage())
            .validation(e.getValidation() != null ? e.getValidation() : null)
            .build();

        return ResponseEntity.status(statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body);
      }
  }
