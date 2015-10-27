CREATE TABLE Leaderboards (
    Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
    GameId INTEGER NOT NULL,
    LeaderboardName TEXT NOT NULL,
    StartTime INTEGER,
    EndTime INTEGER,
    Descending NUMERIC DEFAULT 0,
    UNIQUE(GameId, LeaderboardName)
);