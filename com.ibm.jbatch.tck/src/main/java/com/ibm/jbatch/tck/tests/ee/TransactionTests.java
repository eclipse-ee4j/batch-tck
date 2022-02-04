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
package com.ibm.jbatch.tck.tests.ee;

import static com.ibm.jbatch.tck.utils.AssertionUtils.assertObjEquals;
import static com.ibm.jbatch.tck.utils.AssertionUtils.assertWithMessage;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.batch.runtime.StepExecution;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;
import com.ibm.jbatch.tck.utils.TCKJobExecutionWrapper;

import ee.jakarta.tck.batch.api.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import ee.jakarta.tck.batch.api.EETest;

@Tag("ee")
public class TransactionTests {

    private final static Logger logger = Logger.getLogger(TransactionTests.class.getName());

    private static JobOperatorBridge jobOp;

    @BeforeEach
    public void setUp() throws Exception {
        jobOp = new JobOperatorBridge();
    }

    private void begin(String str) {
        Reporter.log("Begin test method: " + str + "<p>");
    }


    /*
     * @testName: testTranRollbackRetryReadSkipRead
     *
     * @assertion: Test will finish in COMPLETED status, with the onRetryReadException and onSkipItem listener invoked.
     *
     * @test_Strategy:
     * Test that the onRetryReadException listener is invoked when a retryable exception occurs on a read.
     * The transaction will rollback and the chunk will be retried with an item-count of 1 (one)
     * Test that the item is skipped, and onSkipReadItem listener is invoked, when the same exception occurs on the retry
     *
     */
    @EETest
    public void testTranRollbackRetryReadSkipRead() throws Exception {
        String METHOD = "testTranRollbackRetryReadSkipRead";
        begin(METHOD);

        try {
            Integer initNumbers = 10;
            Integer forcedFailCountRead = 8;
            Integer forcedFailCountProcess = 0;
            Integer forcedFailCountWrite = 0;
            Integer dummyDelay = 0;
            Boolean rollback = true;
            Boolean autoCommit = false;

            Properties jobParams = new Properties();

            jobParams.put("jakarta.transaction.global.mode", "true");
            jobParams.put("jakarta.transaction.global.timeout", "20");
            jobParams.put("init.numbers.quantity", initNumbers.toString());
            jobParams.put("forced.fail.count.read", forcedFailCountRead.toString());
            jobParams.put("forced.fail.count.write", forcedFailCountWrite.toString());
            jobParams.put("forced.fail.count.process", forcedFailCountProcess.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());
            jobParams.put("rollback", rollback.toString());
            jobParams.put("auto.commit", autoCommit.toString());

            Reporter.log("Locate job XML file: job_chunk_retryskip_rollback.xml<p>");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_chunk_retryskip_rollback", jobParams);
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testTranRollbackRetryProcessSkipProcess
     *
     * @assertion: Test will finish in COMPLETED status, with the onRetryProcessException and onSkipProcess listener invoked.
     *
     * @test_Strategy:
     * Test that the onRetryProcessException listener is invoked when a retryable exception occurs on a process.
     * The transaction will rollback and the chunk will be retried with an item-count of 1 (one)
     * Test that the item is skipped, and onSkipProcessItem listener is invoked, when the same exception occurs on the retry
     *
     */
    @EETest
    public void testTranRollbackRetryProcessSkipProcess() throws Exception {
        String METHOD = "testTranRollbackRetryProcessSkipProcess";
        begin(METHOD);

        try {
            Integer initNumbers = 10;
            Integer forcedFailCountRead = 0;
            Integer forcedFailCountProcess = 8;
            Integer forcedFailCountWrite = 0;
            Integer dummyDelay = 0;
            Boolean rollback = true;
            Boolean autoCommit = false;

            Properties jobParams = new Properties();

            jobParams.put("jakarta.transaction.global.mode", "true");
            jobParams.put("jakarta.transaction.global.timeout", "20");
            jobParams.put("init.numbers.quantity", initNumbers.toString());
            jobParams.put("forced.fail.count.read", forcedFailCountRead.toString());
            jobParams.put("forced.fail.count.write", forcedFailCountWrite.toString());
            jobParams.put("forced.fail.count.process", forcedFailCountProcess.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());
            jobParams.put("rollback", rollback.toString());
            jobParams.put("auto.commit", autoCommit.toString());

            Reporter.log("Locate job XML file: job_chunk_retryskip_rollback.xml<p>");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_chunk_retryskip_rollback", jobParams);
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testTranRollbackRetryWriteSkipWrite
     *
     * @assertion: Test will finish in COMPLETED status, with the onRetryWriteException and onSkipWriteItem listener invoked.
     *
     * @test_Strategy:
     * Test that the onRetryWriteException listener is invoked when a retryable exception occurs on a write.
     * The transaction will rollback and the chunk will be retried with an item-count of 1 (one)
     * Test that the item is skipped, and onSkipWriteItem listener is invoked, when the same exception occurs on the retry
     *
     */
    @EETest
    public void testTranRollbackRetryWriteSkipWrite() throws Exception {
        String METHOD = "testTranRollbackRetryWriteSkipWrite";
        begin(METHOD);

        try {
            Integer initNumbers = 10;
            Integer forcedFailCountRead = 0;
            Integer forcedFailCountProcess = 0;
            Integer forcedFailCountWrite = 8;
            Integer dummyDelay = 0;
            Boolean rollback = true;
            Boolean autoCommit = false;

            Properties jobParams = new Properties();

            jobParams.put("jakarta.transaction.global.mode", "true");
            jobParams.put("jakarta.transaction.global.timeout", "20");
            jobParams.put("init.numbers.quantity", initNumbers.toString());
            jobParams.put("forced.fail.count.read", forcedFailCountRead.toString());
            jobParams.put("forced.fail.count.write", forcedFailCountWrite.toString());
            jobParams.put("forced.fail.count.process", forcedFailCountProcess.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());
            jobParams.put("rollback", rollback.toString());
            jobParams.put("auto.commit", autoCommit.toString());

            Reporter.log("Locate job XML file: job_chunk_retryskip_rollback.xml<p>");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_chunk_retryskip_rollback", jobParams);
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testGlobalTranNoExceptions
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @EETest
    public void testGlobalTranNoExceptions() throws Exception {
        String METHOD = "testGlobalTranNoExceptions";
        begin(METHOD);

        String DEFAULT_SLEEP_TIME = "0";

        try {
            Integer initInventory = 99;
            Integer forcedFailCount = 0;
            Integer itemCount = 5;
            Integer dummyDelay =
                    Integer.parseInt(System.getProperty("TransactionTests.testGlobalTranNoExceptions.sleep", DEFAULT_SLEEP_TIME));


            Integer expectedInventory = this.calculateGlobalTranExpectedInventory(initInventory, forcedFailCount, itemCount);
            Integer expectedCompletedOrders = this.calculateExpectedCompleteOrders(initInventory, forcedFailCount, itemCount);

            Properties jobParams = new Properties();
            Reporter.log("Create job parameters for execution #1:<p>");
            Reporter.log("jakarta.transaction.global.timeout=300<p>");
            Reporter.log("commit.interval=" + itemCount.toString() + "<p>");
            Reporter.log("init.inventory.quantity=" + initInventory.toString() + "<p>");
            Reporter.log("forced.fail.count=" + forcedFailCount.toString() + "<p>");
            Reporter.log("dummy.delay.seconds=" + dummyDelay.toString() + "<p>");
            Reporter.log("expected.inventory=" + expectedInventory.toString() + "<p>");
            jobParams.put("jakarta.transaction.global.timeout", "300");
            jobParams.put("commit.interval", itemCount.toString());
            jobParams.put("init.inventory.quantity", initInventory.toString());
            jobParams.put("forced.fail.count", forcedFailCount.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());
            jobParams.put("expected.inventory", expectedInventory.toString());


            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_chunk_globaltran", jobParams);

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertObjEquals("Inventory=" + expectedInventory + " InitialCheckpoint=" + null + " OrderCount=" + expectedCompletedOrders, jobExec.getExitStatus());
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }


    /*
     * @testName: testGlobalTranForcedExceptionWithRollback
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @EETest
    public void testGlobalTranForcedExceptionWithRollback() throws Exception {
        String METHOD = "testGlobalTranForcedExceptionWithRollback";
        begin(METHOD);

        String DEFAULT_SLEEP_TIME = "0";

        try {
            Integer initInventory = 99;
            Integer forcedFailCount = 20;
            Integer itemCount = 9;
            Integer dummyDelay =
                    Integer.parseInt(System.getProperty("TransactionTests.testGlobalTranForcedExceptionWithRollback.sleep", DEFAULT_SLEEP_TIME));

            Integer expectedInventory = TransactionTests.calculateGlobalTranExpectedInventory(initInventory, forcedFailCount, itemCount);
            Integer expectedCompletedOrders = this.calculateExpectedCompleteOrders(initInventory, forcedFailCount, itemCount);

            Properties jobParams = new Properties();

            Reporter.log("Create job parameters for execution #1:<p>");
            Reporter.log("jakarta.transaction.global.timeout=300<p>");
            Reporter.log("commit.interval=" + itemCount.toString() + "<p>");
            Reporter.log("init.inventory.quantity=" + initInventory.toString() + "<p>");
            Reporter.log("forced.fail.count=" + forcedFailCount.toString() + "<p>");
            Reporter.log("dummy.delay.seconds=" + dummyDelay.toString() + "<p>");
            Reporter.log("expected.inventory=" + expectedInventory.toString() + "<p>");
            jobParams.put("jakarta.transaction.global.timeout", "300");
            jobParams.put("commit.interval", itemCount.toString());
            jobParams.put("init.inventory.quantity", initInventory.toString());
            jobParams.put("forced.fail.count", forcedFailCount.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());
            jobParams.put("expected.inventory", expectedInventory.toString());

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_chunk_globaltran", jobParams);

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertObjEquals("Inventory=" + expectedInventory + " InitialCheckpoint=" + null + " OrderCount=" + expectedCompletedOrders, jobExec.getExitStatus());
            assertObjEquals(BatchStatus.FAILED, jobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }

    /*
     * @testName: testGlobalTranForcedExceptionCheckpointRestart
     * @assertion: FIXME
     * @test_Strategy: FIXME
     */
    @EETest
    public void testGlobalTranForcedExceptionCheckpointRestart() throws Exception {
        String METHOD = "testGlobalTranForcedExceptionCheckpointRestart";
        begin(METHOD);

        String DEFAULT_SLEEP_TIME = "0";

        try {
            Integer initInventory = 99;
            Integer forcedFailCount = 20;
            Integer itemCount = 9;
            Integer dummyDelay =
                    Integer.parseInt(System.getProperty("TransactionTests.testGlobalTranForcedExceptionCheckpointRestart.sleep", DEFAULT_SLEEP_TIME));

            Integer expectedInventory = TransactionTests.calculateGlobalTranExpectedInventory(initInventory, forcedFailCount, itemCount);
            Integer expectedCompletedOrders = TransactionTests.calculateExpectedCompleteOrders(initInventory, forcedFailCount, itemCount);

            Properties jobParams = new Properties();

            Reporter.log("Create job parameters for execution #1:<p>");
            Reporter.log("jakarta.transaction.global.timeout=300<p>");
            Reporter.log("commit.interval=" + itemCount.toString() + "<p>");
            Reporter.log("init.inventory.quantity=" + initInventory.toString() + "<p>");
            Reporter.log("forced.fail.count=" + forcedFailCount.toString() + "<p>");
            Reporter.log("dummy.delay.seconds=" + dummyDelay.toString() + "<p>");
            Reporter.log("expected.inventory=" + expectedInventory.toString() + "<p>");
            jobParams.put("jakarta.transaction.global.timeout", "300");
            jobParams.put("commit.interval", itemCount.toString());
            jobParams.put("init.inventory.quantity", initInventory.toString());
            jobParams.put("forced.fail.count", forcedFailCount.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());
            jobParams.put("expected.inventory", expectedInventory.toString());


            Reporter.log("Invoke startJobAndWaitForResult<p>");
            TCKJobExecutionWrapper jobExec = jobOp.startJobAndWaitForResult("job_chunk_globaltran", jobParams);
            long jobInstanceId = jobExec.getInstanceId();

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertObjEquals(BatchStatus.FAILED, jobExec.getBatchStatus());
            assertObjEquals("Inventory=" + expectedInventory + " InitialCheckpoint=" + null + " OrderCount=" + expectedCompletedOrders, jobExec.getExitStatus());

            forcedFailCount = 0;
            jobParams.put("forced.fail.count", forcedFailCount.toString());

            expectedInventory = TransactionTests.calculateGlobalTranExpectedInventory(expectedInventory, forcedFailCount, itemCount);
            Integer expectedCompletedOrders2 = TransactionTests.calculateExpectedCompleteOrders(initInventory, forcedFailCount, itemCount);

            Reporter.log("Invoke restartJobAndWaitForResult with id: " + jobInstanceId + "<p>");
            JobExecution restartedJobExec = jobOp.restartJobAndWaitForResult(jobExec.getExecutionId(), jobParams);

            Reporter.log("restarted job JobExecution getBatchStatus()=" + restartedJobExec.getBatchStatus() + "<p>");
            Reporter.log("restarted job JobExecution getExitStatus()=" + restartedJobExec.getExitStatus() + "<p>");
            assertObjEquals("Inventory=" + expectedInventory + " InitialCheckpoint=" + expectedCompletedOrders + " OrderCount=" + expectedCompletedOrders2, restartedJobExec.getExitStatus());
            assertObjEquals(BatchStatus.COMPLETED, restartedJobExec.getBatchStatus());
        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }

    /*
     * @testName: testGlobalTranNoDelayLongTimeout
     * @assertion: Step-level property of 'jakarta.transaction.global.timeout'
     * @Test_Strategy: This test sets a long (180 sec.) checkpoint timeout explicitly in the JSL, and it does not
     *                 use any delay or sleep, so it just confirms that the timeout doesn't hit and the chunk completes
     *                 normally.
     */
    @EETest
    public void testGlobalTranNoDelayLongTimeout() throws Exception {
        String METHOD = "testGlobalTranNoDelayLongTimeout";
        begin(METHOD);

        String DEFAULT_SLEEP_TIME = "0";

        try {

            Integer initInventory = 99;
            // set to zero
            Integer forcedFailCount = 0;
            Integer itemCount = 9;
            // set to zero
            Integer dummyDelay =
                    Integer.parseInt(System.getProperty("TransactionTests.testGlobalTranNoDelayLongTimeout.sleep", DEFAULT_SLEEP_TIME));

            Integer expectedInventory = TransactionTests.calculateGlobalTranExpectedInventory(initInventory, forcedFailCount, itemCount);
            Integer expectedCompletedOrders = TransactionTests.calculateExpectedCompleteOrders(initInventory, forcedFailCount, itemCount);

            Properties jobParams = new Properties();

            Reporter.log("Create job parameters for execution #1:<p>");
            Reporter.log("commit.interval=" + itemCount.toString() + "<p>");
            Reporter.log("init.inventory.quantity=" + initInventory.toString() + "<p>");
            Reporter.log("forced.fail.count=" + forcedFailCount.toString() + "<p>");
            Reporter.log("dummy.delay.seconds=" + dummyDelay.toString() + "<p>");
            Reporter.log("expected.inventory=" + expectedInventory.toString() + "<p>");
            jobParams.put("commit.interval", itemCount.toString());
            jobParams.put("init.inventory.quantity", initInventory.toString());
            jobParams.put("forced.fail.count", forcedFailCount.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());
            jobParams.put("expected.inventory", expectedInventory.toString());

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            TCKJobExecutionWrapper jobExec = jobOp.startJobAndWaitForResult("job_chunk_globaltran_default", jobParams);

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertObjEquals("Inventory=" + expectedInventory + " InitialCheckpoint=" + null + " OrderCount=" + expectedCompletedOrders, jobExec.getExitStatus());
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());

        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }

    /*
     *  (this test name is now a misnomer since we changed the test logic to exclude the "short timeout")
     *
     * @testName: testGlobalTranLongDelayMixOfLongTimeoutStepsAndShortTimeoutSteps
     * @assertion: Step-level property of 'jakarta.transaction.global.timeout', including defaults.  Also shows that
     *             job-level property is ignored.
     * @Test_Strategy: This test uses three steps that are basically the same, reading and writing from the same tables
     *                 (along with some helper steps in between to init the tables).
     *                 Two of the steps are configured with long timeouts, and one with a short timeout.
     *                 The long timeouts involve variations:
     *                  - step 2 : <property name="jakarta.transaction.global.timeout" value="0" />
     *                        (shows that '0' means indefinite timeout)
     *                  - step 3 : No property specified
     *                        (shows that the default is 180 seconds).
     *
     *                 The long timeouts are longer than the "long" delay, so these steps complete successfully.
     *
     *                 Obviously this doesn't prove that the exact default timeout value is 180 seconds (for step 3),
     *                 or that the value of '0' means unlimited.  We don't try to prove that conclusively but just
     *                 to hint at it and possibly catch an implementation doing something completely off-base.
     */
    @EETest
    public void testGlobalTranLongDelayMixOfLongTimeoutStepsAndShortTimeoutSteps() throws Exception {
        String METHOD = "testGlobalTranLongDelayMixOfLongTimeoutStepsAndShortTimeoutSteps";
        begin(METHOD);

        String DEFAULT_SLEEP_TIME = "10000";

        try {
            Integer initInventory = 99;
            Integer forcedFailCount = 15;
            Integer itemCount = 9;
            Integer dummyDelay =
                    Integer.parseInt(System.getProperty("TransactionTests.testGlobalTranLongDelayMixOfLongTimeoutStepsAndShortTimeoutSteps.sleep", DEFAULT_SLEEP_TIME));

            Properties jobParams = new Properties();

            Reporter.log("Create job parameters for execution #1:<p>");
            Reporter.log("commit.interval=" + itemCount.toString() + "<p>");
            Reporter.log("init.inventory.quantity=" + initInventory.toString() + "<p>");
            Reporter.log("forced.fail.count=" + forcedFailCount.toString() + "<p>");
            Reporter.log("dummy.delay.seconds=" + dummyDelay.toString() + "<p>");
            jobParams.put("commit.interval", itemCount.toString());
            jobParams.put("init.inventory.quantity", initInventory.toString());
            jobParams.put("forced.fail.count", forcedFailCount.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            TCKJobExecutionWrapper jobExec = jobOp.startJobAndWaitForResult("job_chunk_globaltran_multiple_steps", jobParams);

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
            // With test refactoring all orders will be processed, and all inventory depleted.
            assertObjEquals("Inventory=0 InitialCheckpoint=null OrderCount=" + initInventory, jobExec.getExitStatus());

            List<StepExecution> stepExecutions = jobOp.getStepExecutions(jobExec.getExecutionId());

            boolean seen2 = false;
            boolean seen3 = false;
            for (StepExecution s : stepExecutions) {
                if (s.getStepName().equals("step2")) {
                    assertObjEquals(BatchStatus.COMPLETED, s.getBatchStatus());
                    seen2 = true;
                } else if (s.getStepName().equals("step3")) {
                    assertObjEquals(BatchStatus.COMPLETED, s.getBatchStatus());
                    seen3 = true;
                }
            }
            assertWithMessage("Step2 execution seen", true, seen2);
            assertWithMessage("Step3 execution seen", true, seen3);

        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }

    /*
     * @testName: testGlobalTranLongDelayMixOfLongTimeoutStepsAndShortTimeoutStepsCustomCheckpointAlgorithm
     * @assertion: Tests the CheckpointAlgorithm interface, with three
     *             job-level property is ignored.
     * @Test_Strategy: This test uses three steps that are basically the same, reading and writing from the same tables
     *                 (along with some helper steps in between to init the tables).
     *
     *                 All three use a custom checkpoint algorithm, which are identical except for their
     *                 behavior with respect to timeout.
     *
     *                 step2 : checkpointTimeout - Don't override, pickup AbstractCheckpointAlgorithm's returning '0' =
     *                    unlimited.
     *                 step3 : checkpointTimeout - Return a long value
     *
     *                 The long timeouts are longer than the "long" delay, so steps 2, 3 complete successfully.
     */
    @EETest
    public void testGlobalTranLongDelayMixOfLongTimeoutStepsAndShortTimeoutStepsCustomCheckpointAlgorithm() throws Exception {
        String METHOD = "testGlobalTranLongDelayMixOfLongTimeoutStepsAndShortTimeoutStepsCustomCheckpointAlgorithm";
        begin(METHOD);

        String DEFAULT_SLEEP_TIME = "10000";

        try {
            Integer initInventory = 99;
            Integer forcedFailCount = 15;
            Integer itemCount = 9;
            Integer dummyDelay =
                    Integer.parseInt(System.getProperty("TransactionTests.testGlobalTranLongDelayMixOfLongTimeoutStepsAndShortTimeoutStepsCustomCheckpointAlgorithm.sleep", DEFAULT_SLEEP_TIME));

            Properties jobParams = new Properties();

            Reporter.log("Create job parameters for execution #1:<p>");
            Reporter.log("commit.interval=" + itemCount.toString() + "<p>");
            Reporter.log("init.inventory.quantity=" + initInventory.toString() + "<p>");
            Reporter.log("forced.fail.count=" + forcedFailCount.toString() + "<p>");
            Reporter.log("dummy.delay.seconds=" + dummyDelay.toString() + "<p>");
            jobParams.put("commit.interval", itemCount.toString());
            jobParams.put("init.inventory.quantity", initInventory.toString());
            jobParams.put("forced.fail.count", forcedFailCount.toString());
            jobParams.put("dummy.delay.seconds", dummyDelay.toString());

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            TCKJobExecutionWrapper jobExec = jobOp.startJobAndWaitForResult("job_chunk_globaltran_multiple_steps-customCA", jobParams);

            Reporter.log("execution #1 JobExecution getBatchStatus()=" + jobExec.getBatchStatus() + "<p>");
            Reporter.log("execution #1 JobExecution getExitStatus()=" + jobExec.getExitStatus() + "<p>");
            assertObjEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus());
            assertObjEquals("Inventory=0 InitialCheckpoint=null OrderCount=" + initInventory, jobExec.getExitStatus());

            List<StepExecution> stepExecutions = jobOp.getStepExecutions(jobExec.getExecutionId());

            boolean seen2 = false;
            boolean seen3 = false;
            for (StepExecution s : stepExecutions) {
                if (s.getStepName().equals("step2")) {
                    assertObjEquals(BatchStatus.COMPLETED, s.getBatchStatus());
                    seen2 = true;
                } else if (s.getStepName().equals("step3")) {
                    assertObjEquals(BatchStatus.COMPLETED, s.getBatchStatus());
                    seen3 = true;
                }
            }
            assertWithMessage("Step2 execution seen", true, seen2);
            assertWithMessage("Step3 execution seen", true, seen3);

        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }


    /**
     * Calculates how many items should be left in the inventory table after all commits and rollbacks.
     */
    private static int calculateGlobalTranExpectedInventory(int initInventory, int forcedFailCount, int commitInterval) {
        int expectedResult;

        if (forcedFailCount <= 0) {
            expectedResult = 0;
        } else {
            expectedResult = (initInventory - forcedFailCount) + (forcedFailCount % commitInterval);
        }

        return expectedResult;
    }


    /**
     * Calculates how many orders should  in the orders table after all commits and rollbacks.
     */
    private static int calculateExpectedCompleteOrders(int initInventory, int forcedFailCount, int commitInterval) {
        int expectedResult;
        if (forcedFailCount <= 0) {
            expectedResult = initInventory;
        } else {
            expectedResult = (forcedFailCount / commitInterval) * commitInterval; //integer arithmetic, so we drop the remainder
        }


        return expectedResult;
    }

    private static void handleException(String methodName, Exception e) throws Exception {
        Reporter.log("Caught exception: " + e.getMessage() + "<p>");
        Reporter.log(methodName + " failed<p>");
        throw e;
    }

}
