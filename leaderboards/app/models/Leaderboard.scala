package models

import java.util.Date

// By using a Scala class with only vals we automatically get an immutable class that can be used in Java

final case class Leaderboard(gameId: Int,
                             leaderboardName: String,
                             startTime: Date,
                             endTime: Date,
                             descending: Boolean)
