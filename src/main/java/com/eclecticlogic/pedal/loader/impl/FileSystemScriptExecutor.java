package com.eclecticlogic.pedal.loader.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.EntityManager;

import com.eclecticlogic.pedal.loader.Script;

/**
 * Implementation of the {@link ScriptExecutor} interface that
 * retrieves the script contents from the local file system.
 * 
 * @author Craig Setera
 *
 */
public class FileSystemScriptExecutor extends AbstractScriptExecutor {

	protected FileSystemScriptExecutor(EntityManager manager) {
		super(manager);
	}

	@Override
	protected InputStream getScriptStream(Script script) 
		throws IOException 
	{
		File scriptSource = null;
		if (scriptDirectory == null || scriptDirectory.trim().length() == 0) {
			scriptSource = new File(script.getName());
		} else {
			File directory = new File(scriptDirectory.trim());
			scriptSource = new File(directory, script.getName());
		}

        return new FileInputStream(scriptSource);
	}
}
