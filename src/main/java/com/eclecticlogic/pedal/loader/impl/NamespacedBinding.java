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

import groovy.lang.Binding;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kabram.
 *
 */
public class NamespacedBinding extends Binding {

    private boolean capture;
    private Map<String, Object> namespacedVariables = new HashMap<>();


    public void startCapture() {
        capture = true;
    }


    @Override
    public void setVariable(String name, Object value) {
        if (capture) {
            namespacedVariables.put(name, value);
        }
        super.setVariable(name, value);
    }


    public Map<String, Object> getNamespacedVariables() {
        return namespacedVariables;
    }

}
