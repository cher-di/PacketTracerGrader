package com.packettracer.grader.args;

public class ParseError extends Exception {

    public ParseError(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
