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

public class FlowTransitioningTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;

    /**
     * @throws JobStartException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testFlowTransitionToStep
     * @assertion: Section 5.3 Flow
     * @test_Strategy: 1. setup a job consisting of one flow (w/ 3 steps) and one step
     * 2. start job
     * 3. create a list of step id's as they are processed
     * 4. return the list from step 3 as job exit status
     * 5. compare that list to our transition list
     * 6. verify that in fact we transition from each step within the flow, then to the flow "next" step
     */
    @EETest
    public void testFlowTransitionToStep() throws Exception {

        String METHOD = "testFlowTransitionToStep";

        try {

            String[] transitionList = {"flow1step1", "flow1step2", "flow1step3", "step1"};
            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("flow_transition_to_step", null);
            Reporter.log("Job Status = " + jobExec.getBatchStatus());

            String[] jobTransitionList = jobExec.getExitStatus().split(",");
            assertWithMessage("transitioned to exact number of steps", transitionList.length, jobTransitionList.length);
            for (int i = 0; i < jobTransitionList.length; i++) {
                assertWithMessage("Flow transitions", transitionList[i], jobTransitionList[i].trim());
            }

            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
            Reporter.log("Job completed");
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /**
     * @throws JobStartException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testFlowTransitionToStepOutOfScope
     * @assertion: Section 5.3 Flow
     * @test_Strategy: 1. setup a job consisting of one flow (w/ 3 steps) and one step
     * 2. start job
     * 3. this job should fail because the flow step flow1step2 next to outside the flow
     * (Alternatively, the implementation may choose to validate and prevent this from starting,
     * by throwing a JobStartException).
     *
     * <flow id="flow1">
     * <step id="flow1step1" next="flow1step2">
     * <batchlet ref="flowTransitionToStepTestBatchlet"/>
     * </step>
     * <step id="flow1step2" next="step1">
     * <batchlet ref="flowTransitionToStepTestBatchlet"/>
     * </step>
     * <step id="flow1step3">
     * <batchlet ref="flowTransitionToStepTestBatchlet"/>
     * </step>
     * </flow>
     *
     * <step id="step1">
     * <batchlet ref="flowTransitionToStepTestBatchlet"/>
     * </step>
     */
    @EETest
    public void testFlowTransitionToStepOutOfScope() throws Exception {

        String METHOD = " testFlowTransitionToStepOutOfScope";

        try {
            boolean seenException = false;
            Reporter.log("starting job");
            JobExecution jobExec = null;
            try {
                jobExec = jobOp.startJobAndWaitForResult("flow_transition_to_step_out_of_scope", null);
            } catch (JobStartException e) {
                Reporter.log("Caught JobStartException:  " + e.getLocalizedMessage());
                seenException = true;
            }

            // If we caught an exception we'd expect that a JobExecution would not have been created,
            // though we won't validate that it wasn't created.

            // If we didn't catch an exception that we require that the implementation fail the job execution.
            if (!seenException) {
                Reporter.log("Didn't catch JobStartException, Job Batch Status = " + jobExec.getBatchStatus());
                assertWithMessage("Job should have failed because of out of scope execution elements.",
                        BatchStatus.FAILED, jobExec.getBatchStatus());
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
     * @testName: testFlowTransitionToDecision
     * @assertion: Section 5.3 Flow
     * @test_Strategy: 1. setup a job consisting of one flow (w/ 3 steps) and one decision
     * 2. start job
     * 3. flow will transition to decider which will change the exit status
     * 4. compare that the exit status set by the decider matches that of the job
     */
    @EETest
    public void testFlowTransitionToDecision() throws Exception {

        String METHOD = "testFlowTransitionToDecision";

        try {
            String exitStatus = "ThatsAllFolks";
            // based on our decider exit status
			/*
			<decision id="decider1" ref="flowTransitionToDecisionTestDecider">
				<end exit-status="ThatsAllFolks" on="DECIDER_EXIT_STATUS*VERY GOOD INVOCATION" />
			</decision>
			 */
            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("flow_transition_to_decision", null);
            Reporter.log("Job Status = " + jobExec.getBatchStatus());

            assertWithMessage("Job Exit Status is from decider", exitStatus, jobExec.getExitStatus());
            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
            Reporter.log("Job completed");
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /**
     * @throws JobStartException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testFlowTransitionWithinFlow
     * @assertion: Section 5.3 Flow
     * @test_Strategy: 1. setup a job consisting of one flow (w/ 3 steps and 1 decision)
     * 2. start job
     * 3. within the flow step1 will transition to decider then to step2 and finally step3.
     * 4. create a list of step id's as they are processed
     * 4. return the list from step 3 as job exit status
     * 5. compare that list to our transition list
     * 6. verify that in fact we transition from each step within the flow, then to the flow "next" step
     */
    @EETest
    public void testFlowTransitionWithinFlow() throws Exception {

        String METHOD = "testFlowTransitionWithinFlow";

        try {
            String[] transitionList = {"flow1step1", "flow1step2", "flow1step3"};
            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("flow_transition_within_flow", null);
            Reporter.log("Job Status = " + jobExec.getBatchStatus());

            String[] jobTransitionList = jobExec.getExitStatus().split(",");
            assertWithMessage("transitioned to exact number of steps", transitionList.length, jobTransitionList.length);
            for (int i = 0; i < jobTransitionList.length; i++) {
                assertWithMessage("Flow transitions", transitionList[i], jobTransitionList[i].trim());
            }

            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
            Reporter.log("Job completed");
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
