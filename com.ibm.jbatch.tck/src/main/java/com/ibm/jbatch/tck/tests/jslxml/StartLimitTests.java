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

import java.util.List;
import java.util.Properties;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.StepExecution;

import ee.jakarta.tck.batch.util.Reporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;
import com.ibm.jbatch.tck.utils.TCKJobExecutionWrapper;

public class StartLimitTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;


    /*
     * @testName: testStartLimitVariation1
     * @assertion:    This description applies to all methods in the class:
     *
     *                1. tests the "start-limit" attribute on steps, when explicitly set or defaulting, or set to '0' (= no limit)
     *                2. Also tests the interaction between start-limit and allow-start-if-complete.  By this we mean that we don't
     *                   count against the start-limit simply because we transition to a step during restart, if allow-start-if-complete=false,
     *                   but only if we execute it.
     *                3. Tests that start-limit counts against the executions that ran to completion as well as the ones that did not.
     *                4. Tests that allow-start-if-complete "trumps" start-limit in the sense
     *                5. Show we can complete all steps in a job without completing the job.
     * @test_Strategy: Reuse a batchlet in multiple steps with these characteristics.
     *                 step1 - allow-start-if-complete=true, start-limit=0
     *                 step2 - allow-start-if-complete=true, (start-limit defaults)
     *                 step3 - allow-start-if-complete=false, start-limit=3
     *                 step4 - allow-start-if-complete=true, start-limit=3
     *                 step5 - allow-start-if-complete=true, start-limit=3
     *
     *                 // Variation1
     *                 Run 1 - c1, c2, x3
     *                 Run 2 - c1, c2, x3
     *                 Run 3 - c1, c2, x3
     *                 Run 4 - c1, c2, FAIL on t3
     *
     *                 // Variation2
     *                 Run 1 - c1, c2, x3
     *                 Run 2 - c1, c2, x3
     *                 Run 3 - c1, c2, s3,
     *                 Run 4 - c1, c2,     s4,restart=3
     *                 Run 5 -             f4
     *                 Run 6 - c1, c2,     c4, f5
     *                 Run 7 - c1, c2,     FAIL on t4
     *
     *                 // Variation3
     *                 Run 1 - c1, c2, c3, c4, s5,restart=5
     *                 Run 2 -                 f5
     *                 Run 3 - c1, c2,     c4  c5
     *
     *                 key
     *                 c - completed
     *                 s - stopped (via JSL transition element)
     *                 x - failed due to exception
     *                 f - failed (via JSL transition element)
     *
     */
    @Test
    public void testStartLimitVariation1() throws Exception {

        String METHOD = "testStartLimitVariation1";

        try {
            long lastExecutionId = 0L;
            TCKJobExecutionWrapper exec = null;

            /*
             * Run 1 - c1, c2, x3
             * Run 2 - c1, c2, x3
             * Run 3 - c1, c2, x3
             * Run 4 - c1, c2, FAIL on t3
             */
            String[] expectedExitStatuses = {"c1,c2,x3", "c1,c2,x3", "c1,c2,x3", "c1,c2"};
            // Note we start at '1'
            for (int i = 1; i <= 4; i++) {
                String execString = new Integer(i).toString();
                Properties jobParameters = new Properties();
                jobParameters.put("execution.number", execString);
                jobParameters.put("batchlet.ref", "startLimitStateMachineVariation1Batchlet");
                if (i == 1) {
                    Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
                    exec = jobOp.startJobAndWaitForResult("startLimitTests", jobParameters);
                } else {
                    Reporter.log("Invoke restartJobAndWaitForResult<p>");
                    exec = jobOp.restartJobAndWaitForResult(lastExecutionId, jobParameters);
                }
                lastExecutionId = exec.getExecutionId();

                Reporter.log("Execution #" + i + " JobExecution getBatchStatus()=" + exec.getBatchStatus() + "<p>");
                Reporter.log("Execution #" + i + " JobExecution getExitStatus()=" + exec.getExitStatus() + "<p>");

                // Typical TCK test execution # is 1-indexed but array is 0-indexed so use 'i-1' below.
                assertWithMessage("Testing execution #" + i, expectedExitStatuses[i - 1], exec.getExitStatus());

                /////
                // All four are designed to FAIL, but with exit status checking and StepExecution checking we're sure
                // we're executing as planned.
                /////
                assertWithMessage("Testing execution #" + i, BatchStatus.FAILED, exec.getBatchStatus());

                List<StepExecution> steps = jobOp.getStepExecutions(lastExecutionId);
                if (i >= 1 && i <= 3) {
                    assertWithMessage("Found 3 step executions", 3, steps.size());
                } else {
                    assertWithMessage("Found 2 step executions", 2, steps.size());
                }
                for (StepExecution step : steps) {
                    if (step.getStepName().equals("step1")) {
                        assertWithMessage("Testing step execution #" + i + ", step1: ", BatchStatus.COMPLETED, step.getBatchStatus());
                    } else if (step.getStepName().equals("step2")) {
                        assertWithMessage("Testing step execution #" + i + ", step2: ", BatchStatus.COMPLETED, step.getBatchStatus());
                    } else if (step.getStepName().equals("step3")) {
                        assertWithMessage("Shouldn't have a StepExecution for step 3 on execution #4", i != 4);
                        assertWithMessage("Testing step execution #" + i + ", step3: ", BatchStatus.FAILED, step.getBatchStatus());
                    } else {
                        throw new IllegalStateException("Shouldn't get here, unknown step");
                    }
                }
            }
        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }

    /*
     * @testName: testStartLimitVariation2
     * @assertion:
     * @test_Strategy:
     */
    @Test
    public void testStartLimitVariation2() throws Exception {

        String METHOD = "testStartLimitVariation2";

        try {
            long lastExecutionId = 0L;
            TCKJobExecutionWrapper exec = null;

            String[] expectedExitStatuses = {"c1,c2,x3", "c1,c2,x3", "c1,c2,s3", "c1,c2,s4",
                    "f4", "c1,c2,c4,f5", "c1,c2"};
            // Note we start at '1'
            for (int i = 1; i <= 7; i++) {
                String execString = new Integer(i).toString();
                Properties jobParameters = new Properties();
                jobParameters.put("execution.number", execString);
                jobParameters.put("batchlet.ref", "startLimitStateMachineVariation2Batchlet");
                if (i >= 4) {
                    // We want it to stop on step 3 on execution 3, but on execution 4 and after
                    // we want to gain further coverage by continuing the restart, so we override
                    // the stop @on value so that we transition on to the step 4.
                    jobParameters.put("stop.after.step.3", "DON'T_MATCH_ME");
                }
                if (i >= 5) {
                    // We want it to stop on step 3 on execution 3, but on execution 4 and after
                    // we want to gain further coverage by continuing the restart, so we override
                    // the stop @on value so that we transition on to the step 4.
                    jobParameters.put("stop.after.step.4", "DON'T_MATCH_ME");
                }
                if (i == 1) {
                    Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
                    exec = jobOp.startJobAndWaitForResult("startLimitTests", jobParameters);
                } else {
                    Reporter.log("Invoke restartJobAndWaitForResult<p>");
                    exec = jobOp.restartJobAndWaitForResult(lastExecutionId, jobParameters);
                }
                lastExecutionId = exec.getExecutionId();

                Reporter.log("Execution #" + i + " JobExecution getBatchStatus()=" + exec.getBatchStatus() + "<p>");
                Reporter.log("Execution #" + i + " JobExecution getExitStatus()=" + exec.getExitStatus() + "<p>");

                // Typical TCK test execution # is 1-indexed but array is 0-indexed so use 'i-1' below.
                assertWithMessage("Testing execution #" + i, expectedExitStatuses[i - 1], exec.getExitStatus());

                // All are failed or stopped...refer to comment above.
                if (i == 1 || i == 2 || i == 5 || i == 6 || i == 7) {
                    assertWithMessage("Testing execution #" + i, BatchStatus.FAILED, exec.getBatchStatus());
                } else if (i == 3 || i == 4) {
                    assertWithMessage("Testing execution #" + i, BatchStatus.STOPPED, exec.getBatchStatus());
                }

                List<StepExecution> steps = jobOp.getStepExecutions(lastExecutionId);
                if (i >= 1 && i <= 4) {
                    assertWithMessage("Found 3 step executions", 3, steps.size());
                } else if (i == 5) {
                    assertWithMessage("Found 1 step executions", 1, steps.size());
                } else if (i == 6) {
                    assertWithMessage("Found 4 step executions", 4, steps.size());
                } else if (i == 7) {
                    assertWithMessage("Found 2 step executions", 2, steps.size());
                }


                /*
                 *  Run 1 - c1, c2, x3
                 *  Run 2 - c1, c2, x3
                 *  Run 3 - c1, c2, s3,
                 *  Run 4 - c1, c2,     s4,restart=3
                 *  Run 5 -             f4
                 *  Run 6 - c1, c2,     c4, f5
                 *  Run 7 - c1, c2,     FAIL on t4
                 */

                for (StepExecution step : steps) {
                    if (step.getStepName().equals("step1")) {
                        assertWithMessage("Shouldn't have a StepExecution for step 1 on execution #5, i= " + i, i != 5);
                        assertWithMessage("Testing step execution #" + i + ", step1: ", BatchStatus.COMPLETED, step.getBatchStatus());
                    } else if (step.getStepName().equals("step2")) {
                        assertWithMessage("Shouldn't have a StepExecution for step 1 on execution #5, i = " + i, i != 5);
                        assertWithMessage("Testing step execution #" + i + ", step2: ", BatchStatus.COMPLETED, step.getBatchStatus());
                    } else if (step.getStepName().equals("step3")) {
                        assertWithMessage("Should only have a StepExecution for step 3 on executions #1,2,3, i= " + i, i <= 3);
                        if (i <= 2) {
                            assertWithMessage("Testing step execution #" + i + ", step3: ", BatchStatus.FAILED, step.getBatchStatus());
                        } else {
                            assertWithMessage("Testing step execution #" + i + ", step3: ", BatchStatus.COMPLETED, step.getBatchStatus());
                        }
                    } else if (step.getStepName().equals("step4")) {
                        assertWithMessage("Should only have a StepExecution for step 4 on executions #4,5,6, i = " + i, i >= 4 && i <= 6);
                        assertWithMessage("Testing step execution #" + i + ", step4: ", BatchStatus.COMPLETED, step.getBatchStatus());
                    } else if (step.getStepName().equals("step5")) {
                        assertWithMessage("Should only have a StepExecution for step 5 on execution #6, i = " + i, i == 6);
                        assertWithMessage("Testing step execution #" + i + ", step5: ", BatchStatus.COMPLETED, step.getBatchStatus());
                    } else {
                        throw new IllegalStateException("Shouldn't get here, unknown step");
                    }
                }
            }
        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }

    /*
     * @testName: testStartLimitVariation3
     * @assertion:
     * @test_Strategy:
     */
    @Test
    public void testStartLimitVariation3() throws Exception {

        String METHOD = "testStartLimitVariation3";

        try {
            long lastExecutionId = 0L;
            TCKJobExecutionWrapper exec = null;

            /*
             * Run 1 - c1, c2, c3, c4, s5,restart=5
             * Run 2 -                 f5
             * Run 3 - c1, c2,     c4  c5
             */
            String[] expectedExitStatuses = {"c1,c2,c3,c4,s5", "f5", "c1,c2,c4,c5"};
            // Note we start at '1'
            for (int i = 1; i <= 3; i++) {
                String execString = new Integer(i).toString();
                Properties jobParameters = new Properties();
                jobParameters.put("execution.number", execString);
                jobParameters.put("batchlet.ref", "startLimitStateMachineVariation3Batchlet");
                if (i == 1) {
                    Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
                    exec = jobOp.startJobAndWaitForResult("startLimitTests", jobParameters);
                } else {
                    Reporter.log("Invoke restartJobAndWaitForResult<p>");
                    exec = jobOp.restartJobAndWaitForResult(lastExecutionId, jobParameters);
                }
                lastExecutionId = exec.getExecutionId();

                Reporter.log("Execution #" + i + " JobExecution getBatchStatus()=" + exec.getBatchStatus() + "<p>");
                Reporter.log("Execution #" + i + " JobExecution getExitStatus()=" + exec.getExitStatus() + "<p>");

                // Typical TCK test execution # is 1-indexed but array is 0-indexed so use 'i-1' below.
                assertWithMessage("Testing execution #" + i, expectedExitStatuses[i - 1], exec.getExitStatus());

                if (i == 1) {
                    assertWithMessage("Testing execution #" + i, BatchStatus.STOPPED, exec.getBatchStatus());
                } else if (i == 2) {
                    assertWithMessage("Testing execution #" + i, BatchStatus.FAILED, exec.getBatchStatus());
                } else {
                    assertWithMessage("Testing execution #" + i, BatchStatus.COMPLETED, exec.getBatchStatus());
                }

                List<StepExecution> steps = jobOp.getStepExecutions(lastExecutionId);
                if (i == 1) {
                    assertWithMessage("Found 5 step executions", 5, steps.size());
                } else if (i == 2) {
                    assertWithMessage("Found 1 step execution", 1, steps.size());
                } else {
                    assertWithMessage("Found 4 step execution", 4, steps.size());
                }

                // All steps will be COMPLETED
                for (StepExecution step : steps) {
                    assertWithMessage("Testing step execution #" + i + ", stepName = " + step.getStepName(), BatchStatus.COMPLETED, step.getBatchStatus());
                }
            }
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
