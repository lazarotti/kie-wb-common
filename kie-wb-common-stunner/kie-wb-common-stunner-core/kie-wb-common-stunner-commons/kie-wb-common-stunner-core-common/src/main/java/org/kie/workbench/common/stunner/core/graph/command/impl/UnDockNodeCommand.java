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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.List;

/**
 * Removes the parent-dock relationship  ( Dock ) between two nodes.
 */
@Portable
public class UnDockNodeCommand extends AbstractGraphCommand {

    private final String parentUUID;
    private final String candidateUUID;
    private transient Node<?, Edge> parent;
    private transient Node<?, Edge> candidate;

    public UnDockNodeCommand( @MapsTo( "parentUUID" ) String parentUUID,
                              @MapsTo( "candidateUUID" ) String candidateUUID ) {
        this.parentUUID = PortablePreconditions.checkNotNull( "parentUUID",
                parentUUID );
        this.candidateUUID = PortablePreconditions.checkNotNull( "candidateUUID",
                candidateUUID );
    }

    public UnDockNodeCommand( Node<?, Edge> parent,
                              Node<?, Edge> candidate ) {
        this( parent.getUUID(), candidate.getUUID() );
        this.parent = parent;
        this.candidate = candidate;
    }

    @Override
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context ) {
        final CommandResult<RuleViolation> results = allow( context );
        if ( !results.getType().equals( CommandResult.Type.ERROR ) ) {
            final Node<?, Edge> parent = getParent( context );
            final Node<?, Edge> candidate = getCandidate( context );
            final Edge<Dock, Node> edge = getEdgeForTarget( parent, candidate );
            if ( null != edge ) {
                edge.setSourceNode( null );
                edge.setTargetNode( null );
                parent.getInEdges().remove( edge );
                candidate.getOutEdges().remove( edge );
                getMutableIndex( context ).removeEdge( edge );
            }
        }
        return results;
    }

    @SuppressWarnings( "unchecked" )
    private Edge<Dock, Node> getEdgeForTarget( final Node<?, Edge> parent,
                                               final Node<?, Edge> candidate ) {
        final List<Edge> outEdges = parent.getInEdges();
        if ( null != outEdges && !outEdges.isEmpty() ) {
            for ( Edge<?, Node> outEdge : outEdges ) {
                if ( outEdge.getContent() instanceof Dock ) {
                    final Node source = outEdge.getSourceNode();
                    if ( null != source && source.equals( candidate ) ) {
                        return ( Edge<Dock, Node> ) outEdge;
                    }
                }
            }
        }
        return null;
    }

    protected CommandResult<RuleViolation> check( final GraphCommandExecutionContext context ) {
        getParent( context );
        getCandidate( context );
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo( GraphCommandExecutionContext context ) {
        final DockNodeCommand undoCommand =
                new DockNodeCommand( getParent( context ), getCandidate( context ) );
        return undoCommand.execute( context );
    }

    @SuppressWarnings( "unchecked" )
    private Node<?, Edge> getParent( final GraphCommandExecutionContext context ) {
        if ( null == parent ) {
            parent = checkNodeNotNull( context, parentUUID );
        }
        return parent;
    }

    @SuppressWarnings( "unchecked" )
    private Node<?, Edge> getCandidate( final GraphCommandExecutionContext context ) {
        if ( null == candidate ) {
            candidate = checkNodeNotNull( context, candidateUUID );
        }
        return candidate;
    }

    public Node<?, Edge> getParent() {
        return parent;
    }

    public Node<?, Edge> getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return "DeleteDockEdgeCommand [parent=" + parentUUID + ", candidate=" + candidateUUID + "]";
    }
}
