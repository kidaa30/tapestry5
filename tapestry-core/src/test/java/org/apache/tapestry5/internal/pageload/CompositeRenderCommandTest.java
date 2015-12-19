<<<<<<< HEAD
// Copyright 2008, 2009 The Apache Software Foundation
=======
// Copyright 2008 The Apache Software Foundation
>>>>>>> refs/remotes/apache/5.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.pageload;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.internal.test.InternalBaseTestCase;
import org.apache.tapestry5.runtime.RenderCommand;
import org.apache.tapestry5.runtime.RenderQueue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CompositeRenderCommandTest extends InternalBaseTestCase
{
<<<<<<< HEAD
    @DataProvider
=======
    @DataProvider(name = "nyi")
>>>>>>> refs/remotes/apache/5.0
    public Object[][] nyi_data()
    {
        RenderCommand push = new RenderCommand()
        {
            public void render(MarkupWriter writer, RenderQueue queue)
            {
                queue.push(null);
            }
        };

        RenderCommand startComponent = new RenderCommand()
        {
            public void render(MarkupWriter writer, RenderQueue queue)
            {
                queue.startComponent(null);
            }
        };

        RenderCommand endComponent = new RenderCommand()
        {
            public void render(MarkupWriter writer, RenderQueue queue)
            {
                queue.endComponent();
            }
        };


        return new Object[][] {
                {
                        push
                },
                {
                        startComponent
                },
                {
                        endComponent
                }
        };
    }

<<<<<<< HEAD
    @Test(dataProvider = "nyi_data")
=======
    @Test(dataProvider = "nyi")
>>>>>>> refs/remotes/apache/5.0
    public void render_queue_commands_nyi
            (RenderCommand
                    rc)
    {
        MarkupWriter writer = mockMarkupWriter();
        RenderQueue queue = mockRenderQueue();

        try
        {
<<<<<<< HEAD
            new CompositeRenderCommand(new RenderCommand[] { rc }).render(writer, queue);
=======
            new CompositeRenderCommand(new RenderCommand[] {rc}).render(writer, queue);
>>>>>>> refs/remotes/apache/5.0

            unreachable();
        }
        catch (IllegalStateException ex)
        {
            // Don't care about the message.
        }
    }
}
