package net.ieldor.modules.scripts;

import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.ieldor.Constants;
import net.ieldor.Main;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

public class ScriptManager {
	
	public static final String DATA_DIR = "./data/";
	
	public static final String SCRIPT_DIR = "./data/scripts/";

	public static final String BIN_DIR = "./bin/";

	private static GroovyScriptEngine engine;

	private static final Map<String, Class<?>> scripts = new HashMap<>();

	public void initiate() {
		Main.getLogger().info("Loading Groovy Sripting System.");
		try {
			engine = new GroovyScriptEngine(new String[] { SCRIPT_DIR });

			ImportCustomizer imports = new ImportCustomizer();
			imports.addImport("Main", "net.vpk.Main");
			imports.addImport("Constants", "net.vpk.Constants");
			File importsFile = new File(DATA_DIR + "imports.txt");
			/*if (Constants.DEVELOPER_MODE) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(importsFile));
				generateImports(new File(BIN_DIR + "alterrs/game/"), imports, writer);
				generateImports(new File(BIN_DIR + "alterrs/rs751/"), imports, writer);
				writer.close();
			} else {*/
				loadImports(importsFile, imports);
			//}

			engine.getConfig().addCompilationCustomizers(imports);
		} catch (IOException e) {
			Main.getLogger().error("Failed to initialize script engine!", e);
		}
	}

	private static void generateImports(File dir, ImportCustomizer imports, BufferedWriter writer) throws IOException {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				generateImports(file, imports, writer);
				continue;
			}

			if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
				String alias = file.getName().replace(".class", "");
				String name = file.getPath().replace(File.separatorChar, '/').replace(BIN_DIR, "").replace(".class", "").replace('/', '.');
				imports.addImports(alias, name);

				writer.write(name);
				writer.newLine();
			}
		}
	}

	private static void loadImports(File file, ImportCustomizer imports) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String name;
		while ((name = reader.readLine()) != null) {
			imports.addImport(name.substring(name.lastIndexOf('.') + 1, name.length()), name);
		}
		reader.close();
	}

	@SuppressWarnings("unchecked")
	public static <S> Class<S> loadScript(String scriptName) {
		Class<S> clazz = (Class<S>) scripts.get(scriptName);
		if (clazz == null) {
			try {
				clazz = engine.loadScriptByName(scriptName.replace('.', '/') + ".groovy");
			} catch (ResourceException | ScriptException e) {
				Main.getLogger().warn("Failed to load script : " + scriptName, e);
				return null;
			}
			scripts.put(scriptName, clazz);
		}
		return clazz;
	}

	public static <S> S initScript(String scriptName, Object... args) {
		try {
			Class<S> clazz = loadScript(scriptName);

			Class<?> types[] = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				types[i] = args[i].getClass();
			}

			return clazz.getConstructor(types).newInstance(args);
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException e) {
			Main.getLogger().warn("Failed to init script : " + scriptName, e);
			return null;
		}
	}

	public static void clearCache() {
		engine.getGroovyClassLoader().clearCache();
		scripts.clear();
	}
}