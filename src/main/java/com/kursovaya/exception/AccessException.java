package com.kursovaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessException extends ApiException {
    public AccessException() {
        super("You don't have access to this endpoint","YOU_SHELL_NOT_PASS");
    }
}
