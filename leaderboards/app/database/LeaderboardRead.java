package database;

import database.queries.LeaderboardQueries;
import play.db.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Leaderboard;

public class LeaderboardRead {
    public static Leaderboard getLeaderboard(int gameId, String leaderboardName) throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.GetLeaderboardByGameLeaderboard);
            stm.setInt(1, gameId);
            stm.setString(2, leaderboardName);

            ResultSet resultSet = stm.executeQuery();

            if(resultSet.next()) {
                return Parser.toLeaderboard(resultSet);
            }

            return null;
        }
    }

    public static List<Leaderboard> getLeaderboardsForGame(int gameId) throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.GetLeaderboardsForGame);
            stm.setInt(1, gameId);

            ResultSet resultSet = stm.executeQuery();
            List<Leaderboard> leaderboards = new ArrayList<>();

            while(resultSet.next()) {
                leaderboards.add(Parser.toLeaderboard(resultSet));
            }

            return leaderboards;
        }
    }

    public static List<Leaderboard> getAllLeaderboards() throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.GetAllLeaderboards);

            ResultSet resultSet = stm.executeQuery();
            List<Leaderboard> leaderboards = new ArrayList<>();

            while(resultSet.next()) {
                leaderboards.add(Parser.toLeaderboard(resultSet));
            }

            return leaderboards;
        }
    }
}
