package models;

import scala.beans.BeanProperty

// By using a Scala class with only vals we automatically get an immutable class that can be used in Java

final case class LeaderboardUser(@BeanProperty userId: String,
                                 @BeanProperty rank: Long,
                                 @BeanProperty score: Double,
                                 @BeanProperty extra: String)
