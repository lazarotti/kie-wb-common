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
package org.kie.workbench.common.stunner.core.command.impl;

import org.kie.workbench.common.stunner.core.command.*;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.logging.Level;
import java.util.logging.Logger;

class CommandManagerImpl<C, V> implements CommandManager<C, V>, HasCommandManagerListener<CommandManagerListener<C, V>> {

    private static Logger LOGGER = Logger.getLogger( CommandManagerImpl.class.getName() );

    private CommandManagerListener<C, V> listener;

    CommandManagerImpl() {
        this.listener = null;
    }

    @Override
    public CommandResult<V> allow( final C context,
                                   final Command<C, V> command ) {
        PortablePreconditions.checkNotNull( "command", command );
        LOGGER.log( Level.FINE, "Evaluating (allow) command [" + command + "]..." );
        final CommandResult<V> result = command.allow( context );
        if ( null != listener ) {
            listener.onAllow( context, command, result );
        }
        LOGGER.log( Level.FINE, "Evaluation (allow) of command [" + command + "] finished - "
                + "Result [" + result + "]");
        return result;
    }

    @Override
    public CommandResult<V> execute( final C context,
                                     final Command<C, V> command ) {
        PortablePreconditions.checkNotNull( "command", command );
        LOGGER.log( Level.FINE, "Executing command [" + command + "]..." );
        final CommandResult<V> result = command.execute( context );
        if ( null != listener ) {
            listener.onExecute( context, command, result );
        }
        LOGGER.log( Level.FINE, "Execution of command [" + command + "] finished - "
                + "Result [" + result + "]");
        return result;
    }

    @Override
    public CommandResult<V> undo( final C context,
                                  final Command<C, V> command ) {
        LOGGER.log( Level.FINE, "Undoing command [" + command + "]..." );
        final CommandResult<V> result = command.undo( context );
        if ( null != listener ) {
            listener.onUndo( context, command, result );
        }
        LOGGER.log( Level.FINE, "Undo of command [" + command + "] finished - "
                + "Result [" + result + "]");
        return result;
    }

    @Override
    public void setCommandManagerListener( final CommandManagerListener<C, V> listener ) {
        this.listener = listener;
    }

}
