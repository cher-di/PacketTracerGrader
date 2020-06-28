package com.packettracer.args.parsers;

import com.packettracer.Constants;
import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.ParseError;

public class HostParser implements Parser {
    @Override
    public String parse(Object host) throws ParseError {
        String _host = (String) host;
        return _host != null ? _host : Constants.DEFAULT_HOST;
    }
}
