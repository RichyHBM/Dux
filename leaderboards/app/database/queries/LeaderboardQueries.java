package database.queries;

import database.Structure;

public class LeaderboardQueries {
    public static String InsertLeaderboard = String.format(
            "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.GameId,
            Structure.Leaderboards.Columns.LeaderboardName,
            Structure.Leaderboards.Columns.StartTime,
            Structure.Leaderboards.Columns.EndTime,
            Structure.Leaderboards.Columns.Descending
    );

    public static String ModifyLeaderboardByGameLeaderboard = String.format(
            "UPDATE %s SET %s=?, %s=? WHERE %s = ? AND %s = ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.StartTime,
            Structure.Leaderboards.Columns.EndTime,
            Structure.Leaderboards.Columns.GameId,
            Structure.Leaderboards.Columns.LeaderboardName
    );

    public static String DeleteLeaderboardByGameLeaderboard = String.format(
            "DELETE FROM %s WHERE %s = ? AND %s = ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.GameId,
            Structure.Leaderboards.Columns.LeaderboardName
    );

    public static String GetLeaderboardByGameLeaderboard = String.format(
            "SELECT * FROM %s WHERE %s = ? AND %s = ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.GameId,
            Structure.Leaderboards.Columns.LeaderboardName
    );

    public static String GetLeaderboardsForGame = String.format(
            "SELECT * FROM %s WHERE %s = ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.GameId
    );

    public static String GetAllLeaderboards = String.format(
            "SELECT * FROM %s",
            Structure.Leaderboards.Name
    );
}
