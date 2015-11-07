package controllers;

import database.Structure;
import implementors.RedisLeaderboardProvider;
import implementors.SqlLeaderboardManager;
import interfaces.ILeaderboardManager;
import interfaces.ILeaderboardProvider;
import play.Application;
import play.db.DB;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.TestBrowser;
import redis.clients.jedis.JedisPool;
import utilities.ITestBrowser;
import utilities.FakeAppRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import static play.inject.Bindings.bind;


public class TestServerRunner {

    static class NonDependencyInjectedProvider extends RedisLeaderboardProvider {
        public NonDependencyInjectedProvider () {
            super(new JedisPool("localhost"));
        }
    }

    public static void runTest(ITestBrowser test) {
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("db.default.driver", "org.sqlite.JDBC");
        config.put("db.default.url", "jdbc:sqlite:database/test.sqlite");

        Application application = new GuiceApplicationBuilder()
                .overrides(bind(ILeaderboardProvider.class).to(NonDependencyInjectedProvider.class))
                .overrides(bind(ILeaderboardManager.class).to(SqlLeaderboardManager.class))
                .configure(config)
                .build();

        FakeAppRunner.runBrowserTestWithApplication(application, test);
    }
}
