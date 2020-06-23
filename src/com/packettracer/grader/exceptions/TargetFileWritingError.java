package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class TargetFileWritingError extends BaseGraderError {
    private static final Constants.ExitStatus exitStatus = Constants.ExitStatus.TARGET_FILE_WRITING_FAILED;

    public TargetFileWritingError(String errorMessage) {
        super(errorMessage);
    }

    public TargetFileWritingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
