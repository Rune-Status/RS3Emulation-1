package net.ieldor.modules.scripts;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;

/**
 * Created by Hadyn Richard
 */
public final class ScriptManager {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(ScriptManager.class);

    /**
     * The JSON factory to create new JSON parsers with.
     */
    private final JsonFactory factory = new JsonFactory();

    /**
     * The ruby script environment for the plugin loader.
     */
    private RubyScriptEnvironment scriptEnvironment = new RubyScriptEnvironment();

    /**
     * The map that contains all the parsed plugin data.
     */
    private final Map<String, ScriptData> parsedPluginData = new HashMap<>();

    /**
     * The set of plugins that have had their scripts loaded.
     */
    private Set<String> loadedPlugins = new HashSet<>();

    /**
     * Constructs a new {@link PluginLoader};
     */
    public ScriptManager() {}

    /**
     * Sets the context for the script environment.
     * @param context The context for the environment.
     */
    public void setContext(ScriptContext context) {
        scriptEnvironment.setContext(context);
    }

    /**
     * Loads all the plugins from the specified directory.
     */
    public void load(String dir) throws IOException, ScriptException {
        load(new File(dir));
    }

    /**
     * Loads all the plugins from the specified file directory.
     */
    public void load(File dir) throws IOException, ScriptException {
        scriptEnvironment.eval(new Script(new File(dir, "bootstrap.rb")));
        for(File file : dir.listFiles()) {
            
            /* Skip over non-directory files */
            if(!file.isDirectory()) {
                continue;
            }

            File dataFile = new File(file, "plugin.json");

            /* Check to see if the json data file exists */
            if(!dataFile.exists()) {
                logger.warn("missing plugin.json file from '" + file.getName() + "' plugin");
                return;
            }

            JsonParser parser = factory.createJsonParser(dataFile);

            /* Check to see if the JSON structure start is correct */
            if(parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException();
            }

            /* Load the plugin data from the JSON file */
            ScriptData pluginData = new ScriptData();
            while(parser.nextToken() != JsonToken.END_OBJECT) {
                String currentName = parser.getCurrentName();
                switch(currentName.toLowerCase()) {
                    case "scripts":
                        /* Check to see if the next token is valid */
                        if(parser.nextToken() != JsonToken.START_ARRAY) {
                            throw new IOException();
                        }
                        
                        while(parser.nextToken() != JsonToken.END_ARRAY) {
                            pluginData.addScript(parser.getText());
                        }
                        break;

                    case "dependencies":
                        /* Check to see if the next token is valid */
                        if(parser.nextToken() != JsonToken.START_ARRAY) {
                            throw new IOException();
                        }

                        while(parser.nextToken() != JsonToken.END_ARRAY) {
                            pluginData.addDependency(parser.getText());
                        }
                        break;
                }
            }
            parsedPluginData.put(file.getName(), pluginData);
        }

        /* Load each of the plugins from its data */
        for(Entry<String, ScriptData> entry : parsedPluginData.entrySet()) {

            /* Check if the plugin has already been loaded */
            if(loadedPlugins.contains(entry.getKey())) {
                continue;
            }

            loadPlugin(dir, entry.getKey(), entry.getValue());
        }

        logger.info("PluginLoader loaded " + loadedPlugins.size() + " plugins...");
    }

    /**
     * Loads a plugin from the specified root directory, name, and plugin data.
     */
    private void loadPlugin(File dir, String name, Data data) throws IOException, ScriptException {
        /* Check if the plugin has already been loaded */
        if(loadedPlugins.contains(name)) {
            return;
        }

        /* Load all of the dependencies first */
        for(String pluginName : data.getDependencies()) {

            /* Check if the dependency is valid */
            if(!parsedPluginData.containsKey(pluginName)) {
                continue;
            }

            loadPlugin(dir, pluginName, parsedPluginData.get(pluginName));
        }
        
        File scriptDir = new File(dir, name);

        /* Evaluate each of the scripts */
        for(String scriptName : data.getScripts()) {
            scriptEnvironment.eval(new Script(new File(scriptDir, scriptName)));
        }

        /* Note that the plugin has been loaded */
        loadedPlugins.add(name);
    }
}