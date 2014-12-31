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

import groovy.lang.Closure;

import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eclecticlogic.pedal.loader.dm.SimpleType;

/**
 * @author kabram.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JpaConfiguration.class)
public class TestLoader {

    @Autowired
    private Loader loader;


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
        Map<String, Object> variables = loader.withScriptDirectory("loader") //
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

}
