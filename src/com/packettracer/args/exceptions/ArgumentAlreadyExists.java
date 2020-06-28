package com.packettracer.args.exceptions;

public class ArgumentAlreadyExists extends ParseError {
    public ArgumentAlreadyExists(String argName) {
        super(makeErrorMessage(argName));
    }

    public ArgumentAlreadyExists(String argName, Throwable err) {
        super(makeErrorMessage(argName), err);
    }

    private static String makeErrorMessage(String argName) {
        return String.format("This argument is already exists: %s", argName);
    }
}
