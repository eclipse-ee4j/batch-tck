package com.ibm.jbatch.tck.utils;

import java.util.logging.Logger;
import org.junit.jupiter.api.TestReporter;

public class Reporter {

    public static ThreadLocal<TestReporter> reporterRef = new ThreadLocal<>();
    
    public static void log(String message) {
        TestReporter reporter = reporterRef.get();
        if (reporter != null) {
            reporter.publishEntry(message);
        } else {
            Logger.getLogger(Reporter.class.getName()).info(message);
        }
    }
    
}
