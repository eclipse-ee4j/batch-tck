/*
 * Copyright 2024 International Business Machines Corp. and others
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
package com.ibm.jbatch.tck.cdi;

import jakarta.batch.api.BatchProperty;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Dependent
@Named("NamedTestBean")
public class NamedTestBean {

    @Inject
    @BatchProperty(name = "prop1")
    private String prop1;

    @Inject
    @BatchProperty
    private String prop2;

    public String getProp1() {
        return prop1;
    }

    public String getProp2() {
        return prop2;
    }
}
