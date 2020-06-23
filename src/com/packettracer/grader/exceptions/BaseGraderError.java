package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class BaseGraderError extends Exception {
    protected static final Constants.ExitStatus exitStatus = Constants.ExitStatus.GENERAL_ERROR;

    public BaseGraderError(String errorMessage) {
        super(errorMessage);
    }

    public BaseGraderError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public Constants.ExitStatus getExitStatus() {
        return exitStatus;
    }
}
