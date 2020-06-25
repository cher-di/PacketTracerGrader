package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class WrongCredentialsError extends BaseGraderError {
    public WrongCredentialsError(String errorMessage) {
        super(errorMessage);
    }

    public WrongCredentialsError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Constants.ExitStatus getExitStatus() {
        return Constants.ExitStatus.WRONG_CREDENTIALS;
    }
}
