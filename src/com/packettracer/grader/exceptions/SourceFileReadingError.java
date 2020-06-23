package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class SourceFileReadingError extends BaseGraderError {
    private static final Constants.ExitStatus exitStatus = Constants.ExitStatus.SOURCE_FILE_READING_FAILED;

    public SourceFileReadingError(String errorMessage) {
        super(errorMessage);
    }

    public SourceFileReadingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
