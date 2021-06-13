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

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

public class ContextsGetIdTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;

    /**
     * @throws Exception
     * @testName: testJobContextGetId
     * @assertion: Section 7.7.2 JobContext
     * @test_Strategy: 1. setup a simple job with one step
     * 2. start job
     * 3. set job exit status equals job id from JobContext in batchlet
     * 4. compare job id 'job1' to job exit status
     *
     * <job id="job1" xmlns="https://jakarta.ee/xml/ns/jakartaee">
     * <step id="step1">
     * <batchlet ref="contextsGetIdJobContextTestBatchlet"/>
     * </step>
     * </job>
     */
    @Test
    public void testJobContextGetId() throws Exception {

        String METHOD = "testJobContextGetId";

        try {

            String jobId = "job1";

            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("contexts_getid_jobcontext", null);
            Reporter.log("Job Status = " + jobExec.getBatchStatus());

            assertWithMessage("job id equals job1", jobId, jobExec.getExitStatus());
            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
            Reporter.log("job completed");
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /**
     * @throws Exception
     * @testName: testStepContextGetId
     * @assertion: Section 7.7.2 StepContext
     * @test_Strategy: 1. setup a simple job with one step
     * 2. start job
     * 3. set job exit status equals step id from StepContext in batchlet
     * 4. compare step id 'step1' to job exit status
     *
     * <job id="job1" xmlns="https://jakarta.ee/xml/ns/jakartaee">
     * <step id="step1">
     * <batchlet ref="contextsGetIdStepContextTestBatchlet"/>
     * </step>
     * </job>
     */
    @Test
    public void testStepContextGetId() throws Exception {

        String METHOD = "testStepContextGetId";

        try {
            String stepId = "step1";

            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("contexts_getid_stepcontext", null);
            Reporter.log("Job Status = " + jobExec.getBatchStatus());

            assertWithMessage("job id equals job1", stepId, jobExec.getExitStatus());

            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
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
