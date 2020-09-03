package com.packettracer.args.parsers;

import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.IntegerParsingError;
import com.packettracer.args.exceptions.ParseError;

public class PortParser implements Parser {
    private static final Integer minPort = 1;
    private static final Integer maxPort = 65535;

    private final Integer defaultPort;

    public PortParser(Integer defaultPort) {
        this.defaultPort = defaultPort;
    }

    @Override
    public Integer parse(Object port) throws ParseError {
        String _port = (String) port;
        int parsed_port;
        try {
            parsed_port = port != null ? Integer.parseInt(_port) : defaultPort;
        } catch (NumberFormatException e) {
            throw new IntegerParsingError(String.format("Can not parse port: %s", _port), e);
        }
        if (parsed_port < minPort || parsed_port > maxPort) {
            throw new IntegerParsingError(String.format("Port should be in range from %d to %d, got %d",
                    minPort, maxPort, parsed_port));
        }
        return parsed_port;
    }
}
