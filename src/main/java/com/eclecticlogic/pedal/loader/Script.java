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

/**
 * Defines a namespaced script.
 * @author kabram.
 *
 */
public class Script {

    private String name;
    private String namespace;


    /**
     * @param name Name of the script to load.
     * @param namespace Namespace of the script to load.
     * @return Script instance.
     */
    public static Script with(String name, String namespace) {
        Script script = new Script();
        script.name = name;
        script.namespace = namespace;
        return script;
    }


    public static Script script(String name) {
        return with(name, null);
    }


    public String getName() {
        return name;
    }


    public String getNamespace() {
        return namespace;
    }

}
