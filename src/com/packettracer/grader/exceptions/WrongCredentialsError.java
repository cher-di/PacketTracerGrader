package com.packettracer.grader.exceptions;

import com.packettracer.grader.Grader;

public class WrongCredentialsError extends GeneralError {
    public WrongCredentialsError(String errorMessage) {
        super(errorMessage);
    }

    public WrongCredentialsError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Integer getReturnCode() {
        return Grader.RETURN_CODE_WRONG_CREDENTIALS;
    }
}
