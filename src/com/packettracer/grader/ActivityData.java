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

    ActivityData(String name, String email, Float percentageComplete, Float percentageCompleteScore, String addInfo, Integer timeElapsed) {
        this.name = name;
        this.email = email;
        this.percentageComplete = percentageComplete;
        this.percentageCompleteScore = percentageCompleteScore;
        this.addInfo = addInfo;
        this.timeElapsed = timeElapsed;
    }

    ActivityData(ActivityData other) {
        this.name = other.name;
        this.email = other.email;
        this.percentageComplete = other.percentageComplete;
        this.percentageCompleteScore = other.percentageCompleteScore;
        this.addInfo = other.addInfo;
        this.timeElapsed = other.timeElapsed;
    }

    ActivityData(ActivityFile activityFile) {
        this.name = activityFile.getUserProfile().getName();
        this.email = activityFile.getUserProfile().getEmail();
        this.percentageComplete = (float) activityFile.getPercentageComplete();
        this.percentageCompleteScore = (float) activityFile.getPercentageCompleteScore();
        this.addInfo = activityFile.getUserProfile().getAddInfo();
        this.timeElapsed = activityFile.getTimeElapsed();
    }

    public static ActivityData fromJsonFile(String filepath) throws FileNotFoundException {
        Type ACTIVITY_DATA_TYPE = new TypeToken<ActivityData>() {}.getType();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filepath));
        return gson.fromJson(reader, ACTIVITY_DATA_TYPE);
    }

    public void toJsonFile(String filepath) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(this);
        FileWriter writer = new FileWriter(filepath);
        writer.write(json);
        writer.flush();
    }
}
