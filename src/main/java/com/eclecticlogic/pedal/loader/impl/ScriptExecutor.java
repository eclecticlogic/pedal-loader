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

import java.io.File;
import java.io.InputStream;

import javax.persistence.EntityManager;

import com.eclecticlogic.pedal.loader.Script;

/**
 * @author kabram.
 */
public class ScriptExecutor extends AbstractScriptExecutor {

    public ScriptExecutor(EntityManager manager) {
        super(manager);
    }

    /*
     * (non-Javadoc)
     * @see com.eclecticlogic.pedal.loader.impl.AbstractScriptExecutor#getScriptStream(com.eclecticlogic.pedal.loader.Script)
     */
	@Override
	protected InputStream getScriptStream(Script script) {
        String filename = scriptDirectory == null || scriptDirectory.trim().length() == 0 ? script.getName()
                : scriptDirectory + File.separator + script.getName();
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
	}
}
