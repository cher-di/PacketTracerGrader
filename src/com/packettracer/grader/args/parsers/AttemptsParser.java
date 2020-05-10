package com.packettracer.grader.args.parsers;

import com.packettracer.grader.Constants;
import com.packettracer.grader.args.Parser;
import com.packettracer.grader.args.exceptions.IntegerParsingError;
import com.packettracer.grader.args.exceptions.ParseError;

public class AttemptsParser implements Parser {
    @Override
    public Object parse(String attempts) throws ParseError {
        try {
            return attempts != null ? Integer.parseInt(attempts) : Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse attempts: %s", attempts), e);
        }
    }
}
