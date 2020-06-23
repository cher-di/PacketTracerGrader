package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class WrongCredentialsError extends BaseGraderError {
    private static final Constants.ExitStatus exitStatus = Constants.ExitStatus.WRONG_CREDENTIALS;

    public WrongCredentialsError(String errorMessage) {
        super(errorMessage);
    }

    public WrongCredentialsError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
