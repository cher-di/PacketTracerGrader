package com.packettracer.grader.args.parsers;

import com.packettracer.grader.Constants;
import com.packettracer.grader.args.Parser;
import com.packettracer.grader.args.exceptions.IntegerParsingError;
import com.packettracer.grader.args.exceptions.ParseError;

public class PortParser implements Parser {
    @Override
    public Object parse(String port) throws ParseError {
        try {
            return port != null ? Integer.parseInt(port) : Constants.DEFAULT_PORT;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse port: %s", port), e);
        }
    }
}
