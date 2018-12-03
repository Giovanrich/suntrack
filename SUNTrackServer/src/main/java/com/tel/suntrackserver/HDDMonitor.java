package com.tel.suntrackserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * @Created on Nov 20, 2018
 * @author chiraran (Giovanrich/JWizard)
 */
public class HDDMonitor {

    public static void main(String[] args) {

        String[] sunServices = {
            "aspnet_state",
            "MMCSS",
            "Netman",
            "Spooler",
            "PcaSvc",
            "sppsvc"
        };
        //get all the drives in the system
        List<File> allDrives = Arrays.asList(File.listRoots());
        for (File f : allDrives) {
            if (f.exists()) {
                String line = new Date().toString() + ":" + f.getPath() + ":"
                        + "Total Space: " + f.getTotalSpace() / 1024 / 1024 / 1024 + "GB \t"
                        + "Free Space: " + f.getFreeSpace() / 1024 / 1024 / 1024 + "GB \t"
                        + "Usable Space: " + f.getUsableSpace() / 1024 / 1024 / 1024 + "GB \t";
                statusLog(line);
            }
        }

        try {
            statusLog("");
            statusLog("******************** System Services *******************");
            statusLog("");
            for (String service : sunServices) {
                ServiceMonitor.logServiceToTemp(service);
                ServiceMonitor.logService(ServiceMonitor.getServiceStatus(service));
            }
            statusLog("");
            statusLog("CPU Usage:" + Double.toString(getProcessCPULoad()));
            statusLog("---------------------------------------------"
                    + "--------------------------------------------------------");
        } catch (Exception ex) {
            try {
                errorLog(ex);
            } catch (IOException ex1) {
                Logger.getLogger(HDDMonitor.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

    }

    /**
     * This method forked from SO.
     *
     * @return double
     * @throws java.lang.Exception
     */
    public static double getProcessCPULoad() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
        if (list.isEmpty()) {
            return Double.NaN;
        }
        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();
        // usually takes a couple of seconds before we get real values
        if (value == -1.0) {
            return Double.NaN;
        }
        // returns a percentage value with 1 decimal point precision
        return ((int) (value * 1000) / 10.0);
    }

    /**
     * Persists errors to the error log file
     */
    private static void errorLog(Exception ex) throws IOException {
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("C:\\stlogs\\errorlog.txt", true)), true);
        pw.println(new Date().toString() + ":\t The following error occured:\t" + ex.getMessage());
        pw.close();
    }

    /**
     * Persists status to the status log file
     */
    private static void statusLog(String status) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("C:\\stlogs\\statuslog.txt", true)), true);
            pw.println(status);
            pw.close();
        } catch (IOException e) {
            try {
                errorLog(e);
            } catch (IOException ex) {
                Logger.getLogger(HDDMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
