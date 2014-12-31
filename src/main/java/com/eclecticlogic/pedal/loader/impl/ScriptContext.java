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

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kabram.
 *
 */
public class ScriptContext {

    private Class<?> entityClass;
    private List<String> attributes;
    private List<Object> createdEntities = new ArrayList<>();

    @SuppressWarnings("serial")
    private Closure<Object> defaultRowClosure = new Closure<Object>(this) {

        @Override
        public Object call(Object argument) {
            return argument;
        }
    };


    public Class<?> getEntityClass() {
        return entityClass;
    }


    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }


    public List<String> getAttributes() {
        return attributes;
    }


    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }


    public List<Object> getCreatedEntities() {
        return createdEntities;
    }


    public Closure<Object> getDefaultRowClosure() {
        return defaultRowClosure;
    }


    public void setDefaultRowClosure(Closure<Object> defaultRowClosure) {
        this.defaultRowClosure = defaultRowClosure;
    }

}
