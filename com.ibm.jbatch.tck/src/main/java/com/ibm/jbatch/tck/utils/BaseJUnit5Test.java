package com.ibm.jbatch.tck.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestReporter;

public class BaseJUnit5Test {
    
    @BeforeEach
    public void setUpReporter(TestReporter reporter) {
        Reporter.reporterRef.set(reporter);
    }

    @AfterEach
    public void cleanUpReporter() {
        Reporter.reporterRef.remove();
    }
}
