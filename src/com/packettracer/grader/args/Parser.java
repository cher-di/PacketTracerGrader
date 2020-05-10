package com.packettracer.grader.args;

import com.packettracer.grader.args.exceptions.ParseError;

public interface Parser {
    Object parse(String value) throws ParseError;
}
