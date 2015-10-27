package database;

import models.Leaderboard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Parser {
    public static Leaderboard toLeaderboard(ResultSet resultSet) throws SQLException {
        int gameId = resultSet.getInt(Structure.Leaderboards.Columns.GameId);
        String leaderboardName = resultSet.getString(Structure.Leaderboards.Columns.LeaderboardName);

        long startTime = resultSet.getLong(Structure.Leaderboards.Columns.StartTime);
        long endTime = resultSet.getLong(Structure.Leaderboards.Columns.EndTime);

        boolean descending = resultSet.getInt(Structure.Leaderboards.Columns.Descending) == 1;

        return new Leaderboard(
                gameId,
                leaderboardName,
                startTime == -1 ? null : new Date(startTime),
                endTime == -1 ? null : new Date(endTime),
                descending
        );
    }
}
