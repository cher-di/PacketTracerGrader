package com.packettracer.args.parsers;

import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.ParseError;

public class BooleanDefaultFalseParser implements Parser {
    @Override
    public Boolean parse(Object value) throws ParseError {
        return (Boolean) value;
    }
}
