package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class CategoryNotFound extends MainException
  {
        public static final String message = "Category not found.";

    public CategoryNotFound()
      {
        super(message);
      }

    @Override
    public int getStatusCode()
      { return 404; }
  }
