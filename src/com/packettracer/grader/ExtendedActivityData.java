package com.packettracer.grader;

import com.google.gson.internal.$Gson$Preconditions;

public class ExtendedActivityData {
    private final String filepath;
    private final Integer returncode;
    private final ActivityData activityData;

    ExtendedActivityData(String name, String email, Float percentageComplete, Float percentageCompleteScore, String addInfo, Integer timeElapsed, String filepath, Integer retruncode) {
        this.activityData = new ActivityData(name, email, percentageComplete, percentageCompleteScore, addInfo, timeElapsed);
        this.filepath = filepath;
        this.returncode = retruncode;
    }

    ExtendedActivityData(ExtendedActivityData other) {
        this.activityData = other.activityData;
        this.filepath = other.filepath;
        this.returncode = other.returncode;
    }

    ExtendedActivityData(ActivityData activityData, String filepath, Integer returncode) {
        this.activityData = activityData;
        this.filepath = filepath;
        this.returncode = returncode;
    }
}
