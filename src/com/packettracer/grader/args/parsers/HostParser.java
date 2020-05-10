package com.packettracer.grader.args.parsers;

import com.packettracer.grader.Constants;
import com.packettracer.grader.args.Parser;
import com.packettracer.grader.args.exceptions.ParseError;

public class HostParser implements Parser {
    @Override
    public Object parse(String host) throws ParseError {
        return host != null ? host : Constants.DEFAULT_HOST;
    }
}
