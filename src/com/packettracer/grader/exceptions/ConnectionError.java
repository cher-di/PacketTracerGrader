package com.packettracer.grader.exceptions;

import com.packettracer.grader.Grader;

public class ConnectionError extends GeneralError {
    public ConnectionError(String errorMessage) {
        super(errorMessage);
    }

    public ConnectionError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Integer getExitStatus() {
        return Grader.UNABLE_TO_CONNECT;
    }
}
