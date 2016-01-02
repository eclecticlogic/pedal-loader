/**
 * Copyright (c) 2014-2015 Eclectic Logic LLC
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

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;

import com.eclecticlogic.pedal.loader.Loader;

/**
 * Data loader entry class.
 * 
 * @author kabram.
 *
 */
@ManagedBean
public class LoaderImpl extends AbstractLoaderImpl implements Loader {

    public LoaderImpl() {
        super();
    }

    public LoaderImpl(EntityManager entityManager) {
        super(entityManager);
    }
    
    @Override
	protected ScriptExecutor createScriptExecutor() {
	    ScriptExecutor executor = new ScriptExecutor(entityManager);
	    executor.setScriptDirectory(scriptDirectory);
	    executor.setCustomMethods(customMethods);
	    return executor;
	}
}
