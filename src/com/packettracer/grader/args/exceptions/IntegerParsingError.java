package com.packettracer.grader.args.exceptions;

import com.packettracer.grader.args.exceptions.ParseError;

public class IntegerParsingError extends ParseError {
    public IntegerParsingError(String errorMessage) {
        super(errorMessage);
    }

    public IntegerParsingError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
