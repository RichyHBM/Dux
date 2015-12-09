package database;

import play.db.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

public class LeaderboardModify {
    public static boolean createLeaderboard(int gameId, String leaderboardName, Date startTime, Date endTime, boolean descending) throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.InsertLeaderboard);
            stm.setInt(1, gameId);
            stm.setString(2, leaderboardName);
            stm.setLong(3, startTime == null ? -1 : startTime.getTime());
            stm.setLong(4, endTime == null ? -1 : endTime.getTime());
            stm.setInt(5, descending ? 1 : 0);

            return stm.executeUpdate() == 1;
        }
    }

    public static boolean editLeaderboard(int gameId, String leaderboardName, Date startTime, Date endTime) throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.ModifyLeaderboardByGameLeaderboard);
            stm.setLong(1, startTime == null ? -1 : startTime.getTime());
            stm.setLong(2, endTime == null ? -1 : endTime.getTime());
            stm.setInt(3, gameId);
            stm.setString(4, leaderboardName);

            return stm.executeUpdate() == 1;
        }
    }

    public static boolean deleteLeaderboard(int gameId, String leaderboardName) throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.DeleteLeaderboardByGameLeaderboard);
            stm.setInt(1, gameId);
            stm.setString(2, leaderboardName);

            return stm.executeUpdate() == 1;
        }
    }

}
