package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class WrongPasswordError extends BaseGraderError {
    private static final Constants.ExitStatus exitStatus = Constants.ExitStatus.WRONG_PASSWORD;

    public WrongPasswordError(String errorMessage) {
        super(errorMessage);
    }

    public WrongPasswordError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
