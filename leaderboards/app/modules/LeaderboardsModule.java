package modules;

import com.google.inject.AbstractModule;
import interfaces.*;
import providers.*;

public class LeaderboardsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ILeaderboardProvider.class).to(RedisLeaderboardProvider.class);
    }
}
