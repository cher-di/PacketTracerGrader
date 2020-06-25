package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class WrongPasswordError extends BaseGraderError {
    public WrongPasswordError(String errorMessage) {
        super(errorMessage);
    }

    public WrongPasswordError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Constants.ExitStatus getExitStatus() {
        return Constants.ExitStatus.WRONG_PASSWORD;
    }
}
