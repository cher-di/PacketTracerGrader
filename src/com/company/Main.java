package com.company;

import com.cisco.pt.impl.OptionsManager;
import com.cisco.pt.ipc.IPCError;
import com.cisco.pt.ipc.IPCFactory;
import com.cisco.pt.ipc.enums.FileOpenReturnValue;
import com.cisco.pt.ipc.system.ActivityFile;
import com.cisco.pt.ipc.ui.IPC;
import com.cisco.pt.ptmp.ConnectionNegotiationProperties;
import com.cisco.pt.ptmp.PacketTracerSession;
import com.cisco.pt.ptmp.PacketTracerSessionFactory;
import com.cisco.pt.ptmp.impl.PacketTracerSessionFactoryImpl;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.apache.commons.codec.digest.Md5Crypt.md5Crypt;

public class Main {
    private static final String HOST = "localhost";
    private static final int PORT = 39000;

    public Main(PacketTracerSession session) {
    }

    public static void main(String[] args)
        throws Exception
    {
        String filepath = args[0];
        var file = new File(filepath);
        System.out.println(file.getCanonicalPath());

        String password = args[1];
        System.out.println(password);

        try {
            // Start by getting an instance of the PT Session factory
            PacketTracerSessionFactory packetTracerSessionFactory = PacketTracerSessionFactoryImpl.getInstance();

            // Get the options used to connect to PT
            ConnectionNegotiationProperties cnp = OptionsManager.getInstance().getConnectOpts();

            // Modify the default options to specify your application parameters
            cnp.setAuthenticationSecret("cisco");
            cnp.setAuthenticationApplication("com.company.grader");
            cnp.setAuthentication(ConnectionNegotiationProperties.MD5_AUTH);

            // Create a PT session
            PacketTracerSession packetTracerSession = packetTracerSessionFactory.openSession(HOST, PORT, cnp);

            // Get the top level IPC object to communicate with PT
            IPCFactory ipcFactory = new IPCFactory(packetTracerSession);
            final IPC ipc = ipcFactory.getIPC();

            // Get percentage of completed
            FileOpenReturnValue status = ipc.appWindow().fileOpen(file.getCanonicalPath());
            System.out.println(status);
            var activityFile = (ActivityFile) ipc.appWindow().getActiveFile();

            // Generate password MD5 hash
            var challengeKey = activityFile.getChallengeKeyAsInts();
            var hashedPassword = hashPassword(password, challengeKey);

            // Get percentage
            var confirmed = activityFile.confirmPassword(hashedPassword);
            if (confirmed) {
                System.out.println("Confirmed");
                var percentage = activityFile.getPercentageComplete();
                System.out.println("Percentage: " + Double.toString(percentage));
                System.out.println("Name: " + activityFile.getUserProfile().getName());
                System.out.println("Email: " + activityFile.getUserProfile().getEmail());
                System.out.println("PercentageScore: " + activityFile.getPercentageCompleteScore());
            }
            else {
                System.out.println("Not confirmed");
            }

            // Close session with Packet Tracer
            packetTracerSession.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String hashPassword(String password, List<Integer> challengeKey) throws NoSuchAlgorithmException {
        var salt = challengeKey.toArray();
        byte[] array = new byte[salt.length];
        for (int i = 0; i < salt.length; i++) {
            array[i] = (byte) (int) salt[i];
        }

        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(array);
        m.update(password.getBytes());
        byte s[] = m.digest();
        String result = "";
        for (int i = 0; i < s.length; i++) {
            result += Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6);
        }
        return result.toUpperCase();
    }
}