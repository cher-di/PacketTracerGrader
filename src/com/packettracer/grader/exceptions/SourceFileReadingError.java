package com.packettracer.grader.exceptions;

public class SourceFileReadingError extends BaseGraderError {

    public SourceFileReadingError(String errorMessage) {
        super(errorMessage);
    }

    public SourceFileReadingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
