package com.packettracer.args.parsers;

import com.packettracer.Constants;
import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.IntegerParsingError;
import com.packettracer.args.exceptions.ParseError;

public class PortParser implements Parser {
    @Override
    public Integer parse(Object port) throws ParseError {
        String _port = (String) port;
        try {
            return port != null ? Integer.parseInt(_port) : Constants.DEFAULT_PORT;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse port: %s", _port), e);
        }
    }
}
