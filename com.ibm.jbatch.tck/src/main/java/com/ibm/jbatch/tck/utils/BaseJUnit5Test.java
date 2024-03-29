/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.ibm.jbatch.tck.utils;

import ee.jakarta.tck.batch.util.Reporter;
import ee.jakarta.tck.batch.util.extensions.VehicleInvocationInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VehicleInvocationInterceptor.class)
public class BaseJUnit5Test {
    
    @BeforeEach
    public void setUpReporter(TestReporter reporter) {
        Reporter.reporterRef.set(reporter);
    }

    @AfterEach
    public void cleanUpReporter() {
        Reporter.reporterRef.remove();
    }
}
