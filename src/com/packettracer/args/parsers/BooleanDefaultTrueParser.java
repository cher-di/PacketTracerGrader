package com.packettracer.args.parsers;

import com.packettracer.args.Parser;
import com.packettracer.args.exceptions.ParseError;

public class BooleanDefaultTrueParser implements Parser {
    @Override
    public Boolean parse(Object value) throws ParseError {
        Boolean _value = (Boolean) value;
        return !_value;
    }
}
