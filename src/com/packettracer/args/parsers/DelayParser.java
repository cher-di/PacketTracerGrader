package com.packettracer.args.parsers;

import com.packettracer.Constants;
import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.IntegerParsingError;

public class DelayParser implements Parser {
    @Override
    public Integer parse(Object delay) throws IntegerParsingError {
        String _delay = (String) delay;
        int parsed_delay;
        try {
            parsed_delay = _delay != null ? Integer.parseInt(_delay) : Constants.DEFAULT_DELAY;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse delay: %s", _delay));
        }
        if (parsed_delay < Constants.MIN_DELAY || parsed_delay > Constants.MAX_DELAY) {
            throw new IntegerParsingError(String.format("Delay between attempts should be %d <= delay <= %d, got %d",
                    Constants.MIN_DELAY, Constants.MAX_DELAY, parsed_delay));
        }
        return parsed_delay;
    }
}
