/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.ConditionExpressionLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.TimeCycle;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.TimeCycleLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.TimeDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.TimeDuration;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.DistributionType;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.StandardDeviation;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.TimeUnit;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.UnitCost;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.WorkingHours;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;

/**
 * This class contains the mappings for the different stencil identifiers that are different from
 * the patterns used in this tool.
 */
public abstract class BaseOryxIdMappings implements OryxIdMappings {

    private final DefinitionManager definitionManager;

    private final Map<Class<?>, String> defMappings = new HashMap<>();

    private final Map<Class<?>, String> globalMappings = getGlobalMappings();

    private final Map<Class<?>, String> customMappings = getCustomMappings();

    private final Map<Class<?>, Set<String>> skippedProperties = getSkippedProperties();

    private final Map<Class<?>, Map<Class<?>, String>> definitionMappings = getDefinitionMappings();

    protected BaseOryxIdMappings() {
        this( null );
    }

    public BaseOryxIdMappings( final DefinitionManager definitionManager ) {
        this.definitionManager = definitionManager;
    }

    @Override
    public void init( final List<Class<?>> definitions ) {
        // Load default & custom mappings for BPMN definitions.
        for ( final Class<?> defClass : definitions ) {
            String customMapping = customMappings.get( defClass );
            customMapping = customMapping != null ? customMapping : globalMappings.get( defClass );
            final String orxId = customMapping != null ? customMapping : getDefaultOryxDefinitionId( defClass );
            defMappings.put( defClass, orxId );
        }
    }

    @Override
    public Map<Class<?>, String> getGlobalMappings() {
        final Map<Class<?>, String> globalMappings = new HashMap<Class<?>, String>() {{
            // Add here global class <-> oryxId mappings, if any.
            put( Name.class, "name" );
            put( TaskType.class, "tasktype" );
            put( NoneTask.class, "Task" );
            put( UserTask.class, "Task" );
            put( ScriptTask.class, "Task" );
            put( BusinessRuleTask.class, "Task" );
            put( RuleFlowGroup.class, "ruleflowgroup" );
            put( CalledElement.class, "calledelement" );
            put( ScriptLanguage.class, "script_language" );
            put( ConditionExpression.class, "conditionexpression" );
            put( ConditionExpressionLanguage.class, "conditionexpressionlanguage" );
            put( Priority.class, "priority" );
            put( ExclusiveDatabasedGateway.class, "Exclusive_Databased_Gateway" );
            put( TimeDate.class, "timedate" );
            put( TimeDuration.class, "timeduration" );
            put( TimeCycle.class, "timecycle" );
            put( TimeCycleLanguage.class, "timecyclelanguage" );

            // Simulation properties
            put( TimeUnit.class, "timeunit" );
            put( StandardDeviation.class, "standarddeviation" );
            put( DistributionType.class, "distributiontype" );
            put( WorkingHours.class, "workinghours" );
            put( UnitCost.class, "unitcost" );
        }};

        return globalMappings;
    }

    @Override
    public Map<Class<?>, String> getCustomMappings() {
        // No custom mappings, for now.
        return Collections.emptyMap();
    }

    @Override
    public Map<Class<?>, Set<String>> getSkippedProperties() {
        final Map<Class<?>, Set<String>> skippedProperties = new HashMap<Class<?>, Set<String>>() {{
            // Add here global class <-> collection oryx property identifiers to skip processing, if any.
            put( BPMNDiagram.class, new HashSet<String>() {{
                add( "name" );
            }} );
        }};

        return skippedProperties;
    }

    @Override
    public Map<Class<?>, Map<Class<?>, String>> getDefinitionMappings() {
        final Map<Class<?>, Map<Class<?>, String>> definitionMappings = new HashMap<Class<?>, Map<Class<?>, String>>() {{
            // Add here class <-> oryxId mappings just for a concrete definition (stencil), if any.
            Map<Class<?>, String> diagramPropertiesMap = new HashMap<Class<?>, String>();
            put( BPMNDiagram.class, diagramPropertiesMap );
            // The name property in the diagram stencil is "processn".
            diagramPropertiesMap.put( Name.class, "processn" );
            // The process variables property in the diagram stencil is "vardefs".
            diagramPropertiesMap.put( ProcessVariables.class, "vardefs" );
            Map<Class<?>, String> userTaskPropertiesMap = new HashMap<Class<?>, String>();
            put( UserTask.class, userTaskPropertiesMap );
            userTaskPropertiesMap.put( AssignmentsInfo.class, "assignmentsinfo" );
            userTaskPropertiesMap.put( TaskName.class, "taskname" );
            Map<Class<?>, String> exclusiveDatabasedGatewayPropertiesMap = new HashMap<Class<?>, String>();
            put( ExclusiveDatabasedGateway.class, exclusiveDatabasedGatewayPropertiesMap );
            exclusiveDatabasedGatewayPropertiesMap.put( DefaultRoute.class, "defaultgate" );

        }};

        return definitionMappings;
    }

    @Override
    public String getOryxDefinitionId( final Class<?> clazz ) {
        return defMappings.get( clazz );
    }

    @Override
    public String getOryxPropertyId( final Class<?> clazz ) {
        String mapping = customMappings.get( clazz );
        mapping = mapping != null ? mapping : globalMappings.get( clazz );
        return mapping != null ? mapping : getDefaultOryxPropertyId( clazz );
    }

    @Override
    public String getOryxPropertyId( final Class<?> definitionClass,
                                     final Class<?> clazz ) {
        Map<Class<?>, String> mappings = definitionMappings.get( definitionClass );
        if ( null != mappings ) {
            String r = mappings.get( clazz );
            if ( null != r ) {
                return r;
            }
        }
        return getOryxPropertyId( clazz );
    }

    @Override
    public boolean isSkipProperty( final Class<?> definitionClass,
                                   final String oryxPropertyId ) {
        Set<String> toSkip = skippedProperties.get( definitionClass );
        return toSkip != null && toSkip.contains( oryxPropertyId );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<?> getProperty( final T definition,
                                     final String oryxId ) {
        Class<?> clazz = getKey( oryxId,
                                 customMappings );
        if ( null != clazz ) {
            return clazz;
        }
        clazz = getKey( oryxId,
                        globalMappings );
        if ( null != clazz ) {
            return clazz;
        }

        Set<Object> properties = (Set<Object>) definitionManager.adapters().forDefinition().getProperties( definition );
        if ( null != properties && !properties.isEmpty() ) {
            for ( Object property : properties ) {
                Class<?> pClass = property.getClass();
                String pId = getDefaultOryxPropertyId( pClass );
                if ( oryxId.equals( pId ) ) {
                    return pClass;
                }
            }

        }
        return null;
    }

    @Override
    public Class<?> getDefinition( final String oryxId ) {
        return get( oryxId,
                    defMappings );
    }

    @Override
    public <T> String getPropertyId( final T definition,
                                     final String oryxId ) {
        Class<?> definitionClass = definition.getClass();
        Map<Class<?>, String> mappings = definitionMappings.get( definitionClass );
        if ( null != mappings ) {
            Class<?> p = get( oryxId, mappings );
            if ( null != p ) {
                return getPropertyId( p );
            }
        }
        Class<?> c = getProperty( definition,
                                  oryxId );
        return null != c ? getPropertyId( c ) : null;
    }

    @Override
    public String getDefinitionId( final String oryxId ) {
        Class<?> c = getDefinition( oryxId );
        return null != c ? getDefinitionId( c ) : null;
    }

    @Override
    public String getPropertyId( final Class<?> clazz ) {
        return BindableAdapterUtils.getPropertyId( clazz );
    }

    @Override
    public String getDefinitionId( final Class<?> clazz ) {
        return BindableAdapterUtils.getDefinitionId( clazz );
    }

    private Class<?> get( final String oryxId,
                          final Map<Class<?>, String> map ) {
        Class<?> r = getKey( oryxId, map );
        if ( null != r ) {
            return r;
        }
        return null;
    }

    private Class<?> getKey( final String value,
                             final Map<Class<?>, String> map ) {
        Set<Map.Entry<Class<?>, String>> entrySet = map.entrySet();
        for ( Map.Entry<Class<?>, String> entry : entrySet ) {
            String oId = entry.getValue();
            if ( oId.equals( value ) ) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getDefaultOryxDefinitionId( final Class<?> clazz ) {
        return clazz.getSimpleName();
    }

    private String getDefaultOryxPropertyId( final Class<?> clazz ) {
        return StringUtils.uncapitalize( clazz.getSimpleName() );
    }

}
