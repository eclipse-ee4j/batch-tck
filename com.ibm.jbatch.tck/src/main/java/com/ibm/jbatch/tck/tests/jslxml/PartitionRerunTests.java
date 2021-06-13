/*
 * Copyright 2016, 2020 International Business Machines Corp. and others
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

import java.util.List;
import java.util.Properties;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.batch.runtime.StepExecution;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ibm.jbatch.tck.ann.*;
import com.ibm.jbatch.tck.utils.BaseJUnit5Test;
import com.ibm.jbatch.tck.utils.JobOperatorBridge;

public class PartitionRerunTests extends BaseJUnit5Test {
    static JobOperatorBridge jobOp = null;

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

    @TCKTest(
            versions = {"1.1.WORKING"},
            assertions = {
                    "Upon job restart, only FAILED partitions of a FAILED partitioned step are re-executed (COMPLETED partitions are not re-executed).",
                    "Upon job restart, all partitions of a COMPLETED partitioned step with allow-start-if-complete=true are re-executed.",
            },
            specRefs = {
                    @SpecRef(
                            version = "1.0", section = "8.2",
                            citations = {"allow-start-if-complete: Specifies whether this step is allowed to start during job restart, even if the step completed in a previous execution."}
                    ),
                    @SpecRef(
                            version = "1.0", section = "10.8.4",
                            citations = {"if the step is a partitioned step, only the partitions that did not complete previously are restarted."},
                            notes = {"See 3.c."}
                    ),
            },
            issueRefs = {"https://java.net/bugzilla/show_bug.cgi?id=6494"},
            strategy = "In the first job execution, the test fails in one partition and passes in the other two while running through step1. "
                    + "In the second execution, check that only the failed partition in step1 is re-executed; complete step1 and then fail in step2. "
                    + "In the third execution, since step1 has previously completed and allow-start-if-complete=true, we verify that all three "
                    + "partitions in step1 are re-executed.",
            notes = {"The spec doesn't explicitly describe this combination of partitions plus allow-start-if-complete=\"true\", but it seems the only valid interpretation."}
    )
    @Test
    public void testRerunPartitionAndBatchlet() throws Exception {
        Properties origParams = new Properties();
        origParams.setProperty("force.failure", "true");
        origParams.setProperty("force.failure2", "false");

        JobExecution je = jobOp.startJobAndWaitForResult("partitionRerun", origParams);
        long execId = je.getExecutionId();

        checkStepExecId(je, "step1", 2);
        assertWithMessage("Didn't fail as expected", BatchStatus.FAILED, je.getBatchStatus());

        //Now run again, since we failed in one partition on the first run this run should have only that one partition rerun
        Properties restartParams = new Properties();
        restartParams.setProperty("force.failure", "false");
        restartParams.setProperty("force.failure2", "true");
        JobExecution restartje = jobOp.restartJobAndWaitForResult(execId, restartParams);
        long restartExecId = restartje.getExecutionId();

        checkStepExecId(restartje, "step1", 1);
        assertWithMessage("Didn't fail as expected", BatchStatus.FAILED, jobOp.getJobExecution(restartExecId).getBatchStatus());

        //Now a third time where we rerun from a fail in step to and expect allow-start-if-complete='true' variable to take over
        //since the failed partitions already reran.
        Properties restartParams2 = new Properties();
        restartParams2.setProperty("force.failure", "false");
        restartParams2.setProperty("force.failure2", "false");
        JobExecution restartje2 = jobOp.restartJobAndWaitForResult(restartExecId, restartParams2);
        long restartExecId2 = restartje2.getExecutionId();

        assertWithMessage("Didn't complete successfully", BatchStatus.COMPLETED, jobOp.getJobExecution(restartExecId2).getBatchStatus());
        checkStepExecId(restartje2, "step1", 3);
    }

    /**
     * @param je
     * @param stepName
     * @param numPartitionResults
     */
    private void checkStepExecId(JobExecution je, String stepName, int numPartitionResults) {
        List<StepExecution> stepExecs = jobOp.getStepExecutions(je.getExecutionId());

        Long stepExecId = null;
        for (StepExecution se : stepExecs) {
            if (se.getStepName().equals(stepName)) {
                stepExecId = se.getStepExecutionId();
                break;
            }
        }

        if (stepExecId == null) {
            throw new IllegalStateException("Didn't find step1 execution for job execution: " + je.getExecutionId());
        }

        String[] retvals = je.getExitStatus().split(",");
        assertWithMessage("Found different number of segments than expected in exit status string for job execution: " + je.getExecutionId(),
                numPartitionResults, retvals.length);

        for (int i = 0; i < retvals.length; i++) {
            assertWithMessage("Did not return a number/numbers matching the stepExecId", stepExecId.longValue(), Long.parseLong(retvals[i]));
        }
    }

}
