package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class BaseGraderError extends Exception {
    public BaseGraderError(String errorMessage) {
        super(errorMessage);
    }

    public BaseGraderError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public Constants.ExitStatus getExitStatus() {
        return Constants.ExitStatus.GENERAL_ERROR;
    }
}
