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
package com.ibm.jbatch.tck.artifacts.chunkartifacts;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.chunk.AbstractItemWriter;
import jakarta.batch.runtime.context.StepContext;
import jakarta.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import ee.jakarta.tck.batch.util.Reporter;

import com.ibm.jbatch.tck.artifacts.chunktypes.NumbersRecord;
import com.ibm.jbatch.tck.artifacts.reusable.MyParentException;


@jakarta.inject.Named("retryWriter")
public class RetryWriter extends AbstractItemWriter {


    protected DataSource dataSource = null;

    @Inject
    StepContext stepCtx;


    @Inject
    @BatchProperty(name = "forced.fail.count.write")
    String forcedFailCountProp;

    @Inject
    @BatchProperty(name = "rollback")
    String rollbackProp;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_RETRY = 1;
    private static final int STATE_SKIP = 2;
    private static final int STATE_EXCEPTION = 3;

    private int testState = STATE_NORMAL;

    int forcedFailCount = 0;
    boolean isInited = false;
    boolean rollback;
    int count = 1;

    @Override
    public void open(Serializable cpd) throws NamingException {
        InitialContext ctx = new InitialContext();
        dataSource = (DataSource) ctx.lookup(RetryConnectionHelper.jndiName);
    }

    @Override
    public void writeItems(List<Object> records) throws Exception {
        int item = -1;
        int quantity = -1;
        int check = -1;


        if (!isInited) {
            forcedFailCount = Integer.parseInt(forcedFailCountProp);
            rollback = Boolean.parseBoolean(rollbackProp);
            isInited = true;
        }

        for (Object record : records) {
            item = ((NumbersRecord) record).getItem();
            quantity = ((NumbersRecord) record).getQuantity();

            Reporter.log("Writing item: " + item + "...<br>");

            // Throw an exception when forcedFailCount is reached
            if (forcedFailCount != 0 && count >= forcedFailCount && (testState == STATE_NORMAL)) {
                //forcedFailCount = 0;
                testState = STATE_RETRY;
                Reporter.log("Fail on purpose in NumbersRecord.writeItems<p>");
                throw new MyParentException("Fail on purpose in NumbersRecord.writeItems()");
            } else if (forcedFailCount != 0 && count > forcedFailCount && (testState == STATE_EXCEPTION)) {
                testState = STATE_SKIP;
                forcedFailCount = 0;
                Reporter.log("Test skip -- Fail on purpose NumbersRecord.writeItems<p>");
                throw new MyParentException("Test skip -- Fail on purpose in NumbersRecord.writeItems()");
            }

            if (testState == STATE_RETRY) {

                if (((Properties) stepCtx.getTransientUserData()).getProperty("retry.write.exception.invoked") != "true") {
                    Reporter.log("onRetryWriteException not invoked<p>");
                    throw new Exception("onRetryWriteException not invoked");
                } else {
                    Reporter.log("onRetryWriteException was invoked<p>");
                }

                if (((Properties) stepCtx.getTransientUserData()).getProperty("retry.write.exception.match") != "true") {
                    Reporter.log("retryable exception does not match");
                    throw new Exception("retryable exception does not match");
                } else {
                    Reporter.log("retryable exception matches");
                }

                testState = STATE_EXCEPTION;
            } else if (testState == STATE_SKIP) {
                if (((Properties) stepCtx.getTransientUserData()).getProperty("skip.write.item.invoked") != "true") {
                    Reporter.log("onSkipWriteItem not invoked<p>");
                    throw new Exception("onSkipWriteItem not invoked");
                } else {
                    Reporter.log("onSkipWriteItem was invoked<p>");
                }

                if (((Properties) stepCtx.getTransientUserData()).getProperty("skip.write.item.match") != "true") {
                    Reporter.log("skippable exception does not match<p>");
                    throw new Exception("skippable exception does not match");
                } else {
                    Reporter.log("skippable exception matches<p>");
                }
                testState = STATE_NORMAL;
            }

            Connection connection = null;
            PreparedStatement statement = null;

            try {
                connection = RetryConnectionHelper.getConnection(dataSource);

                statement = connection.prepareStatement(RetryConnectionHelper.UPDATE_NUMBERS);
                statement.setInt(2, item);
                statement.setInt(1, quantity);
                Reporter.log("Write [item: " + item + " quantity: " + quantity + "]<p>");
                int rs = statement.executeUpdate();
                count++;

            } catch (SQLException e) {
                throw e;
            } finally {
                RetryConnectionHelper.cleanupConnection(connection, null, statement);
            }
        }
    }

}

