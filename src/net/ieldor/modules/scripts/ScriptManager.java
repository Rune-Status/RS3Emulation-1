package net.ieldor.modules.scripts;

import net.ieldor.modules.scripts.impl.PythonScript;

public class ScriptManager {

    public void initScripts() {
        PythonScript.loadScripts("./data/scripts/python/");
    }

}