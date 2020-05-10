package com.packettracer.grader.args;

import com.packettracer.grader.exceptions.ParseError;

public interface Parser {
    Object parse(String value) throws ParseError;
}

@FunctionalInterface
interface StringParser extends Parser {
    String parse(String value) throws ParseError;
}

@FunctionalInterface
interface IntegerParser extends Parser {
    Integer parse(String value) throws ParseError;
}
