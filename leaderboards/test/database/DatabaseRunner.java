package database;

import utilities.FakeAppRunner;
import utilities.ITest;

import java.util.HashMap;

public class DatabaseRunner {
    public static void runDatabaseTests(ITest test) {
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("db.default.driver", "org.sqlite.JDBC");
        config.put("db.default.url", "jdbc:sqlite:database/test.sqlite");

        FakeAppRunner.runTest(test, config);
    }
}
