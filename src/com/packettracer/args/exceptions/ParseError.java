package com.packettracer.args.exceptions;

import com.packettracer.grader.exceptions.GeneralError;

public class ParseError extends GeneralError {
    public ParseError(String errorMessage) {
        super(errorMessage);
    }

    public ParseError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
