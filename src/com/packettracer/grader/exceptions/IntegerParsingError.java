package com.packettracer.grader.exceptions;

public class IntegerParsingError extends ParseError {
    public IntegerParsingError(String errorMessage) {
        super(errorMessage);
    }

    public IntegerParsingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
