package com.packettracer.grader.exceptions;

public class TargetFileWritingError extends BaseGraderError {
    public TargetFileWritingError(String errorMessage) {
        super(errorMessage);
    }

    public TargetFileWritingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
