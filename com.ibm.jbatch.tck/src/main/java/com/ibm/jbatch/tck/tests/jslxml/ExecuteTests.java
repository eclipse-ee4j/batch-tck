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

import java.util.logging.Logger;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.artifacts.specialized.BatchletUsingStepContextImpl;
import com.ibm.jbatch.tck.utils.BaseJUnit5Test;
import com.ibm.jbatch.tck.utils.JobOperatorBridge;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExecuteTests extends BaseJUnit5Test {

    private final static Logger logger = Logger.getLogger(ExecuteTests.class.getName());
    private static JobOperatorBridge jobOp = null;

    @BeforeEach
    public void setUp() throws Exception {
        jobOp = new JobOperatorBridge();
    }

    private static void handleException(String methodName, Exception e) throws Exception {
        Reporter.log("Caught exception: " + e.getMessage() + "<p>");
        Reporter.log(methodName + " failed<p>");
        throw e;
    }

    /*
     * @testName: testMyStepContextBatchlet
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @Test
    public void testMyStepContextBatchlet() throws Exception {

        String METHOD = "testMyStepContextBatchlet";

        try {

            Reporter.log("Invoke startJobAndWaitForResult<p>");

            JobExecution jobExec = jobOp.startJobAndWaitForResult("test_batchlet_stepCtx");

            Reporter.log("EXPECTED JobExecution getExitStatus()=" + BatchletUsingStepContextImpl.GOOD_JOB_EXIT_STATUS + "<p>");
            Reporter.log("ACTUAL JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            Reporter.log("EXPECTED JobExecution getBatchStatus()=COMPLETED<p>");
            Reporter.log("ACTUAL JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            assertObjEquals(BatchletUsingStepContextImpl.GOOD_JOB_EXIT_STATUS, jobExec.getExitStatus());
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }

}
