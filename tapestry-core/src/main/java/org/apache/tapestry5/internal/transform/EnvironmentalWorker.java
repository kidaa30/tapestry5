// Copyright 2006, 2007, 2008, 2010, 2011 The Apache Software Foundation
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

import org.apache.tapestry5.annotations.Environmental;
<<<<<<< HEAD
import org.apache.tapestry5.internal.services.ComponentClassCache;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.*;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
=======
import org.apache.tapestry5.ioc.services.Builtin;
import org.apache.tapestry5.ioc.services.ClassFactory;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.services.*;

import java.lang.reflect.Modifier;
import java.util.List;
>>>>>>> refs/remotes/apache/5.0

/**
 * Obtains a value from the {@link Environment} service based on the field type. This is triggered by the presence of
 * the {@link Environmental} annotation.
 */
@SuppressWarnings("rawtypes")
public class EnvironmentalWorker implements ComponentClassTransformWorker2
{
    private final Environment environment;

<<<<<<< HEAD
    private final ComponentClassCache classCache;

=======
    private final ClassLoader classLoader;

    public EnvironmentalWorker(Environment environment, @Builtin ClassFactory servicesLayerClassFactory)
    {
        this.environment = environment;

        classLoader = servicesLayerClassFactory.getClassLoader();
    }
>>>>>>> refs/remotes/apache/5.0

    @SuppressWarnings("unchecked")
    private final class EnvironmentalConduit implements FieldConduit
    {
        private final String componentClassName;

        private final String fieldName;

        private final Class environmentalType;

        private final boolean required;

        private EnvironmentalConduit(String componentClassName, String fieldName, final Class environmentalType,
                boolean required)
        {
            this.componentClassName = componentClassName;
            this.fieldName = fieldName;
            this.environmentalType = environmentalType;
            this.required = required;
        }

        public Object get(Object instance, InstanceContext context)
        {
            return required ? environment.peekRequired(environmentalType) : environment.peek(environmentalType);
        }

        public void set(Object instance, InstanceContext context, Object newValue)
        {
<<<<<<< HEAD
            throw new RuntimeException(String.format("Field %s.%s is read only.", componentClassName, fieldName));
        }
    }

    public EnvironmentalWorker(Environment environment, ComponentClassCache classCache)
    {
        this.environment = environment;

        this.classCache = classCache;
    }
=======
            Environmental annotation = transformation.getFieldAnnotation(name, Environmental.class);

            transformation.claimField(name, annotation);

            String typeName = transformation.getFieldType(name);
>>>>>>> refs/remotes/apache/5.0

    public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model)
    {
        for (PlasticField field : plasticClass.getFieldsWithAnnotation(Environmental.class))
        {
            transform(model.getComponentClassName(), field);
        }
    }

<<<<<<< HEAD
    private void transform(final String componentClassName, PlasticField field)
    {
        Environmental annotation = field.getAnnotation(Environmental.class);
=======
            // TAP5-417: Calls to javassist.runtime.Desc.getType() are showing up as method hot spots.

            Class type = null;

            try
            {
                type = classLoader.loadClass(typeName);
            }
            catch (ClassNotFoundException ex)
            {
                throw new RuntimeException(ex);
            }

            // TAP5-417: Changed the code to use EnvironmentalAccess, which encapsulates
            // efficient caching.

            String injectedTypeFieldName = transformation.addInjectedField(Class.class, "type", type);

            // First we need (at page attach) to acquire the closure for the type.

            String accessFieldName = transformation.addField(Modifier.PRIVATE, EnvironmentalAccess.class.getName(),
                                                             name + "_access");

            String attachBody = String.format("%s = %s.getAccess(%s);",
                                              accessFieldName, envField, injectedTypeFieldName);

            transformation.extendMethod(TransformConstants.CONTAINING_PAGE_DID_ATTACH_SIGNATURE, attachBody);

            // Clear the closure field when the page detaches.  We'll get a new one when we next attach.

            transformation.extendMethod(TransformConstants.CONTAINING_PAGE_DID_DETACH_SIGNATURE,
                                        accessFieldName + " = null;");

            // Now build a read method that invokes peek() or peekRequired() on the closure. The closure
            // is responsible for safe caching of the environmental value.
>>>>>>> refs/remotes/apache/5.0

        field.claim(annotation);

<<<<<<< HEAD
        final String fieldName = field.getName();

        final Class fieldType = classCache.forName(field.getTypeName());
=======
            TransformMethodSignature sig = new TransformMethodSignature(Modifier.PRIVATE, typeName, methodName, null,
                                                                        null);

            String body = String.format(
                    "return ($r) %s.%s();",
                    accessFieldName,
                    annotation.value() ? "peekRequired" : "peek");
>>>>>>> refs/remotes/apache/5.0

        final boolean required = annotation.value();

        ComputedValue<FieldConduit<Object>> provider = new ComputedValue<FieldConduit<Object>>()
        {
            public FieldConduit<Object> get(InstanceContext context)
            {
                return new EnvironmentalConduit(componentClassName, fieldName, fieldType, required);
            }

            public void set(Object instance, InstanceContext context, Object newValue)
            {
                throw new RuntimeException(
                        String.format("Field %s of component %s is read only.", fieldName, componentClassName));
            }
        };

        field.setComputedConduit(provider);
    }
}
