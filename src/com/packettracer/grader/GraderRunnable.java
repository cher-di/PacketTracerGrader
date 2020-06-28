package com.packettracer.grader;

import com.cisco.pt.ipc.enums.FileOpenReturnValue;
import com.cisco.pt.ipc.system.ActivityFile;
import com.cisco.pt.ipc.ui.IPC;

import com.packettracer.grader.exceptions.*;
import com.packettracer.utils.Session;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.packettracer.utils.Utils.hashPassword;

public class GraderRunnable implements Runnable {
    private String input;
    private final String password;
    private final String host;
    private final Integer port;
    private final Integer attempts;
    private final Integer delay;

    private ActivityData activityData;

    GraderRunnable(String input, String password, String host, Integer port, Integer attempts, Integer delay) {
        this.input = input;
        this.password = password;
        this.host = host;
        this.port = port;
        this.attempts = attempts;
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            input = formatActivityFilePath(input);

            // Start new session
            Session session = new Session(host, port, attempts, delay);

            // Get session IPC
            IPC ipc = session.getIpc();

            // Open activity file
            ActivityFile activityFile = openActivityFile(input, ipc);

            // Generate password MD5 hash
            List<Integer> challengeKey = activityFile.getChallengeKeyAsInts();
            String hashedPassword = hashPassword(password, challengeKey);

            // Get data
            activityFile.confirmPassword(hashedPassword);
            if (!activityFile.isPasswordConfirmed()) {
                throw new WrongPasswordError("Wrong password");
            } else {
                activityData = new ActivityData(activityFile);
                session.close();
            }
        } catch (Exception e) {
            Thread currentThread = Thread.currentThread();
            currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, e);
        }
    }

    public ActivityData getActivityData() {
        return activityData;
    }

    private static ActivityFile openActivityFile(String filepath, IPC ipc) throws WrongCredentialsError, InputFileReadingError {
        FileOpenReturnValue status = null;
        try {
            status = ipc.appWindow().fileOpen(filepath);
        } catch (Error e) {
            throw new WrongCredentialsError("Wrong credentials", e);
        }
        if (status.compareTo(FileOpenReturnValue.FILE_RETURN_OK) != 0) {
            throw new InputFileReadingError(String.format("Can not open .pka file: %s", filepath));
        }
        return (ActivityFile) ipc.appWindow().getActiveFile();
    }

    private static String formatActivityFilePath(String filepath) throws InputFileReadingError {
        try {
            File file = new File(filepath);
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new InputFileReadingError(e.getMessage(), e);
        }
    }
}
