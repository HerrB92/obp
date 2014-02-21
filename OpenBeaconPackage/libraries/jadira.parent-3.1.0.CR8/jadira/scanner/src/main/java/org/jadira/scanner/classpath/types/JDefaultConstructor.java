/*
 *  Copyright 2012 Chris Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.scanner.classpath.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javassist.bytecode.MethodInfo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JDefaultConstructor extends JConstructor {

    private static final List<JParameter> EMPTY_PARAMS = Collections.emptyList();

	protected JDefaultConstructor(MethodInfo methodInfo, JClass jClass, ClasspathResolver resolver) {
        super(methodInfo, jClass, resolver);
    }
	
    public static JDefaultConstructor getJConstructor(MethodInfo methodInfo, JClass jClass, ClasspathResolver resolver) {
        return new JDefaultConstructor(methodInfo, jClass, resolver);
    }

    @Override
    public Constructor<?> getActualConstructor() throws ClasspathAccessException {

        try {
            Class<?> clazz = ((JClass) getEnclosingType()).getActualClass();
            return clazz.getConstructor();
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Could not access constructor: " + e, e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Could not find constructor: " + e, e);
        }
    }
    
    public List<JParameter> getParameters() throws ClasspathAccessException {

        return EMPTY_PARAMS;
    }

    public Method getActualMethod() throws ClasspathAccessException {

        try {
            return getEnclosingType().getActualClass().getMethod(getName());
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Problem obtaining method: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Problem finding method: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Set<JAnnotation<?>> getAnnotations() {
    	return Collections.emptySet();
    }

    @Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		// JDefaultConstructor rhs = (JDefaultConstructor) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.toHashCode();
	}
}