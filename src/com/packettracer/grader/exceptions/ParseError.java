package com.packettracer.grader.exceptions;

public class ParseError extends BaseGraderError {
    public ParseError(String errorMessage) {
        super(errorMessage);
    }

    public ParseError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
