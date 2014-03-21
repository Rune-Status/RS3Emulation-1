package net.ieldor.modules.scripts;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hadyn Richard
 */
public final class RubyScriptEnvironment {

    /**
     * The script engine manager for all the script environments.
     */
    private static final ScriptEngineManager manager = new ScriptEngineManager();

    /**
     * The collection of scripts that have been compiled.
     */
    private Map<String, CompiledScript> scripts;

    /**
     * The script engine for this environment.
     */
    private ScriptEngine engine;

    /**
     * Constructs a new {@link RubyScriptEnvironment};
     */
    public RubyScriptEnvironment() {
        scripts = new HashMap<>();
        engine = manager.getEngineByName("jruby");
        if(engine == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Sets the context of the script environment.
     *
     * @param context   The environment context.
     */
    public void setContext(ScriptContext context) {
        engine.getBindings(javax.script.ScriptContext.GLOBAL_SCOPE).put("ctx", context);
    }

    /**
     * Evalulates a script, the script must have been previously compiled.
     *
     * @param name              The name of the script to evaluate.
     * @throws ScriptException  A script exception was thrown while evaluating the script.
     */
    public void eval(String name) throws ScriptException {
        CompiledScript compiledScript = scripts.get(name);
        if(compiledScript == null) {
            throw new RuntimeException("script does not exist");
        }
        compiledScript.eval();
    }

    /**
     * Loads a script.
     *
     * @param script            The script to load.
     * @throws ScriptException  A script exception was thrown while evaluating the script.
     */
    public void load(Script script) throws ScriptException {
        Compilable compilable = (Compilable) engine;
        CompiledScript compiledScript = compilable.compile(script.getSource());
        scripts.put(script.getName(), compiledScript);
    }

    /**
     * Evaluates a script.
     *
     * @param script            The script to evaluate.
     * @throws ScriptException  A script exception was thrown while evaluating the script.
     */
    public void eval(Script script) throws ScriptException {
        if(scripts.containsKey(script.getName())) {
            eval(script.getName());
        } else {
            Compilable compilable = (Compilable) engine;
            CompiledScript compiledScript = compilable.compile(script.getSource());
            scripts.put(script.getName(), compiledScript);
            compiledScript.eval();
        }
    }
}