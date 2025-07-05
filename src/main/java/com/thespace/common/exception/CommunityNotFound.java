package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class CommunityNotFound extends MainException
  {
        public static final String message = "Community not found.";

    public CommunityNotFound()
      {
        super(message);
      }

    @Override
    public int getStatusCode()
      { return 404; }
  }
