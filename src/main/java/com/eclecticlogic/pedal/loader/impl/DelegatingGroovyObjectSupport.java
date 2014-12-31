/**
 * Copyright (c) 2014 Eclectic Logic LLC
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
 * 
 */
package com.eclecticlogic.pedal.loader.impl;

import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

/**
 * Allows groovy style property setting for any regular java object including automatic type conversions that groovy
 * performs.
 * 
 * @author kabram.
 *
 */
public class DelegatingGroovyObjectSupport<T> implements GroovyObject {

    private transient MetaClass metaClass;
    private T delegate;


    public DelegatingGroovyObjectSupport(T delegate) {
        super();
        this.delegate = delegate;
        metaClass = InvokerHelper.getMetaClass(delegate.getClass());
    }


    @Override
    public Object invokeMethod(String name, Object args) {
        return metaClass.invokeMethod(delegate, name, args);
    }


    @Override
    public Object getProperty(String propertyName) {
        return metaClass.getProperty(delegate, propertyName);
    }


    @Override
    public void setProperty(String propertyName, Object newValue) {
        metaClass.setProperty(delegate, propertyName, newValue);
    }


    @Override
    public MetaClass getMetaClass() {
        return metaClass;
    }


    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

}
