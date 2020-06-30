package com.packettracer.args.parsers;

import com.packettracer.Constants;
import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.IntegerParsingError;
import com.packettracer.args.exceptions.ParseError;

public class AttemptsParser implements Parser {
    @Override
    public Integer parse(Object attempts) throws ParseError {
        String _attempts = (String) attempts;
        int parsed_attempts;
        try {
            parsed_attempts = _attempts != null ? Integer.parseInt(_attempts) : Constants.DEFAULT_ATTEMPTS;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse attempts: %s", _attempts), e);
        }
        if (parsed_attempts < Constants.MIN_ATTEMPTS || parsed_attempts > Constants.MAX_ATTEMPTS) {
            throw new IntegerParsingError(String.format("Attempts number should be in range from %d to %d, got %d",
                    Constants.MIN_ATTEMPTS, Constants.MAX_ATTEMPTS, parsed_attempts));
        }
        return parsed_attempts;
    }
}
