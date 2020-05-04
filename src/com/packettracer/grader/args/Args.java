package com.packettracer.grader.args;

import com.packettracer.grader.Constants;
import com.packettracer.grader.exceptions.ParseError;

public class Args {
    private final String source;
    private final String key;
    private final String target;
    private final int port;
    private final String host;
    private final int connectionAttemptsNumber;

    Args(String source, String password, String target, String port, String host, String connectionAttemptsNumber) throws ParseError {
        this.source = source;
        this.key = password;
        this.target = target;
        this.port = parsePort(port);
        this.host = parseHost(host);
        this.connectionAttemptsNumber = parseAttemptsConnectionNumber(connectionAttemptsNumber);
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

    public int getConnectionAttemptsNumber() {
        return connectionAttemptsNumber;
    }

    private int parsePort(String port) {
        return port != null ? Integer.parseInt(port) : Constants.DEFAULT_PORT;
    }

    private String parseHost(String host) {
        return host != null ? host : Constants.DEFAULT_HOST;
    }

    private int parseAttemptsConnectionNumber(String connectionAttemptsNumber) {
        return connectionAttemptsNumber != null ? Integer.parseInt(connectionAttemptsNumber) : Constants.DEFAULT_CONNECTION_ATTEMPTS_NUMBER;
    }
}
