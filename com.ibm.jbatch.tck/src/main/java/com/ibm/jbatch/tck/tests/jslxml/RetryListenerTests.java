/*
 * Copyright 2012, 2020 International Business Machines Corp. and others
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.ibm.jbatch.tck.tests.jslxml;

import static com.ibm.jbatch.tck.utils.AssertionUtils.assertWithMessage;
import com.ibm.jbatch.tck.utils.BaseJUnit5Test;

import java.util.Properties;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

import ee.jakarta.tck.batch.util.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RetryListenerTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;

    @BeforeEach
    public void setUp() throws Exception {
        jobOp = new JobOperatorBridge();
    }

    /*
     * @testName: testRetryReadListener
     *
     * @assertion: Test will finish in FAILED status, with the onRetryReadException invoked.
     *
     * @test_Strategy: Test that the onRetryReadException listener is invoked when a retryable exception occurs on a read.
     */
    @Test
    public void testRetryReadListener() throws Exception {
        String METHOD = "testRetryReadListener";

        try {
            Reporter.log("Create job parameters for execution #1:<p>");
            Properties jobParams = new Properties();
            Reporter.log("execution.number=1<p>");
            Reporter.log("readrecord.fail=8,13,22<p>");
            Reporter.log("app.arraysize=30<p>");
            jobParams.put("execution.number", "1");
            jobParams.put("readrecord.fail", "8,13,22");
            jobParams.put("app.arraysize", "30");

            Reporter.log("Locate job XML file: job_retry_listener_test.xml<p>");


            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_retry_listener_test", jobParams);

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertWithMessage("Testing execution #1", BatchStatus.FAILED, jobExec.getBatchStatus());
            assertWithMessage("Testing execution #1", "Retry listener invoked", jobExec.getExitStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testRetryProcessListener
     *
     * @assertion: Test will finish in FAILED status, with the onRetryProcessException invoked.
     *
     * @test_Strategy: Test that the onRetryProcessException listener is invoked when a retryable exception occurs on a process.
     */
    @Test
    public void testRetryProcessListener() throws Exception {
        String METHOD = "testRetryProcessListener";

        try {
            Reporter.log("Create job parameters for execution #1:<p>");
            Properties jobParams = new Properties();
            Reporter.log("execution.number=1<p>");
            Reporter.log("processrecord.fail=8,13,22<p>");
            Reporter.log("app.arraysize=30<p>");
            jobParams.put("execution.number", "1");
            jobParams.put("processrecord.fail", "8,13,22");
            jobParams.put("app.arraysize", "30");

            Reporter.log("Locate job XML file: job_retry_listener_test.xml<p>");


            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_retry_listener_test", jobParams);

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertWithMessage("Testing execution #1", BatchStatus.FAILED, jobExec.getBatchStatus());
            assertWithMessage("Testing execution #1", "Retry listener invoked", jobExec.getExitStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testRetryWriteListener
     *
     * @assertion: Test will finish in FAILED status, with the onRetryWriteException invoked.
     *
     * @test_Strategy: Test that the onRetryWriteException listener is invoked when a retryable exception occurs on a write.
     */
    @Test
    public void testRetryWriteListener() throws Exception {
        String METHOD = "testRetryWriteListener";

        try {
            Reporter.log("Create job parameters for execution #1:<p>");
            Properties jobParams = new Properties();
            Reporter.log("execution.number=1<p>");
            Reporter.log("writerecord.fail=8,13,22<p>");
            Reporter.log("app.arraysize=30<p>");
            jobParams.put("execution.number", "1");
            jobParams.put("writerecord.fail", "8,13,22");
            jobParams.put("app.arraysize", "30");


            Reporter.log("Locate job XML file: job_retry_listener_test.xml<p>");


            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_retry_listener_test", jobParams);

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertWithMessage("Testing execution #1", BatchStatus.FAILED, jobExec.getBatchStatus());
            assertWithMessage("Testing execution #1", "Retry listener invoked", jobExec.getExitStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    private static void handleException(String methodName, Exception e) throws Exception {
        Reporter.log("Caught exception: " + e.getMessage() + "<p>");
        Reporter.log(methodName + " failed<p>");
        throw e;
    }

}
