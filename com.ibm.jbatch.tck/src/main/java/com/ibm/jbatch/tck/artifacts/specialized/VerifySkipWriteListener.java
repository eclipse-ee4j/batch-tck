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
package com.ibm.jbatch.tck.artifacts.specialized;

import java.util.List;
import java.util.logging.Logger;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.chunk.listener.SkipWriteListener;
import jakarta.batch.runtime.context.JobContext;
import jakarta.batch.runtime.context.StepContext;
import jakarta.inject.Inject;

import com.ibm.jbatch.tck.utils.Reporter;

import com.ibm.jbatch.tck.artifacts.chunktypes.ReadRecord;
import com.ibm.jbatch.tck.artifacts.reusable.MyParentException;

@jakarta.inject.Named("verifySkipWriteListener")
public class VerifySkipWriteListener implements SkipWriteListener {

    @Inject
    JobContext jobCtx;

    @Inject
    StepContext stepCtx;

    @Inject
    @BatchProperty(name = "test.itemcount") //The test should specify this value to be the item-count of the chunk
            String numberOfExpectedItems;

    private final static String sourceClass = VerifySkipWriteListener.class.getName();
    private final static Logger logger = Logger.getLogger(sourceClass);

    public static final String GOOD_EXIT_STATUS = "VerifySkipWriteListener: GOOD STATUS, GOOD OBJS PASSED IN";
    public static final String BAD_EXIT_STATUS = "VerifySkipWriteListener: BAD STATUS";

    //These values are hardcoded based on when the writer fails. A change to the job or writer will result in a change of these values.
    //these values can be easily retreived by running the test without the check, and viewing the reporter output from line 75
    private static int[] expectedValues = {16, 17, 18, 25, 26, 27, 34, 35, 36};

    //to keep track of which item is expected next
    private static int indexOfExpectedValue = 0;

    @Override
    public void onSkipWriteItem(List items, Exception e) throws Exception {
        Reporter.log("In onSkipWriteItem()" + e + "<p>");
        ReadRecord input = null;
        boolean inputOK = true;
        int numberOfNonNullItemsFound = 0; //count the number of non-null items found in the list

        for (Object obj : items) {   //switched around to fail at NULL items
            input = (ReadRecord) obj;

            if (obj == null) {
                logger.finer("In onSkipProcessItem(), NULL object in items list<p>");
                inputOK = false;
            } else {
                logger.finer("In onSkipProcessItem(), item count = " + input.getCount());
                numberOfNonNullItemsFound++;

                if (input.getCount() != expectedValues[indexOfExpectedValue]) {
                    inputOK = false;
                    logger.finer("In onSkipProcessItem(), wrong item value. Expected " + expectedValues[indexOfExpectedValue] + ", found " + input.getCount() + "<p>");
                }
            }
            indexOfExpectedValue++;
        }

        if (numberOfExpectedItems != null && Integer.parseInt(numberOfExpectedItems) != numberOfNonNullItemsFound) { //check if # of items found matches # in chunk
            inputOK = false;
            logger.finer("Wrong number of items. Expected " + numberOfExpectedItems + ", found " + numberOfNonNullItemsFound + "<p>");
        }

        if (e instanceof MyParentException && inputOK) {
            Reporter.log("VERIFYSKIPLISTENER: onSkipWriteItem, exception is an instance of: MyParentException<p>");
            jobCtx.setExitStatus(GOOD_EXIT_STATUS);
        } else {
            Reporter.log("VERIFYSKIPLISTENER: onSkipWriteItem, exception is NOT an instance of: MyParentException<p>");
            jobCtx.setExitStatus(BAD_EXIT_STATUS);
            throw new Exception(); //fail immediately, don't wait for assertion. next iteration of this class could overwrite status.
        }
    }
}
