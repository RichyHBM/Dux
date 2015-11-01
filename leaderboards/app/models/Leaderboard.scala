package models

import java.util.Date

import scala.beans.BeanProperty

// By using a Scala class with only vals we automatically get an immutable class that can be used in Java

final case class Leaderboard(@BeanProperty gameId: Int,
                             @BeanProperty leaderboardName: String,
                             @BeanProperty startTime: Date,
                             @BeanProperty endTime: Date,
                             @BeanProperty descending: Boolean)
