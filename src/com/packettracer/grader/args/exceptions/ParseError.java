package com.packettracer.grader.args.exceptions;

import com.packettracer.grader.exceptions.BaseGraderError;

public class ParseError extends BaseGraderError {
    public ParseError(String errorMessage) {
        super(errorMessage);
    }

    public ParseError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
