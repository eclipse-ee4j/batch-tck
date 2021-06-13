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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jakarta.batch.operations.JobRestartException;
import jakarta.batch.operations.JobStartException;
import jakarta.batch.operations.NoSuchJobException;
import jakarta.batch.operations.NoSuchJobExecutionException;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JobAttributeRestartTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;

    private long TIMEOUT = 5000L;

    /**
     * @throws JobStartException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @throws JobRestartException
     * @throws NoSuchJobException
     * @throws NoSuchJobExecutionException
     * @throws JobInstanceAlreadyCompleteException
     * @testName: testJobAttributeRestartableTrue
     * @assertion: Section 5.1 job attribute restartable
     * @test_Strategy: set restartable true should allow job to restart
     */
    @Test
    public void testJobAttributeRestartableTrue() throws Exception {

        String METHOD = "testJobAttributeRestartableTrue";

        try {
            Reporter.log("starting job");
            Properties jobParams = new Properties();
            Reporter.log("execution.number=1<p>");
            jobParams.put("execution.number", "1");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_attributes_restart_true_test", jobParams);

            Reporter.log("Job Status = " + jobExec.getBatchStatus());
            assertWithMessage("Job failed ", BatchStatus.FAILED, jobExec.getBatchStatus());

            Reporter.log("restarting job");
            Properties restartParams = new Properties();
            Reporter.log("execution.number=2<p>");
            restartParams.put("execution.number", "2");
            JobExecution newJobExec = jobOp.restartJobAndWaitForResult(jobExec.getExecutionId(), restartParams);

            Reporter.log("Job Status = " + newJobExec.getBatchStatus());
            assertWithMessage("Job completed", BatchStatus.COMPLETED, newJobExec.getBatchStatus());
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
