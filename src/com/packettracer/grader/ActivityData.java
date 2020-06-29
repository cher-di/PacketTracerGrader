package com.packettracer.grader;

import com.cisco.pt.ipc.system.ActivityFile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;

public class ActivityData {
    private final String name;
    private final String email;
    private final Float percentageComplete;
    private final Float percentageCompleteScore;
    private final String addInfo;
    private final Integer timeElapsed;
    private final String labID;

    private static final String labIDVariableName = "LabID";

    ActivityData(String name, String email, Float percentageComplete, Float percentageCompleteScore, String addInfo, Integer timeElapsed, String labID) {
        this.name = name;
        this.email = email;
        this.percentageComplete = percentageComplete;
        this.percentageCompleteScore = percentageCompleteScore;
        this.addInfo = addInfo;
        this.timeElapsed = timeElapsed;
        this.labID = labID;
    }

    ActivityData(ActivityData other) {
        this.name = other.name;
        this.email = other.email;
        this.percentageComplete = other.percentageComplete;
        this.percentageCompleteScore = other.percentageCompleteScore;
        this.addInfo = other.addInfo;
        this.timeElapsed = other.timeElapsed;
        this.labID = other.labID;
    }

    ActivityData(ActivityFile activityFile) {
        this.name = activityFile.getUserProfile().getName();
        this.email = activityFile.getUserProfile().getEmail();
        this.percentageComplete = (float) activityFile.getPercentageComplete();
        this.percentageCompleteScore = (float) activityFile.getPercentageCompleteScore();
        this.addInfo = activityFile.getUserProfile().getAddInfo();
        this.timeElapsed = activityFile.getTimeElapsed();
        this.labID = activityFile.getVariableManager().getVariableByName(labIDVariableName).valueToString();
    }

    public static ActivityData fromJsonFile(String filepath) throws FileNotFoundException {
        Type ACTIVITY_DATA_TYPE = new TypeToken<ActivityData>() {
        }.getType();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filepath));
        return gson.fromJson(reader, ACTIVITY_DATA_TYPE);
    }

    public String toJson(boolean pretty) {
        if (pretty)
            return new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create().toJson(this);
        else
            return new GsonBuilder()
                    .serializeNulls()
                    .create().toJson(this);
    }
}
