package com.packettracer.grader.exceptions;

import com.packettracer.grader.Grader;

public class GeneralError extends Exception {
    public GeneralError(String errorMessage) {
        super(errorMessage);
    }

    public GeneralError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public Integer getReturnCode() {
        return Grader.RETURN_CODE_GENERAL_ERROR;
    }
}
