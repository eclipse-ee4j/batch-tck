/*
 * Copyright 2014, 2020 International Business Machines Corp. and others
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

import java.util.Properties;

import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;

import com.ibm.jbatch.tck.utils.Reporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ibm.jbatch.tck.ann.*;
import com.ibm.jbatch.tck.utils.BaseJUnit5Test;
import com.ibm.jbatch.tck.utils.JobOperatorBridge;


public class ListenerOnErrorTests extends BaseJUnit5Test {
    private static JobOperatorBridge jobOp = null;

    @BeforeEach
    public void setUp() throws Exception {
        jobOp = new JobOperatorBridge();
    }

    @TCKTest(
            versions = {"1.1.WORKING"},
            assertions = {"ItemWriteListener#onWriteError is passed the list of items that were being written by ItemWriter#writeItems when exception was thrown."},
            specRefs = {
                    @SpecRef(
                            version = "1.0", section = "9.1.1.3",
                            citations = {"@param items specifies the list of items to write. This may be an empty list (e.g. if all the items have been filtered out by the ItemProcessor)."},
                            notes = {"API for ItemWriter"}
                    ),
                    @SpecRef(
                            version = "1.0", section = "9.2.6",
                            citations = {"The onWriteError method receives control after an item writer writeItems throws an exception. The method receives the list of items sent to the item writer as input."},
                            notes = {"API for ItemWriteListener"}
                    ),
            },
            apiRefs = {
                    @APIRef(className = "jakarta.batch.api.chunk.ItemWriter", methodNames = {"writeItems"}),
                    @APIRef(className = "jakarta.batch.api.chunk.listener.ItemWriteListener", methodNames = {"onWriteError"}),
            },
            issueRefs = {"https://java.net/bugzilla/show_bug.cgi?id=5431"},
            strategy = "Intentionally fail writer at a specific record number. Take the items passed as input parameter to onWriteError, "
                    + "and set a String representation of this List as the job's exit status.  Check that this matches the expected value "
                    + "based on the chunk size, input data, and failing record number. Also check that the job fails."
    )
    @Test
    public void testOnWriteErrorItems() throws Exception {
        String GOOD_EXIT_STATUS = new String("[10, 12, 14, 16, 18]");

        Reporter.log("Create job parameters for execution<p>");
        Properties jobParams = new Properties();

        Reporter.log("write.fail=true<p>");
        jobParams.put("write.fail", "true");

        Reporter.log("Invoke startJobAndWaitForResult for execution<p>");
        JobExecution je = jobOp.startJobAndWaitForResult("listenerOnError", jobParams);

        Reporter.log("JobExecution getBatchStatus()=" + je.getBatchStatus() + "<p>");
        Reporter.log("JobExecution getExitStatus()=" + je.getExitStatus() + "<p>");
        assertWithMessage("Testing execution for the WRITE LISTENER", BatchStatus.FAILED, je.getBatchStatus());
        assertWithMessage("Testing execution for the WRITE LISTENER", GOOD_EXIT_STATUS, je.getExitStatus());
    }

    @TCKTest(
            versions = {"1.1.WORKING"},
            assertions = {"ItemProcessListener#onProcessError is passed the item that ItemProcessor#processItem throws an exception for."},
            specRefs = {
                    @SpecRef(
                            version = "1.0", section = "9.1.1.2",
                            citations = {"The processItem method is part of a chunk step. It accepts an input item from an item reader and returns an item that gets passed onto the item writer."},
                            notes = {"API for ItemProcessor"}
                    ),
                    @SpecRef(
                            version = "1.0", section = "9.2.5",
                            citations = {"The onProcessError method receives control after an item processor processItem throws an exception. The method receives the item sent to the item processor as input."},
                            notes = {"API for ItemProcessListener"}
                    ),
            },
            apiRefs = {
                    @APIRef(className = "jakarta.batch.api.chunk.ItemProcessor", methodNames = {"processItem"}),
                    @APIRef(className = "jakarta.batch.api.chunk.listener.ItemProcessListener", methodNames = {"onProcessError"}),
            },
            issueRefs = {"https://java.net/bugzilla/show_bug.cgi?id=5431"},
            strategy = "Intentionally fail processor at a specific record number. Take the item passed as input parameter to onProcessError, "
                    + "and set a String representation of this item as the job's exit status. Check that this matches the expected value "
                    + "based on the input data and the failing record number. Also check that the job fails."
    )
    @Test
    public void testOnProcessErrorItems() throws Exception {
        String GOOD_EXIT_STATUS = new String("8");
        Reporter.log("Create job parameters for execution:<p>");
        Properties jobParams = new Properties();

        Reporter.log("process.fail=true<p>");
        jobParams.put("process.fail", "true");

        Reporter.log("Invoke startJobAndWaitForResult for execution<p>");
        JobExecution je = jobOp.startJobAndWaitForResult("listenerOnError", jobParams);

        Reporter.log("JobExecution getBatchStatus()=" + je.getBatchStatus() + "<p>");
        Reporter.log("JobExecution getExitStatus()=" + je.getExitStatus() + "<p>");
        assertWithMessage("Testing execution for the PROCESS LISTENER", BatchStatus.FAILED, je.getBatchStatus());
        assertWithMessage("Testing execution for the PROCESS LISTENER", GOOD_EXIT_STATUS, je.getExitStatus());
    }
}
