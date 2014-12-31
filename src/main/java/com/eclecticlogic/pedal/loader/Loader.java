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
package com.eclecticlogic.pedal.loader;

import groovy.lang.Closure;

import java.util.Map;

/**
 * Data loader utility.
 * @author kabram.
 *
 */
public interface Loader extends LoaderExecutor {

    /**
     * @param directory Base directory for scripts
     * @return Fluent interface for continued loading.
     */
    public Loader withScriptDirectory(String directory);


    /**
     * @param methodName Name for custom method
     * @param closure Closure implementation for method.
     * @return fluent interface to continue loading.
     */
    public Loader withCustomMethod(String methodName, Closure<Object> closure);


    /**
     * @param inputs Objects that can be referenced (by their keys) in the load script.
     * @return fluent interface to continue loading.
     */
    public LoaderExecutor withInputs(Map<String, Object> inputs);

}
