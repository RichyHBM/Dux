package utilities;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;
import java.io.IOException;

public class JavaScriptEngine {
    ScriptEngineManager manager;
    ScriptEngine engine;

    public JavaScriptEngine() {
        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
    }

    public boolean loadJs(String script) throws ScriptException {
        engine.eval(script);
        return true;
    }

    public boolean loadJsFile(String file) throws ScriptException, IOException {
        FileReader reader = new FileReader(file);
        engine.eval(reader);
        reader.close();
        return true;
    }

    public Object callFunction(String function, Object ... args) throws ScriptException, NoSuchMethodException {
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction(function, args);
    }
}
