package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class ConnectionError extends BaseGraderError {
    public ConnectionError(String errorMessage) {
        super(errorMessage);
    }

    public ConnectionError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Constants.ExitStatus getExitStatus() {
        return Constants.ExitStatus.UNABLE_TO_CONNECT;
    }
}
