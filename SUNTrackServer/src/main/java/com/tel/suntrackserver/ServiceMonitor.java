package com.tel.suntrackserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @Created on Nov 30, 2018
 * @author chiraran (Giovanrich/JWizard)
 */
public class ServiceMonitor {

    /**
     * This method logs each service to its temp file
     */
    public static void logServiceToTemp(String SERVICE_NAME) throws IOException {
        File file = new File("C:\\stlogs\\temp\\" + SERVICE_NAME);
        file.delete();
        Process process = Runtime.getRuntime().exec("sc query " + SERVICE_NAME);
        Scanner scanner = new Scanner(process.getInputStream());
        while (scanner.hasNext()) {
            PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
            p.println(scanner.nextLine());
        }
    }

    /**
     * This method returns a method and is status from the temp log files
     */
    public static String[] getServiceStatus(String SERVICE_NAME) throws FileNotFoundException, IOException {
        String[] log = new String[2];
        log[1] = "STOPPED";
        File file = new File("C:\\stlogs\\temp\\" + SERVICE_NAME);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();//Initializing the pointer
        while (line != null) {
            if (containsWord(line, "SERVICE_NAME")) {//I am in the Service Name Line
                log[0] = (line.trim()).substring(13, line.length() - 1);
            } else if (containsWord(line, "RUNNING")) {//The process is running
                log[1] = "RUNNING";
            }
            line = br.readLine();//This is meant for moving the pointer.
        }
        return log;
    }

    /**
     * This method persists a service to the status log file as an entry.
     */
    public static void logService(String[] array) throws IOException {
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File("C:\\stlogs\\statuslog.txt"), true)), true);
        pw.println(new Date().toString() + " : " + array[0] + " Status: \t" + array[1]);
    }

    /**
     * The method that searches a given service_name from an input stream.
     */
    public static boolean containsWord(String text, String service_name) {
        final String REGEX_FIND_WORD = "(?i).*?\\b%s\\b.*?";
        String regex = String.format(REGEX_FIND_WORD, Pattern.quote(service_name));
        return text.matches(regex);
    }
}
