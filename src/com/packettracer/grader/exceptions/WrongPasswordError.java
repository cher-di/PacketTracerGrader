package com.packettracer.grader.exceptions;

import com.packettracer.grader.Grader;

public class WrongPasswordError extends GeneralError {
    public WrongPasswordError(String errorMessage) {
        super(errorMessage);
    }

    public WrongPasswordError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Integer getExitStatus() {
        return Grader.WRONG_PASSWORD;
    }
}
