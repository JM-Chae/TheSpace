package com.thespace.common.exception;

import lombok.Getter;

@Getter
public class RoleNotFound extends MainException {

        public static final String message = "Not Found Role.";

        public RoleNotFound() {
            super(message);
        }

        public int getStatusCode() {
            return 404;
        }
    }
