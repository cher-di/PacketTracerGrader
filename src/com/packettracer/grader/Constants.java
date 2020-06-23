package com.packettracer.grader;

public interface Constants {
    String DEFAULT_HOST = "localhost";
    int DEFAULT_PORT = 39000;
    int DEFAULT_CONNECTION_ATTEMPTS_NUMBER = 10;
    int DEFAULT_CONNECTION_ATTEMPTS_DELAY = 500;

    int MAX_CONNECTION_ATTEMPTS_DELAY = 60000;
    int MIN_CONNECTION_ATTEMPTS_DELAY = 100;

    enum ExitStatus {
        GENERAL_ERROR(1),
        ARGUMENTS_PARSING_FAILED(2),
        SOURCE_FILE_READING_FAILED(3),
        TARGET_FILE_WRITING_FAILED(4),
        UNABLE_TO_CONNECT(5),
        WRONG_PASSWORD(6),
        WRONG_CREDENTIALS(7);

        private final int returnCode;

        ExitStatus(int returnCode) {
            this.returnCode = returnCode;
        }

        public int getReturnCode() {
            return this.returnCode;
        }
    }
}
