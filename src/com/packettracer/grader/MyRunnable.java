package com.packettracer.grader;

import com.packettracer.grader.exceptions.BaseGraderError;

public class MyRunnable implements Runnable {
    private ActivityData activityData;
    private Integer returnCode;

    private final String source;
    private final String password;
    private final String host;
    private final Integer port;
    private final Integer attempts;
    private final Integer delay;

    MyRunnable(String source, String password, String host, Integer port, Integer attempts, Integer delay) {
        this.source = source;
        this.password = password;
        this.host = host;
        this.port = port;
        this.attempts = attempts;
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            activityData = Grader.grade(source, password, host, port, attempts, delay);
            returnCode = 0;
        } catch (BaseGraderError e) {
            returnCode = e.getExitStatus().getReturnCode();
        } catch (Exception e) {
            returnCode = Constants.ExitStatus.GENERAL_ERROR.getReturnCode();
        }
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public ActivityData getActivityData() {
        return activityData;
    }
}
