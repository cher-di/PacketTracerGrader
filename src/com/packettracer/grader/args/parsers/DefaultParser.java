package com.packettracer.grader.args.parsers;

import com.packettracer.grader.args.Parser;
import com.packettracer.grader.args.exceptions.ParseError;

public class DefaultParser implements Parser {
    @Override
    public Object parse(String value) throws ParseError {
        return value;
    }
}
