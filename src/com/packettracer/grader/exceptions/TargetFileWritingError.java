package com.packettracer.grader.exceptions;

import com.packettracer.grader.Constants;

public class TargetFileWritingError extends BaseGraderError {
    public TargetFileWritingError(String errorMessage) {
        super(errorMessage);
    }

    public TargetFileWritingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    @Override
    public Constants.ExitStatus getExitStatus() {
        return Constants.ExitStatus.TARGET_FILE_WRITING_FAILED;
    }
}
