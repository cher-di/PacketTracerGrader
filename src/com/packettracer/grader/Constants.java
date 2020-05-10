package com.packettracer.grader;

public interface Constants {
    String DEFAULT_HOST = "localhost";
    int DEFAULT_PORT = 39000;
    int DEFAULT_CONNECTION_ATTEMPTS_NUMBER = 10;
    int DEFAULT_CONNECTION_ATTEMPTS_DELAY = 500;

    int MAX_CONNECTION_ATTEMPTS_DELAY = 60000;
    int MIN_CONNECTION_ATTEMPTS_DELAY = 100;
}
