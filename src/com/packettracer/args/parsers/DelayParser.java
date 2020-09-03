package com.packettracer.args.parsers;

import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.IntegerParsingError;

public class DelayParser implements Parser {
    private final Integer minDelay;
    private final Integer maxDelay;
    private final Integer defaultDelay;

    public DelayParser(Integer minDelay, Integer maxDelay, Integer defaultDelay) {
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.defaultDelay = defaultDelay;
    }
    @Override
    public Integer parse(Object delay) throws IntegerParsingError {
        String _delay = (String) delay;
        int parsed_delay;
        try {
            parsed_delay = _delay != null ? Integer.parseInt(_delay) : defaultDelay;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse delay: %s", _delay));
        }
        if (parsed_delay < minDelay || parsed_delay > maxDelay) {
            throw new IntegerParsingError(String.format("Delay between attempts should be %d <= delay <= %d, got %d",
                    minDelay, maxDelay, parsed_delay));
        }
        return parsed_delay;
    }
}
