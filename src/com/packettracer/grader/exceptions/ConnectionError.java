package com.packettracer.grader.exceptions;

public class ConnectionError extends BaseGraderError {
    public ConnectionError(String errorMessage) {
        super(errorMessage);
    }

    public ConnectionError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
