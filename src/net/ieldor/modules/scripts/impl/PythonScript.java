package net.ieldor.modules.scripts.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import net.ieldor.modules.scripts.Script;

public class PythonScript extends Scripts {

    @Override
    public void loadScripts(String path) throws IOException {
        BufferedReader scriptList = new BufferedReader(new FileReader(path + "list.txt/");
        String[] scriptNames;
        while ((String tmp = scriptList.readLine()) != null) {
            //scriptNames = tmp;
            System.out.println(tmp);
        }
    }

}