/** 
 *  Copyright (c) 2011-2014-2015 Eclectic Logic LLC. 
 *  All rights reserved. 
 *   
 *  This software is the confidential and proprietary information of 
 *  Eclectic Logic LLC ("Confidential Information").  You shall 
 *  not disclose such Confidential Information and shall use it only
 *  in accordance with the terms of the license agreement you entered 
 *  into with Eclectic Logic LLC.
 *
 **/
package com.eclecticlogic.pedal.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eclecticlogic.pedal.loader.dm.SimpleType;
import com.eclecticlogic.pedal.loader.impl.FileSystemLoaderImpl;

import groovy.lang.Closure;

/**
 * @author kabram.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JpaConfiguration.class)
public class TestLoader {
	private static final String[] SCRIPT_FILE_NAMES = new String[] {
		"customMethod.loader.groovy",
		"input.reader.groovy",
		"simple.loader.groovy",
		"test.loader.groovy"
	};
	
    @Autowired
    private Loader loader;

    @Autowired
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
    @Test
    @Transactional
    public void testLoaderWithNamespaces() {
        Map<String, Object> variables = loader //
                .withScriptDirectory("loader") //
                .load(Script.with("simple.loader.groovy", "a"), Script.with("simple.loader.groovy", "b"));
        Map<String, Object> avars = (Map<String, Object>) variables.get("a");
        Assert.assertEquals(((SimpleType) avars.get("simple1")).getAmount(), 10);
        Map<String, Object> bvars = (Map<String, Object>) variables.get("b");
        Assert.assertEquals(((SimpleType) bvars.get("simple2")).getAmount(), 20);
    }


    @SuppressWarnings("unchecked")
    @Test
    @Transactional
    public void testLoadWithinLoadScript() {
        Map<String, Object> variables = loader
    		.withScriptDirectory("loader") 
            .load("test.loader.groovy");
        Map<String, Object> output = (Map<String, Object>) variables.get("output");
        Map<String, Object> avars = (Map<String, Object>) output.get("a");
        Assert.assertEquals(((SimpleType) avars.get("simple1")).getAmount(), 10);
    }

    @SuppressWarnings("unchecked")
    @Test
    @Transactional
    public void testLoadFromFileSystem() 
    	throws IOException 
    {
    	File tempFolder = copyScriptsToFilesystem();
    	
    	Loader fileSystemLoader = 
    		new FileSystemLoaderImpl(entityManager);
    	
        Map<String, Object> variables = fileSystemLoader
    		.withScriptDirectory(tempFolder.getAbsolutePath()) 
            .load("test.loader.groovy");
        Map<String, Object> output = (Map<String, Object>) variables.get("output");
        Map<String, Object> avars = (Map<String, Object>) output.get("a");
        Assert.assertEquals(((SimpleType) avars.get("simple1")).getAmount(), 10);
    }

    @SuppressWarnings("serial")
    @Test
    @Transactional
    public void testCustomClosures() {
        Map<String, Object> variables = loader //
                .withCustomMethod("doubler", new Closure<Object>(this) {

                    @Override
                    public Object call(Object... args) {
                        Integer i = (Integer) args[0];
                        return i * 2;
                    }
                }).withScriptDirectory("loader") //
                .load("customMethod.loader.groovy");
        Assert.assertEquals(variables.get("myvar"), 400);
    }

    /**
     * Copy the Pedal Loader test scripts from the classloader
     * to the file system.
     * 
     * @return
     * @throws IOException
     */
	private File copyScriptsToFilesystem() 
		throws IOException 
	{
    	// Copy the test scripts to a temporary location on the file system...
    	File tempFolder = File.createTempFile("pedalloader_", ".tst");
    	tempFolder.delete();
    	tempFolder.mkdirs();
    	
    	for (String name : SCRIPT_FILE_NAMES) {
    		File outputFile = new File(tempFolder, name);
    		
    		try (InputStream scriptStream = getClass().getClassLoader().getResourceAsStream("loader/" + name)) {
    			try (FileOutputStream fos = new FileOutputStream(outputFile)) {
    				IOUtils.copy(scriptStream, fos);
    			}
    		}
    	}
    	
		return tempFolder;
	}
}
