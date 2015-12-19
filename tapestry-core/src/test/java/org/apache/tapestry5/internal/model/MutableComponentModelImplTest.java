// Copyright 2006, 2007, 2008, 2011, 2012 The Apache Software Foundation
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

package org.apache.tapestry5.internal.model;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.internal.test.InternalBaseTestCase;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.model.MutableEmbeddedComponentModel;
import org.apache.tapestry5.model.ParameterModel;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * Tests {@link org.apache.tapestry5.internal.model.MutableComponentModelImpl} and {@link
 * org.apache.tapestry5.internal.model.MutableEmbeddedComponentModelImpl}.
 */
public class MutableComponentModelImplTest extends InternalBaseTestCase
{
    private static final String COMPONENT_CLASS_NAME = "org.example.components.Fred";

    private static final String CLASS_NAME = "org.example.components.Foo";

    @Test
    public void root_class_vs_sub_class()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertTrue(model.isRootClass());

        MutableComponentModel subModel = new MutableComponentModelImpl(CLASS_NAME, logger, r, model, false, null);

        assertFalse(subModel.isRootClass());

        verify();
    }

    @Test
    public void add_new_parameter()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertTrue(model.getParameterNames().isEmpty());

        String parameterName = "value";

        model.addParameter(parameterName, true, true, BindingConstants.PROP);

        ParameterModel pm = model.getParameterModel(parameterName);

        assertEquals(pm.getName(), parameterName);
        assertEquals(true, pm.isRequired());
        assertEquals(pm.getDefaultBindingPrefix(), BindingConstants.PROP);

        assertEquals(model.getDeclaredParameterNames(), Arrays.asList(parameterName));

        // Verify that the binding prefix is actually stored:

        model.addParameter("fred", true, true, "flint");

        // Checks that parameter names are case insensitive

        assertEquals(model.getParameterModel("Fred").getDefaultBindingPrefix(), "flint");

        verify();
    }

    @Test
    public void parameter_names_are_sorted()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        model.addParameter("fred", true, true, BindingConstants.PROP);
        model.addParameter("wilma", true, true, BindingConstants.PROP);
        model.addParameter("barney", true, true, BindingConstants.PROP);

        assertEquals(model.getDeclaredParameterNames(), Arrays.asList("barney", "fred", "wilma"));

        verify();
    }

    @Test
    public void declared_parameter_names_does_not_include_superclass_parameters()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();
        ComponentModel parent = mockComponentModel();

        train_getPersistentFieldNames(parent);
        train_getParameterNames(parent, "betty");

        expect(parent.getParameterModel("fred")).andReturn(null);
        expect(parent.getParameterModel("wilma")).andReturn(null);
        expect(parent.getParameterModel("barney")).andReturn(null);

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        model.addParameter("fred", true, true, BindingConstants.PROP);
        model.addParameter("wilma", true, true, BindingConstants.PROP);
        model.addParameter("barney", true, true, BindingConstants.PROP);

        assertEquals(model.getDeclaredParameterNames(), Arrays.asList("barney", "fred", "wilma"));
        assertEquals(model.getParameterNames(), Arrays.asList("barney", "betty", "fred", "wilma"));

        verify();
    }

    @Test
    public void libraryName_property()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, "superduper");

        assertEquals(model.getLibraryName(), "superduper");

        verify();
    }

    @Test
    public void add_duplicate_parameter()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        model.addParameter("fred", true, true, BindingConstants.PROP);

        try
        {
            // This also helps check that the comparison is caseless!

            model.addParameter("Fred", true, true, BindingConstants.PROP);
            unreachable();
        } catch (IllegalArgumentException ex)
        {
            assertEquals(ex.getMessage(),
                    "Parameter 'Fred' of component class org.example.components.Foo is already defined.");
        }

        verify();
    }

    @Test
    public void get_parameter_by_name_with_no_parameters_defined()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertNull(model.getParameterModel("foo"));

        verify();
    }

    @Test
    public void get_unknown_parameter()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
        model.addParameter("fred", true, true, BindingConstants.PROP);

        assertNull(model.getParameterModel("barney"));

        verify();
    }

    @Test
    public void add_embedded()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();
        Location l = mockLocation();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertTrue(model.getEmbeddedComponentIds().isEmpty());

        MutableEmbeddedComponentModel fred = model.addEmbeddedComponent("fred", "Fred", COMPONENT_CLASS_NAME, false, l);

        assertEquals(fred.getId(), "fred");
        assertEquals(fred.getComponentType(), "Fred");
        assertFalse(fred.getInheritInformalParameters());
        assertSame(fred.getLocation(), l);

        MutableEmbeddedComponentModel barney = model.addEmbeddedComponent("barney", "Barney", COMPONENT_CLASS_NAME,
                false, null);

        assertEquals(model.getEmbeddedComponentIds(), Arrays.asList("barney", "fred"));

        assertSame(model.getEmbeddedComponentModel("fred"), fred);
        assertSame(model.getEmbeddedComponentModel("barney"), barney);

        // Access by id is case insensitive

        assertSame(model.getEmbeddedComponentModel("FRED"), fred);
        assertSame(model.getEmbeddedComponentModel("BARNEY"), barney);

        assertEquals(fred.toString(),
                "EmbeddedComponentModel[id=fred type=Fred class=org.example.components.Fred inheritInformals=false]");

        verify();
    }

    @Test
    public void add_embedded_component_with_duplicate_id()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        model.addEmbeddedComponent("fred", "Fred1", COMPONENT_CLASS_NAME, false, null);

        try
        {
            model.addEmbeddedComponent("fred", "Fred2", COMPONENT_CLASS_NAME, false, null);
            unreachable();
        } catch (IllegalArgumentException ex)
        {
            assertEquals(ex.getMessage(),
                    "Embedded component 'fred' has already been defined for component class org.example.components.Foo.");
        }

        verify();
    }

    @Test
    public void add_embedded_with_inherit_informal_parameters()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();
        Location l = mockLocation();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertTrue(model.getEmbeddedComponentIds().isEmpty());

        MutableEmbeddedComponentModel fred = model.addEmbeddedComponent("fred", "Fred", COMPONENT_CLASS_NAME, true, l);

        assertTrue(fred.getInheritInformalParameters());

        assertEquals(fred.toString(),
                "EmbeddedComponentModel[id=fred type=Fred class=org.example.components.Fred inheritInformals=true]");

        verify();
    }


    @Test
    public void add_embedded_is_case_insensitive()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        model.addEmbeddedComponent("fred", "Fred1", COMPONENT_CLASS_NAME, false, null);

        try
        {
            model.addEmbeddedComponent("FRED", "Fred2", COMPONENT_CLASS_NAME, false, null);
            unreachable();
        } catch (IllegalArgumentException ex)
        {
            assertEquals(ex.getMessage(),
                    "Embedded component 'FRED' has already been defined for component class org.example.components.Foo.");
        }

        verify();
    }

    @Test
    public void add_parameters_to_embedded()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        MutableEmbeddedComponentModel fred = model.addEmbeddedComponent("fred", "Fred", COMPONENT_CLASS_NAME, false,
                null);

        assertTrue(fred.getParameterNames().isEmpty());

        fred.addParameter("city", "bedrock");
        fred.addParameter("job", "crane operator");

        assertEquals(fred.getParameterNames(), Arrays.asList("city", "job"));

        assertEquals(fred.getParameterValue("city"), "bedrock");

        verify();
    }

    @Test
    public void add_duplicate_parameters_to_embedded()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        MutableEmbeddedComponentModel fred = model.addEmbeddedComponent("fred", "Fred", COMPONENT_CLASS_NAME, false,
                null);

        fred.addParameter("city", "bedrock");

        try
        {
            fred.addParameter("city", "slateville");
            unreachable();
        } catch (IllegalArgumentException ex)
        {
            assertEquals(ex.getMessage(),
                    "A value for parameter 'city' of embedded component fred (of component class org.example.components.Foo) has already been provided.");
        }

        verify();
    }

    @Test
    public void mixin_names_is_initially_empty_list()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        MutableEmbeddedComponentModel fred = model.addEmbeddedComponent("fred", "Fred", COMPONENT_CLASS_NAME, false,
                null);

        assertTrue(fred.getMixinClassNames().isEmpty());

        verify();
    }

    @Test
    public void mixin_class_names_remembered_in_order_added()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        MutableEmbeddedComponentModel fred = model.addEmbeddedComponent("fred", "Fred", COMPONENT_CLASS_NAME, false,
                null);

        fred.addMixin("zip.zop.Zoom");
        fred.addMixin("foo.bar.Baz");

        assertEquals(fred.getMixinClassNames(), Arrays.asList("zip.zop.Zoom", "foo.bar.Baz"));

        verify();
    }

    @Test
    public void mixin_order_remembered()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        MutableEmbeddedComponentModel fred = model.addEmbeddedComponent("fred", "Fred", COMPONENT_CLASS_NAME, false,
                null);

        fred.addMixin("zip.zop.Zoom", "before:*", "after:foo.bar.Baz");
        fred.addMixin("foo.bar.Baz");

        assertEquals(fred.getConstraintsForMixin("zip.zop.Zoom"), new String[]{"before:*", "after:foo.bar.Baz"});
        assertEquals(fred.getConstraintsForMixin("foo.bar.Baz"), new String[0]);

        verify();
    }

    @Test
    public void mixin_name_conflict()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        MutableEmbeddedComponentModel fred = model.addEmbeddedComponent("fred", "Fred", COMPONENT_CLASS_NAME, false,
                null);

        fred.addMixin("zip.zop.Zoom");

        try
        {
            fred.addMixin("zip.zop.Zoom");
            unreachable();
        } catch (IllegalArgumentException ex)
        {
            assertEquals(ex.getMessage(), "Mixin zip.zop.Zoom (for component fred) has already been defined.");
        }

        // Make sure it wasn't actually added.

        assertEquals(fred.getMixinClassNames(), Arrays.asList("zip.zop.Zoom"));

        verify();
    }

    @Test
    public void get_persistent_field_names_when_none_defined()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertTrue(model.getPersistentFieldNames().isEmpty());

        verify();
    }

    @Test
    public void get_persistent_field_names_are_sorted()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertEquals(model.setFieldPersistenceStrategy("fred", "session"), "fred");
        assertEquals(model.setFieldPersistenceStrategy("barney", "client"), "barney");

        assertEquals(model.getPersistentFieldNames(), Arrays.asList("barney", "fred"));

        verify();
    }

    @Test
    public void get_persistent_field_names_reflects_parent_model()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertEquals(parent.setFieldPersistenceStrategy("wilma", "session"), "wilma");

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        assertEquals(model.setFieldPersistenceStrategy("fred", "session"), "fred");
        assertEquals(model.setFieldPersistenceStrategy("barney", "client"), "barney");

        assertEquals(model.getPersistentFieldNames(), Arrays.asList("barney", "fred", "wilma"));

        verify();
    }

    @Test
    public void persistent_field_names_allocated_in_subclasses_are_unique()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
        assertEquals(parent.setFieldPersistenceStrategy("wilma", "session"), "wilma");

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        assertEquals(model.setFieldPersistenceStrategy("wilma", "session"), "wilma_0");

        assertEquals(model.getPersistentFieldNames(), Arrays.asList("wilma", "wilma_0"));

        verify();
    }

    @Test
    public void get_persistent_field_defined_in_model()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        model.setFieldPersistenceStrategy("fred", "session");
        model.setFieldPersistenceStrategy("barney", "client");

        assertEquals(model.getFieldPersistenceStrategy("fred"), "session");
        assertEquals(model.getFieldPersistenceStrategy("barney"), "client");

        verify();
    }

    @Test
    public void no_persistence_defined_for_field()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        try
        {
            model.getFieldPersistenceStrategy("someField");
            unreachable();
        } catch (IllegalArgumentException ex)
        {
            assertEquals(ex.getMessage(), "No field persistence strategy has been defined for field \'someField\'.");
        }

        verify();
    }

    @Test
    public void get_persistent_field_defined_in_parent()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        parent.setFieldPersistenceStrategy("wilma", "parent");

        model.setFieldPersistenceStrategy("fred", "session");

        assertEquals(model.getFieldPersistenceStrategy("wilma"), "parent");

        verify();
    }

    @Test
    public void default_for_supports_informal_parameters_is_false()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertFalse(model.getSupportsInformalParameters());

        model.enableSupportsInformalParameters();

        assertTrue(model.getSupportsInformalParameters());

        verify();
    }

    @Test
    public void get_mixin_class_names_with_no_mixins()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();
        ComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        assertTrue(model.getMixinClassNames().isEmpty());

        verify();
    }

    @Test
    public void get_mixin_class_names_mixes_with_parent_model()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        parent.addMixinClassName("Wilma");

        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        child.addMixinClassName("Fred");
        child.addMixinClassName("Barney");

        assertEquals(child.getMixinClassNames(), Arrays.asList("Barney", "Fred", "Wilma"));

        verify();
    }

    @Test
    public void get_order_for_mixin_with_parent_model()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        parent.addMixinClassName("Wilma", "before:Fred");

        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        child.addMixinClassName("Fred", "after:Barney");
        child.addMixinClassName("Barney");

        assertEquals(child.getOrderForMixin("Wilma"), new String[]{"before:Fred"});
        assertEquals(child.getOrderForMixin("Fred"), new String[]{"after:Barney"});
        assertEquals(child.getOrderForMixin("Barney"), null);

        verify();
    }

    @Test
    public void get_order_for_mixin_with_collision_in_parent_model()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        parent.addMixinClassName("Wilma", "before:Fred");

        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        child.addMixinClassName("Wilma", "before:*");
        child.addMixinClassName("Fred", "after:Barney");
        child.addMixinClassName("Barney");

        assertEquals(child.getOrderForMixin("Wilma"), new String[]{"before:*"});
        assertEquals(child.getOrderForMixin("Fred"), new String[]{"after:Barney"});
        assertEquals(child.getOrderForMixin("Barney"), null);

        verify();
    }

    @Test
    public void get_parent_from_subclass()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        assertSame(child.getParentModel(), parent);
        assertNull(parent.getParentModel());

        verify();
    }

    @Test
    public void set_and_get_meta()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);

        model.setMeta("fred", "flintstone");
        model.setMeta("barney", "rubble");

        assertEquals(model.getMeta("fred"), "flintstone");
        assertEquals(model.getMeta("barney"), "rubble");

        // Ensure case insensitive:

        assertEquals(model.getMeta("FRED"), "flintstone");
        assertEquals(model.getMeta("BARNEY"), "rubble");

        verify();
    }

    @Test
    public void get_meta_from_parent()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        parent.setMeta("fred", "flintstone");

        assertEquals(child.getMeta("fred"), "flintstone");

        verify();
    }

    @Test
    public void parent_does_not_have_meta()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        parent.setMeta("fred", "flintstone");

        assertNull(child.getMeta("wilma"));

        verify();
    }

    @Test
    public void child_meta_overrides_parent_meta()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);

        parent.setMeta("fred", "flintstone");
        child.setMeta("fred", "mcmurray");

        assertEquals(parent.getMeta("fred"), "flintstone");
        assertEquals(child.getMeta("fred"), "mcmurray");

        verify();
    }

    /**
     * @since 5.0.19
     */
    @Test
    public void does_not_handle_render_phase_and_no_parent()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

<<<<<<< HEAD
        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
=======
        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null);
>>>>>>> refs/remotes/apache/5.0


        assertFalse(model.getHandledRenderPhases().contains(BeginRender.class));

        verify();
    }

    /**
     * @since 5.0.19
     */
    @Test
    public void handles_render_phase()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

<<<<<<< HEAD
        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
=======
        MutableComponentModel model = new MutableComponentModelImpl(CLASS_NAME, logger, r, null);
>>>>>>> refs/remotes/apache/5.0

        model.addRenderPhase(BeginRender.class);

        assertTrue(model.getHandledRenderPhases().contains(BeginRender.class));

        verify();
    }

    /**
     * @since 5.0.19
     */
    @Test
    public void parent_handles_render_phase()
    {
        Resource r = mockResource();
        Logger logger = mockLogger();

        replay();

<<<<<<< HEAD
        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null, false, null);
        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent, false, null);
=======
        MutableComponentModel parent = new MutableComponentModelImpl(CLASS_NAME, logger, r, null);
        MutableComponentModel child = new MutableComponentModelImpl(CLASS_NAME, logger, r, parent);
>>>>>>> refs/remotes/apache/5.0

        parent.addRenderPhase(BeginRender.class);


        assertTrue(child.getHandledRenderPhases().contains(BeginRender.class));

        verify();
    }
}
