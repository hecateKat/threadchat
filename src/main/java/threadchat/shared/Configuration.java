package threadchat.shared;

import java.io.File;

public class Configuration {
    public static Integer serverPort = 7777;
    public static String serverHost = "localhost";
    public static File chatContentFilePath = new File("E:\\threadchat-files\\history-logs.txt");
    public static String fileStorePath = "E:\\threadchat-files\\";
}