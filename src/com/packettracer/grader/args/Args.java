package com.packettracer.grader.args;

import java.io.File;
import java.io.IOException;

import com.packettracer.grader.Constants;

public class Args {
    private final String source;
    private final String key;
    private final String target;
    private final int port;
    private final String host;
    private final int connectionAttemptsNumber;

    Args(String source, String password, String target, String port, String host, String connectionAttemptsNumber) throws ParseError {
        this.source = parseExistentFile(source);
        this.key = password;
        this.target = parseNotExistentFile(target);
        this.port = parsePort(port);
        this.host =parseHost(host);
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

    private String parseExistentFile(String filepath) throws ParseError {
        try {
            File file = new File(filepath);
            return file.getCanonicalPath();
        }
        catch (IOException e) {
            throw new ParseError(e.getMessage(), e);
        }
    }

    private String parseNotExistentFile(String filepath) throws ParseError {
        return filepath;
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
