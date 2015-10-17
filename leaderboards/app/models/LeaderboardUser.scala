package models;

// By using a Scala class with only vals we automatically get an immutable class that can be used in Java

final case class LeaderboardUser(userId: String, rank: Long, score: Double, extra: String)
