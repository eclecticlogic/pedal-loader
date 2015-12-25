package com.eclecticlogic.pedal.loader.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.eclecticlogic.pedal.loader.Loader;
import com.eclecticlogic.pedal.loader.LoaderExecutor;
import com.eclecticlogic.pedal.loader.Script;

import groovy.lang.Closure;

/**
 * 
 * Abstract implementation for all of the {@link Loader} implementations
 * 
 * @author kabram.
 * @author Craig Setera
 *
 */
public abstract class AbstractLoaderImpl implements Loader {

    public AbstractLoaderImpl() {
        super();
    }

    public AbstractLoaderImpl(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

	protected String scriptDirectory;
	protected Map<String, Closure<Object>> customMethods = new HashMap<>();
	
	@PersistenceContext
	protected EntityManager entityManager;

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

	@Override
	public LoaderExecutor withInputs(Map<String, Object> inputs) {
		AbstractScriptExecutor executor = createScriptExecutor();
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

	protected abstract AbstractScriptExecutor createScriptExecutor();
}
