package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class SourceFileReadingError extends BaseGraderError {
    public SourceFileReadingError(String errorMessage) {
        super(errorMessage);
    }

    public SourceFileReadingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Constants.ExitStatus getExitStatus() {
        return Constants.ExitStatus.SOURCE_FILE_READING_FAILED;
    }
}
