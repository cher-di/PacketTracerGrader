package com.packettracer.grader.exceptions;

import com.packettracer.grader.Grader;

public class OutputFileWritingError extends GeneralError {
    public OutputFileWritingError(String errorMessage) {
        super(errorMessage);
    }

    public OutputFileWritingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Integer getReturnCode() {
        return Grader.RETURN_CODE_OUTPUT_FILE_WRITING_FAILED;
    }
}
