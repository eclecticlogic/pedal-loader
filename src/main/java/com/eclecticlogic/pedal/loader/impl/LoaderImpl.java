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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.eclecticlogic.pedal.loader.Loader;
import com.eclecticlogic.pedal.loader.LoaderExecutor;
import com.eclecticlogic.pedal.loader.Script;

/**
 * Data loader entry class.
 * 
 * @author kabram.
 *
 */
public class LoaderImpl implements Loader {

    private String scriptDirectory;
    @PersistenceContext
    private EntityManager entityManager;

    private Map<String, Closure<Object>> customMethods = new HashMap<>();


    public LoaderImpl() {
        super();
    }


    public LoaderImpl(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }


    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public Loader withScriptDirectory(String directory) {
        scriptDirectory = directory;
        return this;
    }


    @Override
    public Loader withCustomMethod(String methodName, Closure<Object> closure) {
        customMethods.put(methodName, closure);
        return this;
    }


    private ScriptExecutor createScriptExecutor() {
        ScriptExecutor executor = new ScriptExecutor(entityManager);
        executor.setScriptDirectory(scriptDirectory);
        executor.setCustomMethods(customMethods);
        return executor;
    }


    @Override
    public LoaderExecutor withInputs(Map<String, Object> inputs) {
        ScriptExecutor executor = createScriptExecutor();
        executor.setInputs(inputs);
        return executor;
    }


    @Override
    public Map<String, Object> load(String loadScript, String... additionalScripts) {
        return createScriptExecutor().load(loadScript, additionalScripts);
    }


    @Override
    public Map<String, Object> load(Script script, Script... additionalScripts) {
        return createScriptExecutor().load(script, additionalScripts);
    }


    @Override
    public Map<String, Object> load(Collection<String> loadScripts) {
        return createScriptExecutor().load(loadScripts);
    }


    @Override
    public Map<String, Object> loadNamespaced(Collection<Script> loadScripts) {
        return createScriptExecutor().loadNamespaced(loadScripts);
    }
}
