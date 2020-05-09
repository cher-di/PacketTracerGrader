package com.packettracer.grader.exceptions;

public class WrongCredentialsError extends BaseGraderError {

    public WrongCredentialsError(String errorMessage) {
        super(errorMessage);
    }

    public WrongCredentialsError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
