package modules

import com.google.inject.AbstractModule
import interfaces._
import implementors._

case class LeaderboardsModule() extends AbstractModule {
    override def configure() {
        bind(classOf[ILeaderboardProvider]).to(classOf[RedisLeaderboardProvider])
        bind(classOf[ILeaderboardManager]).to(classOf[SqlLeaderboardManager])
    }
}
