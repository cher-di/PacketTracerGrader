package com.packettracer.grader.exceptions;

public class WrongPasswordError extends BaseGraderError {

    public WrongPasswordError(String errorMessage) {
        super(errorMessage);
    }

    public WrongPasswordError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
