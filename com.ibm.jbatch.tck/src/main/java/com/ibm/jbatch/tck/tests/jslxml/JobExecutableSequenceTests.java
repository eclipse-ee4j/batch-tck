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

import java.io.FileNotFoundException;
import java.io.IOException;

import jakarta.batch.operations.JobStartException;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

public class JobExecutableSequenceTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;

    /**
     * @throws JobStartException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testJobExecutableSequenceToUnknown
     * @assertion: Section 5.3 Flow
     * @test_Strategy: 1. setup a job consisting of 3 steps (step1 next to step2, step2 next to unknown, step3 unreachable
     * 2. start job
     * 3. job should fail because it shouldn't be able to transition to unknown
     */
    @Test
    public void testJobExecutableSequenceToUnknown() throws Exception {

        String METHOD = "testJobExecutableSequenceToUnknown";

        try {

            Reporter.log("starting job");
            JobExecution jobExec = null;
            boolean seenException = false;
            try {
                jobExec = jobOp.startJobAndWaitForResult("job_executable_sequence_invalid", null);
            } catch (JobStartException e) {
                Reporter.log("Caught JobStartException:  " + e.getLocalizedMessage());
                seenException = true;
            }
            // If we caught an exception we'd expect that a JobExecution would not have been created,
            // though we won't validate that it wasn't created.
            // If we didn't catch an exception that we require that the implementation fail the job execution.
            if (!seenException) {
                Reporter.log("Didn't catch JobStartException, Job Batch Status = " + jobExec.getBatchStatus());
                assertWithMessage("Job should have failed because of out of scope execution elements.", BatchStatus.FAILED, jobExec.getBatchStatus());
            }
            Reporter.log("job failed");
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
