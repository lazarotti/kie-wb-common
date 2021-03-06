/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.service;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.backend.vfs.Path;

/**
 * Base service type for Diagrams of type <code>D</code>.
 *
 * @param <D> The type of the Diagram that this service suports.
 */
public interface BaseDiagramService<D extends Diagram> {

    /**
     * Returns a Diagram by the given path in the service.
     * Implementations can throw unchecked exceptions.
     */
    D getDiagramByPath( Path path );

    /**
     * Checks if this service accepts a given Diagram by its path.
     */
    boolean accepts( Path path );

    /**
     * Creates a new Diagram for the given Definition Set identifier into the given path.
     * Implementations can throw unchecked exceptions.*
     *
     * @param path     The path for the new diagram.
     * @param name     The diagram's name to create.
     * @param defSetId The diagram's and graph resulting by the Definition Set identifier.
     */
    Path create( Path path, String name, String defSetId );

    /**
     * Saves or updates the diagram.
     */
    void saveOrUpdate( D diagram );

    /**
     * Deletes the diagram.
     * Implementations can throw unchecked exceptions.
     *
     * @return <code>true</code> if the operation result is success, <code>false</code> otherwise.
     */
    boolean delete( D diagram );

}
