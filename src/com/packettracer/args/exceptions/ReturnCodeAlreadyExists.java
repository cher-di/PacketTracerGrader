package com.packettracer.args.exceptions;

public class ReturnCodeAlreadyExists extends ParseError {
    public ReturnCodeAlreadyExists(String errorMessage) {
        super(errorMessage);
    }

    public ReturnCodeAlreadyExists(Integer returnCode, Throwable err) {
        super(makeErrorMessage(returnCode), err);
    }

    public ReturnCodeAlreadyExists(Integer returnCode) {
        super(makeErrorMessage(returnCode));
    }

    private static String makeErrorMessage(Integer returnCode) {
        return String.format("This return code is already exists: %d", returnCode);
    }
}
