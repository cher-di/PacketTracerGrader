package com.packettracer.args.parsers;

import com.packettracer.Constants;
import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.IntegerParsingError;
import com.packettracer.args.exceptions.ParseError;

public class PortParser implements Parser {
    @Override
    public Integer parse(Object port) throws ParseError {
        String _port = (String) port;
        int parsed_port;
        try {
            parsed_port = port != null ? Integer.parseInt(_port) : Constants.DEFAULT_PORT;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse port: %s", _port), e);
        }
        if (parsed_port < Constants.MIN_PORT || parsed_port > Constants.MAX_PORT) {
            throw new IntegerParsingError(String.format("Port should be in range from %d to %d, got %d",
                    Constants.MIN_PORT, Constants.MAX_PORT, parsed_port));
        }
        return parsed_port;
    }
}
