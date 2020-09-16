package com.packettracer.grader;

import com.cisco.pt.ipc.system.ActivityFile;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ActivityData {
    private final String name;
    private final String email;
    private final String addInfo;

    private final Double percentageComplete;
    private final Double percentageCompleteScore;

    private final Integer timeElapsed;
    private final Integer countDownTime;
    private final Integer countDownTimeLeft;

    private final List<String> components;

    private final Double assessmentItemsCount;
    private final Double correctAssessmentItemsCount;
    private final Double correctAssessmentScoreCount;

    private final String completedFeedback;
    private final String incompleteFeedback;
    private final String dynamicFeedbackString;

    private final Double connectivityCount;

    private final ArrayList<HashMap<String, String>> instructions;

    private final HashMap<String, String> variables;


    ActivityData(ActivityFile activityFile) {
        name = activityFile.getUserProfile().getName();
        email = activityFile.getUserProfile().getEmail();
        addInfo = activityFile.getUserProfile().getAddInfo();

        percentageComplete = activityFile.getPercentageComplete();
        percentageCompleteScore = activityFile.getPercentageCompleteScore();

        timeElapsed = activityFile.getTimeElapsed();
        countDownTime = activityFile.getCountDownTime();
        countDownTimeLeft = activityFile.getCountDownTimeLeft();

        components = activityFile.getComponentList();

        assessmentItemsCount = activityFile.getAssessmentItemsCount();
        correctAssessmentItemsCount = activityFile.getCorrectAssessmentItemsCount();
        correctAssessmentScoreCount = activityFile.getCorrectAssessmentScoreCount();

        completedFeedback = activityFile.getCompletedFeedback();
        incompleteFeedback = activityFile.getIncompleteFeedback();
        dynamicFeedbackString = activityFile.getDyFeedbackString();

        connectivityCount = activityFile.getConnectivityCount();

        instructions = getInstructions(activityFile);

        variables = getVariables(activityFile);
    }

    private static HashMap<String, String> getVariables(ActivityFile activityFile) {
        return new HashMap<>() {{
            var variableManager = activityFile.getVariableManager();
            for (int i = 0; i < variableManager.getVariableSize(); i++) {
                var variable = variableManager.getVariable(i);
                put(variable.name(), variable.valueToString());
            }
        }};
    }

    private static ArrayList<HashMap<String, String>> getInstructions(ActivityFile activityFile) {
        var instructions = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < activityFile.getInstructionCount(); i++) {
            int finalI = i;
            instructions.add(new HashMap<>() {{
                put("instruction", activityFile.getInstruction(finalI));
                put("instructionSource", activityFile.getInstructionSource(finalI));
            }});
        }
        return instructions;
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
