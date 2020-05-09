package com.packettracer.grader.args;

import com.packettracer.grader.Constants;
import com.packettracer.grader.exceptions.ParseError;

public class Args {
    private final String source;
    private final String key;
    private final String target;
    private final int port;
    private final String host;
    private final int attempts;
    private final int delay;

    Args(String source, String password, String target, String port, String host, String attempts, String delay) throws ParseError {
        this.source = source;
        this.key = password;
        this.target = target;
        this.port = parsePort(port);
        this.host = parseHost(host);
        this.attempts = parseAttempts(attempts);
        this.delay = parseDelay(delay);
    }

    public String getSource() {
        return source;
    }

    public String getKey() {
        return key;
    }

    public String getTarget() {
        return target;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getDelay() {
        return delay;
    }

    private int parsePort(String port) {
        return port != null ? Integer.parseInt(port) : Constants.DEFAULT_PORT;
    }

    private String parseHost(String host) {
        return host != null ? host : Constants.DEFAULT_HOST;
    }

    private int parseAttempts(String attempts) {
        return attempts != null ? Integer.parseInt(attempts) : Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER;
    }

    private int parseDelay(String delay) throws ParseError {
        int parsed_delay = delay != null ? Integer.parseInt(delay) : Constants.DEFAULT_CONNECTION_ATTEMPTS_DELAY;
        if (parsed_delay < Constants.MIN_CONNECTION_ATTEMPTS_DELAY || parsed_delay > Constants.MAX_CONNECTION_ATTEMPTS_DELAY) {
            throw new ParseError(String.format("Delay between attempts should be %d <= delay <= %d, got %d",
                    Constants.MIN_CONNECTION_ATTEMPTS_DELAY, Constants.MAX_CONNECTION_ATTEMPTS_DELAY, parsed_delay));
        }
        return parsed_delay;
    }
}
