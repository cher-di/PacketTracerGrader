package com.packettracer.grader;

import com.packettracer.grader.exceptions.*;

import java.util.Map;

public interface Constants {
    String DEFAULT_HOST = "localhost";
    int DEFAULT_PORT = 39000;
    int DEFAULT_CONNECTION_ATTEMPTS_NUMBER = 10;
    int DEFAULT_CONNECTION_ATTEMPTS_DELAY = 500;

    int MAX_CONNECTION_ATTEMPTS_DELAY = 60000;
    int MIN_CONNECTION_ATTEMPTS_DELAY = 100;

    String AUTH_SECRET = "cisco";
    String AUTH_APP = "com.packettracer.grader";

    String ARG_NAME_SOURCE = "source";
    String ARG_NAME_KEY = "key";
    String ARG_NAME_PORT = "port";
    String ARG_NAME_HOST = "host";
    String ARG_NAME_ATTEMPTS = "attempts";
    String ARG_NAME_TARGET = "target";
    String ARG_NAME_DELAY = "delay";

    int EXIT_STATUS_GENERAL_ERROR = 1;
    int EXIT_STATUS_ARGUMENTS_PARSING_FAILED = 2;
    int EXIT_STATUS_SOURCE_FILE_READING_FAILED = 3;
    int EXIT_STATUS_TARGET_FILE_WRITING_FAILED = 4;
    int EXIT_STATUS_UNABLE_TO_CONNECT = 5;
    int EXIT_STATUS_WRONG_PASSWORD = 6;
    int EXIT_STATUS_WRONG_CREDENTIALS = 7;

    Map<Class<? extends BaseGraderError>, Integer> EXCEPTION_TO_EXIT_STATUS_MAPPING = Map.of(
            BaseGraderError.class, EXIT_STATUS_GENERAL_ERROR,
            ParseError.class, EXIT_STATUS_ARGUMENTS_PARSING_FAILED,
            SourceFileReadingError.class, EXIT_STATUS_SOURCE_FILE_READING_FAILED,
            TargetFileWritingError.class, EXIT_STATUS_TARGET_FILE_WRITING_FAILED,
            ConnectionError.class, EXIT_STATUS_UNABLE_TO_CONNECT,
            WrongPasswordError.class, EXIT_STATUS_WRONG_PASSWORD,
            WrongCredentialsError.class, EXIT_STATUS_WRONG_CREDENTIALS
    );
}
