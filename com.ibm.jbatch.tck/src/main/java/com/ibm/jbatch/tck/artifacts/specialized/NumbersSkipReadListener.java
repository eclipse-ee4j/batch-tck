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
package com.ibm.jbatch.tck.artifacts.specialized;

import java.util.Properties;
import java.util.logging.Logger;

import jakarta.batch.api.chunk.listener.SkipReadListener;
import jakarta.batch.runtime.context.StepContext;
import jakarta.inject.Inject;

import ee.jakarta.tck.batch.util.Reporter;

import com.ibm.jbatch.tck.artifacts.reusable.MyParentException;

@jakarta.inject.Named("numbersSkipReadListener")
public class NumbersSkipReadListener implements SkipReadListener {

    @Inject
    StepContext stepCtx;

    private final static String sourceClass = NumbersSkipReadListener.class.getName();
    private final static Logger logger = Logger.getLogger(sourceClass);

    @Override
    public void onSkipReadItem(Exception e) {
        Reporter.log("In onSkipReadItem" + e + "<p>");

        ((Properties) stepCtx.getTransientUserData()).setProperty("skip.read.item.invoked", "true");
        if (e instanceof MyParentException) {
            ((Properties) stepCtx.getTransientUserData()).setProperty("skip.read.item.match", "true");
        } else {
            ((Properties) stepCtx.getTransientUserData()).setProperty("skip.read.item.match", "false");
        }
    }
}
