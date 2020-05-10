package com.packettracer.grader.args;

import com.packettracer.grader.Constants;
import com.packettracer.grader.exceptions.IntegerParsingError;

public class Parsers {
    public static Integer parsePort(String port) throws IntegerParsingError {
        try {
            return port != null ? Integer.parseInt(port) : Constants.DEFAULT_PORT;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse port: %s", port), e);
        }
    }

    public static String parseHost(String host) {
        return host != null ? host : Constants.DEFAULT_HOST;
    }

    public static Integer parseAttempts(String attempts) throws IntegerParsingError {
        try {
            return attempts != null ? Integer.parseInt(attempts) : Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse attempts: %s", attempts), e);
        }
    }

    public static Integer parseDelay(String delay) throws IntegerParsingError {
        int parsed_delay;
        try {
            parsed_delay = delay != null ? Integer.parseInt(delay) : Constants.DEFAULT_CONNECTION_ATTEMPTS_DELAY;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse delay: %s", delay));
        }
        if (parsed_delay < Constants.MIN_CONNECTION_ATTEMPTS_DELAY || parsed_delay > Constants.MAX_CONNECTION_ATTEMPTS_DELAY) {
            throw new IntegerParsingError(String.format("Delay between attempts should be %d <= delay <= %d, got %d",
                    Constants.MIN_CONNECTION_ATTEMPTS_DELAY, Constants.MAX_CONNECTION_ATTEMPTS_DELAY, parsed_delay));
        }
        return parsed_delay;
    }
}
