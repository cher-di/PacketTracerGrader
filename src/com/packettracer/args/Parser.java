package com.packettracer.args;

import com.packettracer.args.exceptions.ParseError;

public interface Parser {
    Object parse(Object value) throws ParseError;
}
