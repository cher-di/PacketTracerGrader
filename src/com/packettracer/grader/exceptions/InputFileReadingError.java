package com.packettracer.grader.exceptions;

import com.packettracer.grader.Grader;

public class InputFileReadingError extends GeneralError {
    public InputFileReadingError(String errorMessage) {
        super(errorMessage);
    }

    public InputFileReadingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Integer getReturnCode() {
        return Grader.RETURN_CODE_INPUT_FILE_READING_FAILED;
    }
}
