package com.packettracer.utils;

import com.cisco.pt.ipc.ui.IPC;
import com.cisco.pt.launcher.PacketTracerLaunchException;
import com.cisco.pt.launcher.PacketTracerLauncher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Utils {
    public static Process launchPacketTracer(Integer port, Boolean noGui) throws PacketTracerLaunchException {
        PacketTracerLauncher launcher = PacketTracerLauncher.getInstance();
        return launcher.launch(port, noGui);
    }

    public static String getFirstLetter(String string) {
        return string.substring(0, 1);
    }

    public static String hashPassword(String password, List<Integer> challengeKey) throws NoSuchAlgorithmException {
        var salt = challengeKey.toArray();
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
}