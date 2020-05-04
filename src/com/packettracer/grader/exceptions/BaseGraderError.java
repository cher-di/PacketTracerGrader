package com.packettracer.grader.exceptions;

public class BaseGraderError extends Exception {

    public BaseGraderError(String errorMessage) {
        super(errorMessage);
    }

    public BaseGraderError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
