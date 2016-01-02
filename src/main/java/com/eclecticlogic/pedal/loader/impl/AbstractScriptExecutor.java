package com.eclecticlogic.pedal.loader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;

import com.eclecticlogic.pedal.loader.LoaderExecutor;
import com.eclecticlogic.pedal.loader.Script;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

public abstract class AbstractScriptExecutor implements LoaderExecutor {

    protected String scriptDirectory;
    private Stack<ScriptContext> scriptContextStack = new Stack<>();
    private Stack<Map<String, Object>> scriptInputs = new Stack<>(); // used to pass inputs to scripts when defined using withInput

    private Map<String, Object> inputs = new HashMap<>();

    private EntityManager entityManager;
    private Map<String, Closure<Object>> customMethods;


    protected AbstractScriptExecutor(EntityManager manager) {
        this.entityManager = manager;
    }


    public void setScriptDirectory(String scriptDirectory) {
        this.scriptDirectory = scriptDirectory;
    }


    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }


    public void setCustomMethods(Map<String, Closure<Object>> customMethods) {
        this.customMethods = customMethods;
    }


    @Override
    public Map<String, Object> load(String loadScript, String... additionalScripts) {
        List<String> scripts = new ArrayList<>();
        scripts.add(loadScript);
        if (additionalScripts != null) {
            for (int i = 0; i < additionalScripts.length; i++) {
                scripts.add(additionalScripts[i]);
            }
        }

        return load(scripts);
    }


    @Override
    public Map<String, Object> load(Script script, Script... additionalScripts) {
        List<Script> scripts = new ArrayList<>();
        scripts.add(script);
        if (additionalScripts != null) {
            for (int i = 0; i < additionalScripts.length; i++) {
                scripts.add(additionalScripts[i]);
            }
        }
        return loadNamespaced(scripts);
    }


    @Override
    public Map<String, Object> load(Collection<String> loadScripts) {
        List<Script> scripts = new ArrayList<>();
        for (String script : loadScripts) {
            scripts.add(Script.script(script));
        }
        return loadNamespaced(scripts);
    }


    @Override
    public Map<String, Object> loadNamespaced(Collection<Script> scripts) {
        Map<String, Object> variables = new HashMap<>();
        // Add overall variables
        Map<String, Object> inputVariables = scriptInputs.isEmpty() ? inputs : scriptInputs.pop();

        for (Entry<String, Object> var : inputVariables.entrySet()) {
            variables.put(var.getKey(), var.getValue());
        }

        for (Script script : scripts) {
            NamespacedBinding binding = create();
            // Bind inputs.
            for (String key : variables.keySet()) {
                binding.setVariable(key, variables.get(key));
            }
            binding.startCapture();

            try (InputStream stream = getScriptStream(script)) {
                List<String> lines = IOUtils.readLines(stream);
                StringBuilder builder = new StringBuilder();
                for (String line : lines) {
                    builder.append(line).append("\n");
                }
                execute(builder.toString(), binding);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            // Add output variables
            if (script.getNamespace() == null || script.getNamespace().trim().length() == 0) {
                for (String key : binding.getNamespacedVariables().keySet()) {
                    variables.put(key, binding.getNamespacedVariables().get(key));
                }
            } else {
                variables.put(script.getNamespace(), binding.getNamespacedVariables());
            }
        }

        return variables;
    }

    /**
     * Return the proper {@link InputStream} for the specified {@link Script} 
     * based on the way the subclass is implemented.
     * 
     * @param script
     * @return
     * @throws IOException 
     */
    protected abstract InputStream getScriptStream(Script script) throws IOException;
    
    @SuppressWarnings("serial")
    private NamespacedBinding create() {
        Closure<List<Object>> table = new Closure<List<Object>>(this) {

            @SuppressWarnings("unchecked")
            @Override
            public List<Object> call(Object... args) {
                if (args == null || args.length != 3) {
                    throw new RuntimeException("The table method expects JPA entity class reference, "
                            + "list of bean properties and a closure");
                }
                return invokeWithClosure((Class<?>) args[0], (List<String>) args[1], (Closure<Void>) args[2]);
            }
        };

        Closure<Object> row = new Closure<Object>(this) {

            @Override
            public Object call(Object... args) {
                return invokeRowClosure(args);
            }
        };

        Closure<Object> defaultRow = new Closure<Object>(this) {

            @SuppressWarnings("unchecked")
            @Override
            public Object call(Object... args) {
                invokeDefaultRowClosure((Closure<Object>) args[0]);
                return null;
            }
        };

        Closure<Object> find = new Closure<Object>(this) {

            @SuppressWarnings("unchecked")
            @Override
            public Object call(Object... args) {
                return invokeFindClosure((Class<? extends Serializable>) args[0], (Serializable) args[1]);
            };
        };

        Closure<Object> withInput = new Closure<Object>(this) {

            @Override
            public Object call(Object... args) {
                return invokeWithInputClosure(args[0]);
            };
        };

        Closure<Object> load = new Closure<Object>(this) {

            @Override
            public Object call(Object... args) {
                return invokeLoadClosure(args);
            };
        };

        Closure<Object> flush = new Closure<Object>(this) {

            @Override
            public Object call(Object... args) {
                entityManager.flush();
                return null;
            };
        };

        NamespacedBinding binding = new NamespacedBinding();
        binding.setVariable("table", table);
        binding.setVariable("row", row);
        binding.setVariable("defaultRow", defaultRow);
        binding.setVariable("find", find);
        binding.setVariable("withInput", withInput);
        binding.setVariable("load", load);
        binding.setVariable("flush", flush);

        // Custom methods
        for (Entry<String, Closure<Object>> entry : customMethods.entrySet()) {
            binding.setVariable(entry.getKey(), entry.getValue());
        }
        return binding;
    }


    @SuppressWarnings("unchecked")
    public Map<String, Object> execute(String script, Binding binding) {
        GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding);
        shell.evaluate(script);
        return binding.getVariables();
    }


    private <V> List<Object> invokeWithClosure(Class<?> clz, List<String> attributes, Closure<V> callable) {
        ScriptContext context = new ScriptContext();
        context.setEntityClass(clz);
        context.setAttributes(attributes);

        scriptContextStack.push(context);

        callable.call();

        scriptContextStack.pop();
        return context.getCreatedEntities();
    }


    private void invokeDefaultRowClosure(Closure<Object> closure) {
        scriptContextStack.peek().setDefaultRowClosure(closure);
    }


    private Object invokeRowClosure(Object... attributeValues) {
        Serializable instance = instantiate();
        DelegatingGroovyObjectSupport<Serializable> delegate = new DelegatingGroovyObjectSupport<Serializable>(instance);

        for (int i = 0; i < scriptContextStack.peek().getAttributes().size(); i++) {
            delegate.setProperty(scriptContextStack.peek().getAttributes().get(i), attributeValues[i]);
        }
        scriptContextStack.peek().getDefaultRowClosure().call(instance);
        entityManager.persist(instance);
        scriptContextStack.peek().getCreatedEntities().add(instance);
        return instance;
    }


    private Object invokeFindClosure(Class<? extends Serializable> clz, Serializable id) {
        return entityManager.find(clz, id);
    }


    @SuppressWarnings("unchecked")
    private Object invokeWithInputClosure(Object arg) {
        Map<String, Object> scriptInput = (Map<String, Object>) arg;
        scriptInputs.push(scriptInput);
        // Return a constrained set of operations that are possible. Returning owner will result in the entire set
        // of methods in this class being exposed.
        return new Object() {

            @SuppressWarnings("unused")
            public Map<String, Object> load(String loadScript, String... additionalScripts) {
                return AbstractScriptExecutor.this.load(loadScript, additionalScripts);
            }


            @SuppressWarnings("unused")
            public Map<String, Object> load(Map<String, String> namespacedScripts) {
                List<Script> scripts = new ArrayList<>();
                for (Entry<String, String> entry : namespacedScripts.entrySet()) {
                    scripts.add(Script.with(entry.getValue(), entry.getKey()));
                }
                return AbstractScriptExecutor.this.loadNamespaced(scripts);
            }
        };
    }


    @SuppressWarnings("unchecked")
    private Object invokeLoadClosure(Object[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("The load() method should be called with a map of namespace and "
                    + "script names or a list of one or more script names.");
        } else if (args[0] instanceof Map) {
            Map<String, String> scriptMap = (Map<String, String>) args[0];
            List<Script> scripts = new ArrayList<>();
            for (String namespace : scriptMap.keySet()) {
                scripts.add(Script.with(scriptMap.get(namespace), namespace));
            }
            return loadNamespaced(scripts);
        } else {
            // Assuming these are simply script names.
            List<String> scripts = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                scripts.add((String) args[i]);
            }
            return load(scripts);
        }
    }


    private Serializable instantiate() {
        try {
            return (Serializable) scriptContextStack.peek().getEntityClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
