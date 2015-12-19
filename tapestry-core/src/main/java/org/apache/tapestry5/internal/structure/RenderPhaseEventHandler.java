<<<<<<< HEAD
// Copyright 2008, 2011 The Apache Software Foundation
=======
// Copyright 2008 The Apache Software Foundation
>>>>>>> refs/remotes/apache/5.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
<<<<<<< HEAD
// http://www.apache.org/licenses/LICENSE-2.0
=======
//     http://www.apache.org/licenses/LICENSE-2.0
>>>>>>> refs/remotes/apache/5.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.structure;

import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.Renderable;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.runtime.RenderQueue;

<<<<<<< HEAD
import java.util.Arrays;
=======
>>>>>>> refs/remotes/apache/5.0
import java.util.List;

/**
 * Used by {@link org.apache.tapestry5.internal.structure.ComponentPageElementImpl} to track the results of invoking the
 * component methods for a render phase event.
 *
 * @since 5.0.19
 */
class RenderPhaseEventHandler implements ComponentEventCallback
{
<<<<<<< HEAD
    private final RenderQueue renderQueue;

=======
>>>>>>> refs/remotes/apache/5.0
    private boolean result = true;

    private List<RenderCommand> commands;

<<<<<<< HEAD
    public RenderPhaseEventHandler(RenderQueue renderQueue)
    {
        this.renderQueue = renderQueue;
    }

=======
>>>>>>> refs/remotes/apache/5.0
    boolean getResult()
    {
        return result;
    }

<<<<<<< HEAD
    void enqueueSavedRenderCommands()
    {
        if (commands != null)
        {
            for (RenderCommand command : commands)
                renderQueue.push(command);
        }

    }

    /**
     * Handles a result (a return value from an event handler method). The result
     * must be Boolean, {@link RenderCommand} or {@link Renderable}.  For the latter two types, the result
     * is converted to a {@link RenderCommand} and added to an internal list; the commands in the list
     * are pushed onto the {@link RenderQueue} at the end of the render phase, when {@link #enqueueSavedRenderCommands()}} is invoked.
     *
     * @param result the result value returned from the event handler method
     * @return true if the event is aborted (a Boolean), false if event processing should continue (other types)
     * @throws RuntimeException for any other type
     */
=======
    void reset()
    {
        result = true;

        commands = null;
    }

>>>>>>> refs/remotes/apache/5.0
    public boolean handleResult(Object result)
    {
        if (result instanceof Boolean)
        {
            this.result = (Boolean) result;
            return true; // abort other handler methods
        }

        if (result instanceof RenderCommand)
        {
            RenderCommand command = (RenderCommand) result;

            add(command);

            return false; // do not abort!
        }

        if (result instanceof Renderable)
        {
            final Renderable renderable = (Renderable) result;

            RenderCommand wrapper = new RenderCommand()
            {
                public void render(MarkupWriter writer, RenderQueue queue)
                {
                    renderable.render(writer);
                }
            };

            add(wrapper);

            return false;
        }

<<<<<<< HEAD
        throw new RuntimeException(StructureMessages.wrongPhaseResultType(Arrays.asList(Boolean.class.getName(),
                Renderable.class.getName(), RenderCommand.class.getName())));
=======
        throw new RuntimeException(StructureMessages.wrongPhaseResultType(Boolean.class));
>>>>>>> refs/remotes/apache/5.0
    }

    private void add(RenderCommand command)
    {
<<<<<<< HEAD
        if (commands == null)
            commands = CollectionFactory.newList();
=======
        if (commands == null) commands = CollectionFactory.newList();
>>>>>>> refs/remotes/apache/5.0

        commands.add(command);
    }

<<<<<<< HEAD
=======
    public void queueCommands(RenderQueue queue)
    {
        if (commands == null) return;

        for (RenderCommand command : commands)
            queue.push(command);
    }
>>>>>>> refs/remotes/apache/5.0
}
