// Copyright 2006-2013 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.transform;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Flow;
import org.apache.tapestry5.func.Predicate;
import org.apache.tapestry5.internal.InternalComponentResources;
import org.apache.tapestry5.internal.ParameterAccess;
import org.apache.tapestry5.internal.bindings.LiteralBinding;
import org.apache.tapestry5.internal.services.ComponentClassCache;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.ioc.services.PerThreadValue;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.util.ExceptionUtils;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.*;
import org.apache.tapestry5.services.BindingSource;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * Responsible for identifying parameters via the {@link org.apache.tapestry5.annotations.Parameter} annotation on
 * component fields. This is one of the most complex of the transformations.
 */
public class ParameterWorker implements ComponentClassTransformWorker2
{
    private final Logger logger = LoggerFactory.getLogger(ParameterWorker.class);

    /**
     * Contains the per-thread state about a parameter, as stored (using
     * a unique key) in the {@link PerthreadManager}. Externalizing such state
     * is part of Tapestry 5.2's pool-less pages.
     */
    private final class ParameterState
    {
        boolean cached;

        Object value;

        void reset(Object defaultValue)
        {
            cached = false;
            value = defaultValue;
        }
    }

    private final ComponentClassCache classCache;

    private final BindingSource bindingSource;

    private final ComponentDefaultProvider defaultProvider;

<<<<<<< HEAD
    private final TypeCoercer typeCoercer;

    private final PerthreadManager perThreadManager;

    public ParameterWorker(ComponentClassCache classCache, BindingSource bindingSource,
                           ComponentDefaultProvider defaultProvider, TypeCoercer typeCoercer, PerthreadManager perThreadManager)
    {
        this.classCache = classCache;
        this.bindingSource = bindingSource;
        this.defaultProvider = defaultProvider;
        this.typeCoercer = typeCoercer;
        this.perThreadManager = perThreadManager;
=======
        String accessFieldName = addParameterSetup(name, annotation.defaultPrefix(), annotation.value(),
                                                   parameterName, cachedFieldName, cache, type, resourcesFieldName,
                                                   transformation, annotation.autoconnect());

        addReaderMethod(name, cachedFieldName, accessFieldName, cache, parameterName, type, resourcesFieldName,
                        transformation);

        addWriterMethod(name, cachedFieldName, accessFieldName, cache, parameterName, type, resourcesFieldName,
                        transformation);
>>>>>>> refs/remotes/apache/5.0
    }

    private final Comparator<PlasticField> byPrincipalThenName = new Comparator<PlasticField>()
    {
<<<<<<< HEAD
        public int compare(PlasticField o1, PlasticField o2)
        {
            boolean principal1 = o1.getAnnotation(Parameter.class).principal();
            boolean principal2 = o2.getAnnotation(Parameter.class).principal();
=======

        String accessFieldName = transformation.addField(Modifier.PRIVATE, ParameterAccess.class.getName(),
                                                         fieldName + "_access");

        String defaultFieldName = transformation.addField(Modifier.PRIVATE, fieldType, fieldName + "_default");

        BodyBuilder builder = new BodyBuilder().begin();

        addDefaultBindingSetup(parameterName, defaultPrefix, defaultBinding, resourcesFieldName,
                               transformation,
                               builder, autoconnect);

        // Order is (alas) important here: must invoke getParameterAccess() after the binding setup, as
        // that code may invoke InternalComponentResources.bindParameter().

        builder.addln("%s = %s.getParameterAccess(\"%s\");", accessFieldName, resourcesFieldName, parameterName);

        // Store the current value of the field into the default field. This value will
        // be used to reset the field after rendering.

        builder.addln("%s = %s;", defaultFieldName, fieldName);
        builder.end();

        transformation.extendMethod(TransformConstants.CONTAINING_PAGE_DID_LOAD_SIGNATURE, builder
                .toString());

        // Now, when the component completes rendering, ensure that any variant parameters are
        // are returned to default value. This isn't necessary when the parameter is not cached,
        // because (unless the binding is invariant), there's no value to get rid of (and if it is
        // invariant, there's no need to get rid of it).

        if (cache)
        {
            builder.clear();

            builder.addln("if (! %s.isInvariant())", accessFieldName);
            builder.begin();
            builder.addln("%s = %s;", fieldName, defaultFieldName);
            builder.addln("%s = false;", cachedFieldName);
            builder.end();
>>>>>>> refs/remotes/apache/5.0

            if (principal1 == principal2)
            {
                return o1.getName().compareTo(o2.getName());
            }

            return principal1 ? -1 : 1;
        }
    };


    public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model)
    {
        Flow<PlasticField> parametersFields = F.flow(plasticClass.getFieldsWithAnnotation(Parameter.class)).sort(byPrincipalThenName);

        for (PlasticField field : parametersFields)
        {
            convertFieldIntoParameter(plasticClass, model, field);
        }
<<<<<<< HEAD
    }

    private void convertFieldIntoParameter(PlasticClass plasticClass, MutableComponentModel model,
                                           PlasticField field)
=======

        return accessFieldName;
    }

    private void addDefaultBindingSetup(String parameterName, String defaultPrefix, String defaultBinding,
                                        String resourcesFieldName,
                                        ClassTransformation transformation,
                                        BodyBuilder builder, boolean autoconnect)
>>>>>>> refs/remotes/apache/5.0
    {

        Parameter annotation = field.getAnnotation(Parameter.class);

        String fieldType = field.getTypeName();

        String parameterName = getParameterName(field.getName(), annotation.name());

        field.claim(annotation);

        model.addParameter(parameterName, annotation.required(), annotation.allowNull(), annotation.defaultPrefix(),
                annotation.cache());

        MethodHandle defaultMethodHandle = findDefaultMethodHandle(plasticClass, parameterName);

        ComputedValue<FieldConduit<Object>> computedParameterConduit = createComputedParameterConduit(parameterName, fieldType,
                annotation, defaultMethodHandle);

        field.setComputedConduit(computedParameterConduit);
    }

<<<<<<< HEAD
=======
        final String methodName = "default" + parameterName;
>>>>>>> refs/remotes/apache/5.0

    private MethodHandle findDefaultMethodHandle(PlasticClass plasticClass, String parameterName)
    {
        final String methodName = "default" + parameterName;

        Predicate<PlasticMethod> predicate = new Predicate<PlasticMethod>()
        {
            public boolean accept(PlasticMethod method)
            {
<<<<<<< HEAD
                return method.getDescription().argumentTypes.length == 0
                        && method.getDescription().methodName.equalsIgnoreCase(methodName);
=======
                return signature.getParameterTypes().length == 0
                        && signature.getMethodName().equalsIgnoreCase(methodName);
>>>>>>> refs/remotes/apache/5.0
            }
        };

        Flow<PlasticMethod> matches = F.flow(plasticClass.getMethods()).filter(predicate);

        // This will match exactly 0 or 1 (unless the user does something really silly)
        // methods, and if it matches, we know the name of the method.

<<<<<<< HEAD
        return matches.isEmpty() ? null : matches.first().getHandle();
    }

    @SuppressWarnings("all")
    private ComputedValue<FieldConduit<Object>> createComputedParameterConduit(final String parameterName,
                                                                               final String fieldTypeName, final Parameter annotation,
                                                                               final MethodHandle defaultMethodHandle)
    {
        boolean primitive = PlasticUtils.isPrimitive(fieldTypeName);
=======
        // Because the check was case-insensitive, we need to determine the actual
        // name.

        String actualMethodName = signatures.get(0).getMethodName();

        builder.addln("if (! %s.isBound(\"%s\"))", resourcesFieldName, parameterName);
        builder.addln("  %s(\"%s\", %s, ($w) %s());",
                      BIND_METHOD_NAME,
                      parameterName,
                      resourcesFieldName,
                      actualMethodName);
    }

    private void addWriterMethod(String fieldName, String cachedFieldName, String accessFieldName, boolean cache,
                                 String parameterName,
                                 String fieldType, String resourcesFieldName,
                                 ClassTransformation transformation)
    {
        BodyBuilder builder = new BodyBuilder();
        builder.begin();

        // Before the component is loaded, updating the property sets the default value
        // for the parameter. The value is stored in the field, but will be
        // rolled into default field inside containingPageDidLoad().

        builder.addln("if (! %s.isLoaded())", resourcesFieldName);
        builder.begin();
        builder.addln("%s = $1;", fieldName);
        builder.addln("return;");
        builder.end();

        // Always start by updating the parameter; this will implicitly check for
        // read-only or unbound parameters. $1 is the single parameter
        // to the method.

        builder.addln("%s.write(($w)$1);", accessFieldName);

        builder.addln("%s = $1;", fieldName);
>>>>>>> refs/remotes/apache/5.0

        final boolean allowNull = annotation.allowNull() && !primitive;

        return new ComputedValue<FieldConduit<Object>>()
        {
            public ParameterConduit get(InstanceContext context)
            {
                final InternalComponentResources icr = context.get(InternalComponentResources.class);

<<<<<<< HEAD
                final Class fieldType = classCache.forName(fieldTypeName);
=======
    /**
     * Adds a private method that will be the replacement for read-access to the field.
     */
    private void addReaderMethod(String fieldName, String cachedFieldName, String accessFieldName, boolean cache,
                                 String parameterName, String fieldType, String resourcesFieldName,
                                 ClassTransformation transformation)
    {
        BodyBuilder builder = new BodyBuilder();
        builder.begin();
>>>>>>> refs/remotes/apache/5.0

                final PerThreadValue<ParameterState> stateValue = perThreadManager.createValue();

<<<<<<< HEAD
                // Rely on some code generation in the component to set the default binding from
                // the field, or from a default method.
=======
        builder.addln("if (%s || ! %s.isLoaded() || ! %s.isBound()) return %s;", cachedFieldName,
                      resourcesFieldName, accessFieldName, fieldName);
>>>>>>> refs/remotes/apache/5.0

                return new ParameterConduit()
                {
                    // Default value for parameter, computed *once* at
                    // page load time.

                    private Object defaultValue = classCache.defaultValueForType(fieldTypeName);

                    private Binding parameterBinding;

                    boolean loaded = false;

                    private boolean invariant = false;

                    {
                        // Inform the ComponentResources about the parameter conduit, so it can be
                        // shared with mixins.

                        icr.setParameterConduit(parameterName, this);
                        icr.getPageLifecycleCallbackHub().addPageLoadedCallback(new Runnable()
                        {
                            public void run()
                            {
                                load();
                            }
                        });
                    }

                    private ParameterState getState()
                    {
                        ParameterState state = stateValue.get();

                        if (state == null)
                        {
                            state = new ParameterState();
                            state.value = defaultValue;
                            stateValue.set(state);
                        }

                        return state;
                    }

                    private boolean isLoaded()
                    {
                        return loaded;
                    }

                    public void set(Object instance, InstanceContext context, Object newValue)
                    {
                        ParameterState state = getState();

                        // Assignments before the page is loaded ultimately exist to set the
                        // default value for the field. Often this is from the (original)
                        // constructor method, which is converted to a real method as part of the transformation.

                        if (!loaded)
                        {
                            state.value = newValue;
                            defaultValue = newValue;
                            return;
                        }

                        // This will catch read-only or unbound parameters.

                        writeToBinding(newValue);

                        state.value = newValue;

                        // If caching is enabled for the parameter (the typical case) and the
                        // component is currently rendering, then the result
                        // can be cached in this ParameterConduit (until the component finishes
                        // rendering).

                        state.cached = annotation.cache() && icr.isRendering();
                    }

                    private Object readFromBinding()
                    {
                        Object result;

                        try
                        {
                            Object boundValue = parameterBinding.get();

                            result = typeCoercer.coerce(boundValue, fieldType);
                        } catch (RuntimeException ex)
                        {
                            throw new TapestryException(String.format(
                                    "Failure reading parameter '%s' of component %s: %s", parameterName,
                                    icr.getCompleteId(), ExceptionUtils.toMessage(ex)), parameterBinding, ex);
                        }

                        if (result == null && !allowNull)
                        {
                            throw new TapestryException(
                                    String.format(
                                            "Parameter '%s' of component %s is bound to null. This parameter is not allowed to be null.",
                                            parameterName, icr.getCompleteId()), parameterBinding, null);
                        }

                        return result;
                    }

                    private void writeToBinding(Object newValue)
                    {
                        // An unbound parameter acts like a simple field
                        // with no side effects.

                        if (parameterBinding == null)
                        {
                            return;
                        }

                        try
                        {
                            Object coerced = typeCoercer.coerce(newValue, parameterBinding.getBindingType());

                            parameterBinding.set(coerced);
                        } catch (RuntimeException ex)
                        {
                            throw new TapestryException(String.format(
                                    "Failure writing parameter '%s' of component %s: %s", parameterName,
                                    icr.getCompleteId(), ExceptionUtils.toMessage(ex)), icr, ex);
                        }
                    }

                    public void reset()
                    {
                        if (!invariant)
                        {
                            getState().reset(defaultValue);
                        }
                    }

                    public void load()
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug(String.format("%s loading parameter %s", icr.getCompleteId(), parameterName));
                        }

                        // If it's bound at this point, that's because of an explicit binding
                        // in the template or @Component annotation.

                        if (!icr.isBound(parameterName))
                        {
                            if (logger.isDebugEnabled())
                            {
                                logger.debug(String.format("%s parameter %s not yet bound", icr.getCompleteId(),
                                        parameterName));
                            }

                            // Otherwise, construct a default binding, or use one provided from
                            // the component.

                            Binding binding = getDefaultBindingForParameter();

                            if (logger.isDebugEnabled())
                            {
                                logger.debug(String.format("%s parameter %s bound to default %s", icr.getCompleteId(),
                                        parameterName, binding));
                            }

                            if (binding != null)
                            {
                                icr.bindParameter(parameterName, binding);
                            }
                        }

                        parameterBinding = icr.getBinding(parameterName);

                        loaded = true;

                        invariant = parameterBinding != null && parameterBinding.isInvariant();

                        getState().value = defaultValue;
                    }

                    public boolean isBound()
                    {
                        return parameterBinding != null;
                    }

                    public Object get(Object instance, InstanceContext context)
                    {
                        if (!isLoaded())
                        {
                            return defaultValue;
                        }

                        ParameterState state = getState();

                        if (state.cached || !isBound())
                        {
                            return state.value;
                        }

                        // Read the parameter's binding and cast it to the
                        // field's type.

                        Object result = readFromBinding();

                        // If the value is invariant, we can cache it until at least the end of the request (before
                        // 5.2, it would be cached forever in the pooled instance).
                        // Otherwise, we we may want to cache it for the remainder of the component render (if the
                        // component is currently rendering).

                        if (invariant || (annotation.cache() && icr.isRendering()))
                        {
                            state.value = result;
                            state.cached = true;
                        }

                        return result;
                    }

                    private Binding getDefaultBindingForParameter()
                    {
                        if (InternalUtils.isNonBlank(annotation.value()))
                        {
                            return bindingSource.newBinding("default " + parameterName, icr,
                                    annotation.defaultPrefix(), annotation.value());
                        }

                        if (annotation.autoconnect())
                        {
                            return defaultProvider.defaultBinding(parameterName, icr);
                        }

                        // Invoke the default method and install any value or Binding returned there.

<<<<<<< HEAD
                        invokeDefaultMethod();
=======
        builder.addln("%s result = ($r) ((%s) %s.read(\"%2$s\"));", fieldType, cast, accessFieldName);
>>>>>>> refs/remotes/apache/5.0

                        return parameterBinding;
                    }

<<<<<<< HEAD
                    private void invokeDefaultMethod()
                    {
                        if (defaultMethodHandle == null)
                        {
                            return;
                        }
=======
        builder.add("if (%s.isInvariant()", accessFieldName);
>>>>>>> refs/remotes/apache/5.0

                        if (logger.isDebugEnabled())
                        {
                            logger.debug(String.format("%s invoking method %s to obtain default for parameter %s",
                                    icr.getCompleteId(), defaultMethodHandle, parameterName));
                        }

                        MethodInvocationResult result = defaultMethodHandle.invoke(icr.getComponent());

                        result.rethrow();

                        Object defaultValue = result.getReturnValue();

                        if (defaultValue == null)
                        {
                            return;
                        }

                        if (defaultValue instanceof Binding)
                        {
                            parameterBinding = (Binding) defaultValue;
                            return;
                        }

                        parameterBinding = new LiteralBinding(null, "default " + parameterName, defaultValue);
                    }


                };
            }
        };
    }

<<<<<<< HEAD
    private static String getParameterName(String fieldName, String annotatedName)
=======
    /**
     * Invoked from generated code as part of the handling of parameter default methods.
     */
    public static void bind(String parameterName, InternalComponentResources resources, Object value)
>>>>>>> refs/remotes/apache/5.0
    {
        if (InternalUtils.isNonBlank(annotatedName))
        {
            return annotatedName;
        }

        return InternalUtils.stripMemberName(fieldName);
    }
}
