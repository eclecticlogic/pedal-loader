package com.eclecticlogic.pedal.loader.impl;

import javax.persistence.EntityManager;

import com.eclecticlogic.pedal.loader.Loader;
import com.eclecticlogic.pedal.loader.Script;

/**
 * Implementation of the {@link Loader} interface that finds the
 * referred {@link Script}'s from the file system rather than
 * the classpath.
 * 
 * @author Craig Setera
 */
public class FileSystemLoaderImpl extends AbstractLoaderImpl {

	public FileSystemLoaderImpl() {
		super();
	}

	public FileSystemLoaderImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected AbstractScriptExecutor createScriptExecutor() {
		FileSystemScriptExecutor executor = new FileSystemScriptExecutor(entityManager);
	    executor.setScriptDirectory(scriptDirectory);
	    executor.setCustomMethods(customMethods);
	    return executor;
	}
}
