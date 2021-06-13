/*
 * Copyright 2013, 2020 International Business Machines Corp. and others
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

import jakarta.batch.operations.JobExecutionNotMostRecentException;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

public class RestartNotMostRecentTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;

    /*
     * @testName: testRestartNotMostRecentException
     *
     * @assertion: FIXME
     *
     * @test_Strategy: FIXME
     */
    @Test
    public void testRestartNotMostRecentException() throws Exception {
        String METHOD = "testRestartNotMostRecentException";

        try {
            Reporter.log("starting job");
            Properties jobParams = new Properties();
            Reporter.log("execution.number=1<p>");
            jobParams.put("execution.number", "1");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_attributes_restart_true_test", jobParams);

            Properties restartParams = new Properties();
            Reporter.log("execution.number=2<p>");
            restartParams.put("execution.number", "2");
            jobOp.restartJobAndWaitForResult(jobExec.getExecutionId(), restartParams);

            try {
                Reporter.log("Trying to execute the first job execution again.");
                jobOp.restartJobAndWaitForResult(jobExec.getExecutionId(), restartParams);
                assertWithMessage("It should have thrown JobExecutionNotMostRecentException", false);
            } catch (JobExecutionNotMostRecentException e) {
                assertWithMessage("JobExecutionNotMostRecentException thrown", true);
            }

            Reporter.log("Job Status = " + jobExec.getBatchStatus());
            Reporter.log("job completed");

        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }


    private static void handleException(String methodName, Exception e) throws Exception {
        Reporter.log("Caught exception: " + e.getMessage() + "<p>");
        Reporter.log(methodName + " failed<p>");
        throw e;
    }

    @BeforeAll
    public static void beforeTest() throws ClassNotFoundException {
        jobOp = new JobOperatorBridge();
    }

    @AfterAll
    public static void afterTest() {
        jobOp = null;
    }

}
