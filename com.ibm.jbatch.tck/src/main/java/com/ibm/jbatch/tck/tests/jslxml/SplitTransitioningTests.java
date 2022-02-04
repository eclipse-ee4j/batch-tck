/*
 * Copyright 2013, 2022 International Business Machines Corp. and others
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

import ee.jakarta.tck.batch.api.Reporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ee.jakarta.tck.batch.api.EETest;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

public class SplitTransitioningTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;

    /**
     * @throws JobStartException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testSplitTransitionToStep
     * @assertion: Section 5.4 Split
     * @test_Strategy: 1. setup a job consisting of one split (w/ 2 flows) and one step
     * 2. start job
     * 3. add step id from step context to job context exit status
     * 4. verify that the split indeed transitioned to the step
     */
    @EETest
    public void testSplitTransitionToStep() throws Exception {

        String METHOD = "testSplitTransitionToStep";

        try {
            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("split_transition_to_step", null);
            Reporter.log("Job Status = " + jobExec.getBatchStatus());

            assertWithMessage("Split transitioned to step", "step1", jobExec.getExitStatus());

            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
            Reporter.log("job completed");
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /**
     * @throws JobStartException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testSplitTransitionToStepOutOfScope
     * @assertion: Section 5.4 Split
     * @test_Strategy: 1. setup a job consisting of one split (w/ 2 flows) and one step
     * 2. start job
     * 3. this job should fail because the split flow 'flow1' next to outside the split
     *
     * <split id="split1">
     * <flow id="flow1" next="step1">
     * <step id="flow1step1" next="flow1step2">
     * <batchlet ref="splitTransitionToDecisionTestBatchlet"/>
     * </step>
     * <step id="flow1step2">
     * <batchlet ref="splitTransitionToDecisionTestBatchlet"/>
     * </step>
     * </flow>
     * <flow id="flow2">
     * <step id="flow1step3" next="flow1step4">
     * <batchlet ref="splitTransitionToDecisionTestBatchlet"/>
     * </step>
     * <step id="flow1step4">
     * <batchlet ref="splitTransitionToDecisionTestBatchlet"/>
     * </step>
     * </flow>
     * </split>
     *
     * <step id="step1">
     * <batchlet ref="splitTransitionToStepTestBatchlet"/>
     * </step>
     */
    @EETest
    public void testSplitTransitionToStepOutOfScope() throws Exception {

        String METHOD = "testSplitTransitionToStepOutOfScope";

        try {
            Reporter.log("starting job");

            boolean seenException = false;
            JobExecution jobExec = null;
            try {
                jobExec = jobOp.startJobAndWaitForResult("split_transition_to_step_out_of_scope", null);
            } catch (JobStartException e) {
                Reporter.log("Caught JobStartException:  " + e.getLocalizedMessage());
                seenException = true;
            }

            // If we caught an exception we'd expect that a JobExecution would not have been created,
            // though we won't validate that it wasn't created.

            // If we didn't catch an exception that we require that the implementation fail the job execution.
            if (!seenException) {
                Reporter.log("Didn't catch JobstartException, Job Batch Status = " + jobExec.getBatchStatus());
                assertWithMessage("Job should have failed because of out of scope execution elements.", BatchStatus.FAILED, jobExec.getBatchStatus());
            }
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /**
     * @throws JobStartException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testSplitTransitionToDecision
     * @assertion: Section 5.4 Split
     * @test_Strategy: 1. setup a job consisting of one split (w/ 2 flows) and one decision
     * 2. start job
     * 3. split will transition to decider which will change the exit status
     * 4. compare that the exit status set by the decider matches that of the job
     */
    @EETest
    public void testSplitTransitionToDecision() throws Exception {

        String METHOD = "testSplitTransitionToDecision";

        try {
            String exitStatus = "ThatsAllFolks";
            // based on our decider exit status
			/*
			<decision id="decider1" ref="flowTransitionToDecisionTestDecider">
				<end exit-status="ThatsAllFolks" on="DECIDER_EXIT_STATUS*2" />
			</decision>
			 */
            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("split_transition_to_decision", null);
            Reporter.log("Job Status = " + jobExec.getBatchStatus());

            assertWithMessage("Job Exit Status is from decider", exitStatus, jobExec.getExitStatus());
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
