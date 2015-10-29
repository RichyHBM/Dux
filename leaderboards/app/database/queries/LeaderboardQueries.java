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

    public static String GetLeaderboardsBetweenDateForGame = String.format(
            "SELECT * FROM %s WHERE %s <= ? AND %s > ? AND %s = ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.StartTime,
            Structure.Leaderboards.Columns.EndTime,
            Structure.Leaderboards.Columns.GameId
    );

    public static String GetAllLeaderboardsBetweenDate = String.format(
            "SELECT * FROM %s WHERE %s <= ? AND %s > ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.StartTime,
            Structure.Leaderboards.Columns.EndTime
    );

    public static String CountAllLeaderboards = String.format(
            "SELECT COUNT(*) as Count FROM %s",
            Structure.Leaderboards.Name
    );

    public static String CountLeaderboardsForGame = String.format(
            "SELECT COUNT(*) as Count FROM %s WHERE %s = ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.GameId
    );

    public static String CountAllLeaderboardsBetweenDate = String.format(
            "SELECT COUNT(*) as Count FROM %s WHERE %s <= ? AND %s > ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.StartTime,
            Structure.Leaderboards.Columns.EndTime
    );

    public static String CountLeaderboardsBetweenDateForGame = String.format(
            "SELECT COUNT(*) as Count FROM %s WHERE %s <= ? AND %s > ? AND %s = ?",
            Structure.Leaderboards.Name,
            Structure.Leaderboards.Columns.StartTime,
            Structure.Leaderboards.Columns.EndTime,
            Structure.Leaderboards.Columns.GameId
    );
}
