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

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StepLevelPropertiesTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp = null;

    private int PROPERTIES_COUNT = 3;

    private String FOO_VALUE = "bar";

    /**
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testStepLevelPropertiesCount
     * @assertion: Section 5.2.3 Step Level Properties
     * @test_Strategy: set a list of properties to the step should add them to the step context properties
     */
    @Test
    public void testStepLevelPropertiesCount() throws Exception {

        String METHOD = "testStepLevelPropertiesCount";
        String SHOULD_BE_UNAVAILABLE_PROP_PREFIX = "com.ibm.jbatch.tck.tests.jslxml.StepLevelPropertiesTests";

        Properties jobParams = new Properties();
        jobParams.put(SHOULD_BE_UNAVAILABLE_PROP_PREFIX + ".parm1", "should.not.appear.in.step.context.properties");

        try {

            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("step_level_properties_count");

            Reporter.log("Job Status = " + jobExec.getBatchStatus());
            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
            assertWithMessage("Job completed", "VERY GOOD INVOCATION", jobExec.getExitStatus());
            Reporter.log("job completed");
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /**
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @testName: testStepLevelPropertiesPropertyValue
     * @assertion: Section 5.2.3 Step Level Properties
     * @test_Strategy: set a step property value should equal value set on step context property
     */
    @Test
    public void testStepLevelPropertiesPropertyValue() throws Exception {

        String METHOD = "testStepLevelPropertiesPropertyValue";

        try {

            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("step_level_properties_value");

            Reporter.log("Job Status = " + jobExec.getBatchStatus());
            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
            Reporter.log("job completed");

            assertWithMessage("Property value", FOO_VALUE, jobExec.getExitStatus());

            Reporter.log("Job batchlet return code is the step property foo value " + FOO_VALUE);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /**
     * @throws InterruptedException
     * @testName: testStepLevelPropertiesShouldNotBeAvailableThroughJobContext
     * @assertion: Section 5.2.3 Step Level Properties
     * @test_Strategy: set a step property value should not be available to job context
     */
    @Test
    public void testStepLevelPropertiesShouldNotBeAvailableThroughJobContext() throws Exception {

        String METHOD = "testStepLevelPropertiesShouldNotBeAvailableThroughJobContext";

        try {

            Reporter.log("starting job");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("step_level_properties_scope");

            Reporter.log("Job Status = " + jobExec.getBatchStatus());
            assertWithMessage("Job completed", BatchStatus.COMPLETED, jobExec.getBatchStatus());
            Reporter.log("job completed");
            assertWithMessage("Step Level Property is not available through job context", BatchStatus.COMPLETED.name(), jobExec.getExitStatus());
            Reporter.log("Job batchlet return code is the step.property read through job context (expected value=COMPLETED) " + jobExec.getExitStatus());
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
