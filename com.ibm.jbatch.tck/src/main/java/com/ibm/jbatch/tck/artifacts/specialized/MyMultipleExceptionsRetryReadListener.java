/**
 * Copyright 2013, 2020 International Business Machines Corp. and others
 * <p>
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.ibm.jbatch.tck.artifacts.specialized;

import java.util.logging.Logger;

import jakarta.batch.api.chunk.listener.RetryReadListener;
import jakarta.batch.runtime.context.JobContext;
import jakarta.batch.runtime.context.StepContext;
import jakarta.inject.Inject;

import ee.jakarta.tck.batch.util.Reporter;

import com.ibm.jbatch.tck.artifacts.reusable.MyParentException;

@jakarta.inject.Named("myMultipleExceptionsRetryReadListener")
public class MyMultipleExceptionsRetryReadListener implements RetryReadListener {

    @Inject
    JobContext jobCtx;

    @Inject
    StepContext stepCtx;

    private final static String sourceClass = MySkipReadListener.class.getName();
    private final static Logger logger = Logger.getLogger(sourceClass);

    public static final String GOOD_EXIT_STATUS = "MyMultipleExceptionsRetryReadListener: GOOD STATUS";
    public static final String BAD_EXIT_STATUS = "MyMultipleExceptionsRetryReadListener: BAD STATUS";


    @Override
    public void onRetryReadException(Exception ex) throws Exception {
        Reporter.log("In onSkipReadItem" + ex + "<p>");

        if (ex instanceof MyParentException) {
            Reporter.log("SKIPLISTENER: onSkipReadItem, exception is an instance of: MyParentException<p>");
            jobCtx.setExitStatus(GOOD_EXIT_STATUS);
        } else {
            Reporter.log("SKIPLISTENER: onSkipReadItem, exception is NOT an instance of: MyParentException<p>");
            jobCtx.setExitStatus(BAD_EXIT_STATUS);
        }
    }

}
