package com.packettracer.args.parsers;

import com.packettracer.Constants;
import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.IntegerParsingError;
import com.packettracer.args.exceptions.ParseError;

public class AttemptsParser implements Parser {
    @Override
    public Integer parse(Object attempts) throws ParseError {
        String _attempts = (String) attempts;
        try {
            return _attempts != null ? Integer.parseInt(_attempts) : Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse attempts: %s", _attempts), e);
        }
    }
}
