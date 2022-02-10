/*
 * Copyright 2022 Eclipse Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ee.jakarta.tck.batch.arquillian;

import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 *
 * @author omihalyi
 */
public enum DeploymentPackageType {
    WAR {
        @Override
        protected PackageBuilder getPackageBuilder(Archive<?> archive) {
            return new WarPackageBuilder(archive);
        }
    }, EAR {
        @Override
        protected PackageBuilder getPackageBuilder(Archive<?> archive) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public static DeploymentPackageType fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String upperCaseValue = value.toUpperCase();
        // remove special chars, e.g. ejb-jar is turned into EJBJAR
        upperCaseValue = upperCaseValue.replaceAll("-|_|\\.| ", "");
        return DeploymentPackageType.valueOf(upperCaseValue);
    }

    public static DeploymentPackageType fromArchive(Archive<?> archive) {
        if (archive == null) {
            return null;
        }
        if (archive instanceof WebArchive) {
            return WAR;
        } else if (archive instanceof EnterpriseArchive) {
            return EAR;
        } else {
            throw new RuntimeException("Unsupported archive type: " + archive.getClass());
        }
    }

    protected PackageBuilder getPackageBuilder() {
        return getPackageBuilder(null);
    }

    // create builder from an existing archive - modifies the existing archive
    protected abstract PackageBuilder getPackageBuilder(Archive<?> archive);

    interface PackageBuilder {

        PackageBuilder addArtifact(File artifactFile);

        PackageBuilder addResource(Asset resourceAsset, String resourceName);

        Archive<?> build();
    }

    class WarPackageBuilder implements PackageBuilder {

        WebArchive archive;

        public WarPackageBuilder(Archive<?> archive) {
            if (archive == null) {
                this.archive = ShrinkWrap.create(WebArchive.class, "jbatch-test-package-all.war").as(WebArchive.class);
            } else {
                this.archive = archive.as(WebArchive.class);
            }
        }

        @Override
        public WarPackageBuilder addArtifact(File artifactFile) {
            archive.addAsLibrary(artifactFile);
            return this;
        }

        @Override
        public WarPackageBuilder addResource(Asset resourceAsset, String resourceName) {
            archive.addAsResource(resourceAsset, resourceName);
            return this;
        }

        @Override
        public Archive<?> build() {
            return archive;
        }
    }
    
}
