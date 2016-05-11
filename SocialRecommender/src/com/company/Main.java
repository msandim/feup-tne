package com.company;

import com.company.Lib;
import happy.coding.io.Logs;
import happy.coding.system.Systems;
import librec.main.LibRec;

public class Main {


    public static void main(String[] args) {
        try {
            // config logger
            Logs.config("log4j.xml", true);

            // config recommender
            String configFile = "librec.conf";

            // run algorithm
            Lib lib = new Lib();
            lib.setConfigFiles(configFile);
            lib.executeLibRec(args);
        } catch(Exception ex) {
            System.err.println(ex.toString());
        }
    }
}
