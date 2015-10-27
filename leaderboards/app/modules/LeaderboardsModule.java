package modules;

import com.google.inject.AbstractModule;
import interfaces.*;
import implementors.*;

public class LeaderboardsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ILeaderboardProvider.class).to(RedisLeaderboardProvider.class);
        bind(ILeaderboardManager.class).to(SqlLeaderboardManager.class);
    }
}
