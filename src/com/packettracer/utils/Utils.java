package com.packettracer.utils;

import com.cisco.pt.launcher.PacketTracerLaunchException;
import com.cisco.pt.launcher.PacketTracerLauncher;

import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Utils {
    public static Process launchPacketTracer(Integer port, Boolean noGui) throws PacketTracerLaunchException {
        PacketTracerLauncher launcher = PacketTracerLauncher.getInstance();
        return launcher.launch(port, noGui);
    }

    public static String getFirstLetter(String string) {
        return getFirstLetters(string, 1);
    }

    public static String getFirstLetters(String string, int num) {
        return string.substring(0, num);
    }

    public static String hashPassword(String password, List<Integer> challengeKey) throws NoSuchAlgorithmException {
        Object[] salt = challengeKey.toArray();
        byte[] array = new byte[salt.length];
        for (int i = 0; i < salt.length; i++) {
            array[i] = (byte) (int) salt[i];
        }

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(array);
        messageDigest.update(password.getBytes());
        byte[] digest = messageDigest.digest();
        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            result.append(Integer.toHexString((0x000000ff & b) | 0xffffff00).substring(6));
        }
        return result.toString().toUpperCase();
    }

    public static void writeDataToFile(String data, String filepath) throws IOException {
        FileWriter writer = new FileWriter(filepath);
        writer.write(data);
        writer.flush();
    }
}
