package com.packettracer.args.parsers;

import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.ParseError;

public class DefaultParser implements Parser {
    @Override
    public Object parse(Object value) throws ParseError {
        return value;
    }
}
