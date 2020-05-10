package com.packettracer.grader.args.parsers;

import com.packettracer.grader.Constants;
import com.packettracer.grader.args.Parser;
import com.packettracer.grader.args.exceptions.IntegerParsingError;

public class DelayParser implements Parser {
    @Override
    public Object parse(String delay) throws IntegerParsingError {
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
