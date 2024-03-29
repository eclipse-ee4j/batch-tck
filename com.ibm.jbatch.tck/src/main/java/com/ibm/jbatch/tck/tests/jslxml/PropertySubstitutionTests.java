/*
 * Copyright 2012, 2021 International Business Machines Corp. and others
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
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ibm.jbatch.tck.utils.BaseJUnit5Test;

import java.io.File;
import java.util.Properties;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.JobOperatorBridge;

import ee.jakarta.tck.batch.util.Reporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PropertySubstitutionTests extends BaseJUnit5Test {

    private static JobOperatorBridge jobOp;


    @BeforeEach
    public void setUp() throws Exception {
        jobOp = new JobOperatorBridge();
    }

    @AfterEach
    public void cleanUp() throws Exception {
        // Clear this property for next test
        System.clearProperty("property.junit.result");
    }

    /*
     * @testName: testBatchArtifactPropertyInjection
     *
     * @assertion: Section 6.3.1, The @BatchProperty may be used on a class
     * field for any class identified as a batch programming model artifact
     *
     * @test_Strategy: Include a property element on a Batchlet artifact and
     * verify the java value of the String is set to the same value as in the
     * job xml.
     */
    @Test
    public void testBatchArtifactPropertyInjection() throws Exception {
        String METHOD = "testBatchArtifactPropertyInjection";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Set system property: property.junit.propName=myProperty1<p>");
            System.setProperty("property.junit.propName", "myProperty1");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2");

            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");
            assertObjEquals("value1", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }


    /*
     * @testName: testInitializedPropertyIsOverwritten
     *
     * @assertion: The java initialized value is overwritten with the required
     * injected value.
     *
     * @test_Strategy: Include a property element on a Batchlet artifact and
     * verify the java value of the String is set to the same value as in the
     * job xml even if the Java field is initialized.
     */
    @Test
    public void testInitializedPropertyIsOverwritten() throws Exception {

        String METHOD = "testInitializedPropertyIsOverwritten";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Set system property: property.junit.propName=myProperty2<p>");
            System.setProperty("property.junit.propName", "myProperty2");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2");

            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");
            assertObjEquals("value2", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testPropertyWithJobParameter
     *
     * @assertion: Job Parameters submitted on start are substituted in the job
     * XML using the following syntax, #{jobParameters['<property-name>']}
     *
     * @test_Strategy: Submit job parameters through JobOperator.start(String,
     * Properties) and verify that xml string is substituted correctly by
     * injecting the property into a batch artifact
     */
    @Test
    public void testPropertyWithJobParameter() throws Exception {

        String METHOD = "testPropertyWithJobParameter";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Create job parameters for execution #1:<p>");
            Properties jobParameters = new Properties();
            String expectedResult = "mySubmittedValue";
            Reporter.log("mySubmittedPropr=" + expectedResult + "<p>");
            jobParameters.setProperty("mySubmittedProp", expectedResult);

            Reporter.log("Set system property: property.junit.propName=mySubmittedProp<p>");
            System.setProperty("property.junit.propName", "mySubmittedProp");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2", jobParameters);

            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");
            assertObjEquals(expectedResult, result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }


    /*
     * @testName: testDefaultPropertyName
     *
     * @assertion: If a name value is not supplied on the @BatchProperty
     * qualifier the property name defaults to the java field name.
     *
     * @test_Strategy: Supply an xml property element with the name matching the
     * Java field name and verify that the java field value injected matches the
     * value provided through the job xml
     */
    @Test
    public void testDefaultPropertyName() throws Exception {

        String METHOD = "testDefaultPropertyName";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Set system property:property.junit.propName=property4<p>");
            System.setProperty("property.junit.propName", "property4");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2");

            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");
            assertObjEquals("value4", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testGivenPropertyName
     *
     * @assertion: The name provided on the @BatchProperty annotation maps to the job xml name of the property element
     *
     * @test_Strategy: Supply an xml property element with the name matching the
     * @BatchProperty name and verify that the java field value injected matches the
     * value provided through the job xml.
     */
    @Test
    public void testGivenPropertyName() throws Exception {

        String METHOD = "testGivenPropertyName";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Set system property:property.junit.propName=myProperty4<p>");
            System.setProperty("property.junit.propName", "myProperty4");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2");

            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");
            assertObjEquals("value4", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testPropertyInnerScopePrecedence
     *
     * @assertion: A batch artifacts properties overrides any parent properties defined in
     * an outer XML scope. Child properties always override parent properties with the same name.
     *
     * @test_Strategy: Issue a job with a step level property and batchlet property with the same name.
     * Verify that the injected property value is from the artifact level property.
     */
    @Test
    public void testPropertyInnerScopePrecedence() throws Exception {

        String METHOD = "testPropertyInnerScopePrecedence";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Set system property:property.junit.propName=batchletProp<p>");
            System.setProperty("property.junit.propName", "batchletProp");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2");

            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");
            assertObjEquals("batchletPropValue", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testPropertyQuestionMarkSimple
     *
     * @assertion: Section 5.7, The ?:...; syntax is honored for property
     * defaulting, if a property cannot be resolved.
     *
     * @test_Strategy: A jobParameter property is used in the XML which is never passed
     * in programmatically resulting in an unresolved property. The property then defaults
     * to the default value expression which is verified through it's Java value in the
     * batctlet artifact.
     */
    @Test
    public void testPropertyQuestionMarkSimple() throws Exception {

        String METHOD = "testPropertyQuestionMarkSimple";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Set system property:property.junit.propName=defaultPropName1<p>");
            System.setProperty("property.junit.propName", "defaultPropName1");

            Reporter.log("Set system property:file.name.junit=myfile1.txt<p>");
            System.setProperty("file.name.junit", "myfile1.txt");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2");

            // String result = jobExec.getStatus();
            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");
            assertObjEquals("myfile1.txt", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testPropertyQuestionMarkComplex
     *
     * @assertion: Section 5.7, Accord to the spec grammar multiple properties can each have their own default
     * values and also be concatenated with string literals.
     *
     * @test_Strategy: Two jobParameter properties are used in the XML which are never passed
     * in programmatically resulting in an unresolved property. The properties then default
     * to the default value expression.  This is verified through the injected Java value in the
     * batctlet artifact.
     */
    @Test
    public void testPropertyQuestionMarkComplex() throws Exception {

        String METHOD = "testPropertyQuestionMarkComplex";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Set system property:property.junit.propName=defaultPropName2<p>");
            System.setProperty("property.junit.propName", "defaultPropName2");

            Reporter.log("Set system property:file.name.junit=myfile2.txt<p>");
            System.setProperty("file.name.junit", "myfile2");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2");

            // String result = jobExec.getStatus();
            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");

            assertObjEquals(File.separator + "myfile2.txt", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testPropertyWithConcatenation
     *
     * @assertion: Section 5.7, Properties can be concatenated with string literals.
     *
     * @test_Strategy: Supply an xml property element with the name matching the
     * @BatchProperty name and verify that the java field value injected matches the
     * value provided through the job xml.
     */
    @Test
    public void testPropertyWithConcatenation() throws Exception {

        String METHOD = "testPropertyWithConcatenation";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Create job parameters for execution #1:<p>");
            Properties jobParameters = new Properties();
            Reporter.log("myFilename=testfile1<p>");
            jobParameters.setProperty("myFilename", "testfile1");

            Reporter.log("Set system property:file.name.junit=myConcatProp<p>");
            System.setProperty("property.junit.propName", "myConcatProp");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2", jobParameters);

            String result = System.getProperty("property.junit.result");
            Reporter.log("Test result: " + result + "<p>");
            assertObjEquals("testfile1.txt", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }
    }

    /*
     * @testName: testJavaSystemProperty
     *
     * @assertion: Section 5.7, Java system properties can be substituted in the XML using
     * the following systax#{systemProperties['some.property']}, and properties can be
     * concatenated.
     *
     * @test_Strategy: Supply an xml property element with the name matching the
     * @BatchProperty name and verify that the java field value injected matches the
     * value provided through the job xml. The "file.separator" system property is used
     * in this test.
     *
     */
    @Test
    public void testJavaSystemProperty() throws Exception {

        String METHOD = "testJavaSystemProperty";

        try {
            Reporter.log("Locate job XML file: job_properties2.xml<p>");

            Reporter.log("Create job parameters for execution #1:<p>");
            Properties jobParameters = new Properties();
            Reporter.log("myFilename=testfile2<p>");
            jobParameters.setProperty("myFilename", "testfile2");

            Reporter.log("Set system property:file.name.junit=myJavaSystemProp<p>");
            System.setProperty("property.junit.propName", "myJavaSystemProp");

            Reporter.log("Invoke startJobAndWaitForResult<p>");
            JobExecution jobExec = jobOp.startJobAndWaitForResult("job_properties2", jobParameters);
            String result = System.getProperty("property.junit.result");

            String pathSep = System.getProperty("file.separator");

            Reporter.log("Test result: " + pathSep + "test" + pathSep + "testfile2.txt<p>");
            assertObjEquals(pathSep + "test" + pathSep + "testfile2.txt", result);
        } catch (Exception e) {
            handleException(METHOD, e);
        }

    }

    
    /*
     * @testName: testCDIBatchPropsNonString
     *
     * @assertion: Section 9.3.3. Conversion from strings to non-String types
     * 
     * @test_Strategy: Supply job parameters of different types with values matching 
     * expected values hard-coded within the test job batchlet.   Perform validation 
     * within the batchlet that these parameter values get mapped onto corresponding
     * batch properties of (non-String) primitive wrapper types:  Integer, Double, etc.
     * injected into the batchlet via:
     * 
     *    @Inject @BatchProperty Integer
     *    
     * etc.
     */
    @ParameterizedTest
    @ValueSource(strings = {"CDIDependentScopedBatchletPropsNonString", "dependentScopedBatchletPropsNonString", "com.ibm.jbatch.tck.artifacts.cdi.DependentScopedBatchletPropsNonString"})
    public void testCDIBatchPropsNonString(String refName) throws Exception {

        String METHOD = "testCDIBatchPropsNonString";

        try {
            
            Properties jobParams = new Properties();
            jobParams.setProperty("refName", refName);
            jobParams.setProperty("stringProp", "HappyBatchProperties");
            jobParams.setProperty("booleanProp1", "true");
            jobParams.setProperty("booleanProp2", "Nope");
            jobParams.setProperty("doubleProp1", "234.432");
            jobParams.setProperty("doubleProp2", "123.321");
            jobParams.setProperty("floatProp1", Float.toString(11234.432F));
            jobParams.setProperty("floatProp2", Float.toString(11123.321F));
            jobParams.setProperty("intProp1", "7777");
            jobParams.setProperty("intProp2", "8888");
            jobParams.setProperty("longProp1", Long.toString(1234567890123L));
            jobParams.setProperty("longProp2", Long.toString(12345678901234L));
            jobParams.setProperty("shortProp1", "333");
            jobParams.setProperty("shortProp2", "444");
            Reporter.log("starting job with refName = " + refName);
            JobExecution jobExec = jobOp.startJobAndWaitForResult("batch_props_non_string", jobParams);
            Reporter.log("Job Status = " + jobExec.getBatchStatus());
            Reporter.log("job ended with exit status = " + jobExec.getExitStatus());
            assertEquals(BatchStatus.COMPLETED, jobExec.getBatchStatus(), "Job didn't complete successfully");
            Reporter.log("GOOD result");
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
