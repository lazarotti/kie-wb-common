/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.ProjectDataModelOracleTestUtils.*;

/**
 * Tests for DataModelService
 */
public class ProjectDataModelServiceTest extends AbstractDataModelWeldTest {

    @Test
    public void testProjectDataModelOracle() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest1/src/main/java/t3p1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 3,
                      oracle.getProjectModelFields().size() );
        assertContains( "t3p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t3p2.Bean2",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "java.lang.String",
                        oracle.getProjectModelFields().keySet() );

        assertTrue( oracle.getProjectEventTypes().get( "t3p1.Bean1" ) );
        assertFalse( oracle.getProjectEventTypes().get( "t3p2.Bean2" ) );

        assertEquals( 3,
                      oracle.getProjectModelFields().get( "t3p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t3p1.Bean1" ) );
        assertContains( "field1",
                        oracle.getProjectModelFields().get( "t3p1.Bean1" ) );
        assertContains( "field2",
                        oracle.getProjectModelFields().get( "t3p1.Bean1" ) );

        assertEquals( 2,
                      oracle.getProjectModelFields().get( "t3p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t3p2.Bean2" ) );
        assertContains( "field1",
                        oracle.getProjectModelFields().get( "t3p2.Bean2" ) );
    }

    @Test
    public void testProjectDataModelOracleJavaDefaultPackage() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest2/src/main/java" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 0,
                      oracle.getProjectModelFields().size() );
    }

}
