package recommender;

import happy.coding.io.Logs;

public class Main {


    public static void main(String[] args) {
        try {
            // config logger
            Logs.config("log4j.xml", true);

            // config recommender
            String configFile = "config\\AR.conf";

            // run algorithm
            RecommenderAlgorithm lib = new RecommenderAlgorithm();
            lib.setConfigFiles(configFile);
            lib.executeLib(args);
        } catch(Exception ex) {
            System.err.println(ex.toString());
        }
    }
}
