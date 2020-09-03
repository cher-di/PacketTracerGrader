package com.packettracer.args.parsers;

import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.IntegerParsingError;
import com.packettracer.args.exceptions.ParseError;

public class AttemptsParser implements Parser {
    private final Integer minAttempts;
    private final Integer maxAttempts;
    private final Integer defaultAttempts;

    public AttemptsParser(Integer minAttempts, Integer maxAttempts, Integer defaultAttempts) {
        this.minAttempts = minAttempts;
        this.maxAttempts = maxAttempts;
        this.defaultAttempts = defaultAttempts;
    }

    @Override
    public Integer parse(Object attempts) throws ParseError {
        String _attempts = (String) attempts;
        int parsed_attempts;
        try {
            parsed_attempts = _attempts != null ? Integer.parseInt(_attempts) : defaultAttempts;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse attempts: %s", _attempts), e);
        }
        if (parsed_attempts < minAttempts || parsed_attempts > maxAttempts) {
            throw new IntegerParsingError(String.format("Attempts number should be in range from %d to %d, got %d",
                    minAttempts, maxAttempts, parsed_attempts));
        }
        return parsed_attempts;
    }
}
