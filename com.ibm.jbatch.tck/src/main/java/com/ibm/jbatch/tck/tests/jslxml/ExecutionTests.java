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

import static com.ibm.jbatch.tck.utils.AssertionUtils.assertObjEquals;
import com.ibm.jbatch.tck.utils.BaseJUnit5Test;


import java.util.logging.Logger;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ExecutionTests extends BaseJUnit5Test {

    private final static Logger logger = Logger.getLogger(ExecutionTests.class.getName());

    private static JobOperatorBridge jobOp;

    @BeforeEach
    public void setUp() throws Exception {
        jobOp = new JobOperatorBridge();
    }

    private void begin(String str) {
        Reporter.log("Begin test method: " + str);
    }

    /*
     * @testName: testInvokeJobWithOneBatchletStep
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testInvokeJobWithOneBatchletStep() throws Exception {
        String METHOD = "testInvokeJobWithOneBatchletStep";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_batchlet_1step.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_batchlet_1step");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobWithTwoStepSequenceOfBatchlets
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testInvokeJobWithTwoStepSequenceOfBatchlets() throws Exception {
        String METHOD = "testInvokeJobWithTwoStepSequenceOfBatchlets";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_batchlet_2steps.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_batchlet_2steps");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobWithFourStepSequenceOfBatchlets
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testInvokeJobWithFourStepSequenceOfBatchlets() throws Exception {
        String METHOD = "testInvokeJobWithFourStepSequenceOfBatchlets";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_batchlet_4steps.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_batchlet_4steps");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobWithNextElement
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testInvokeJobWithNextElement() throws Exception {
        String METHOD = "testInvokeJobWithNextElement";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_batchlet_nextElement.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_batchlet_nextElement");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobWithFailElement
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testInvokeJobWithFailElement() throws Exception {
        String METHOD = "testInvokeJobWithFailElement";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_batchlet_failElement.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_batchlet_failElement");

            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals("TEST_FAIL", jobExec.getExitStatus());
            assertObjEquals(BatchStatus.FAILED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobWithStopElement
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testInvokeJobWithStopElement() throws Exception {
        String METHOD = "testInvokeJobWithStopElement";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_batchlet_stopElement.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_batchlet_stopElement");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.STOPPED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobWithEndElement
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testInvokeJobWithEndElement() throws Exception {
        String METHOD = "testInvokeJobWithEndElement";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_batchlet_endElement.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_batchlet_endElement");

            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals("TEST_ENDED", jobExec.getExitStatus());
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobSimpleChunk
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testInvokeJobSimpleChunk() throws Exception {
        String METHOD = "testInvokeJobSimpleChunk";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_chunk_simple.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_chunk_simple");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobChunkWithFullAttributes
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    @Disabled("Bug 5379.  Decided to exclude this test.")
    public void testInvokeJobChunkWithFullAttributes() throws Exception {
        String METHOD = "testInvokeJobChunkWithFullAttributes";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_chunk_full_attributes.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_chunk_full_attributes");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testInvokeJobUsingTCCL
     * @assertion: Section 10.5 Batch Artifact loading
     * @test_Strategy: Implementation should attempt to load artifact using Thread Context Class Loader if implementation specific
     * 	and archive loading are unable to find the specified artifact.
     */
    @Test
    public void testInvokeJobUsingTCCL() throws Exception {
        String METHOD = "testInvokeJobUsingTCCL";
        begin(METHOD);

        try {
            Reporter.log("Run job using job XML file: test_artifact_load_classloader<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("test_artifact_load_classloader");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testCheckpoint
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testCheckpoint() throws Exception {
        String METHOD = "testCheckpoint";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_chunk_checkpoint.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_chunk_checkpoint");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testSimpleFlow
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testSimpleFlow() throws Exception {
        String METHOD = "testSimpleFlow";
        begin(METHOD);

        try {
            Reporter.log("Locate job XML file: job_flow_batchlet_4steps.xml<p>");

            Reporter.log("Invoking startJobAndWaitForResult for Execution #1<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_flow_batchlet_4steps");

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
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
