package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class ConnectionError extends BaseGraderError {
    private static final Constants.ExitStatus exitStatus = Constants.ExitStatus.UNABLE_TO_CONNECT;

    public ConnectionError(String errorMessage) {
        super(errorMessage);
    }

    public ConnectionError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
