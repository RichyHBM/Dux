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

    public static List<Leaderboard> getActiveLeaderboardsForGame(int gameId) throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.GetLeaderboardsBetweenDateForGame);

            long now = new Date().getTime();
            stm.setLong(1, now);
            stm.setLong(2, now);
            stm.setInt(3, gameId);

            ResultSet resultSet = stm.executeQuery();
            List<Leaderboard> leaderboards = new ArrayList<>();

            while(resultSet.next()) {
                leaderboards.add(Parser.toLeaderboard(resultSet));
            }

            return leaderboards;
        }
    }

    public static List<Leaderboard> getAllActiveLeaderboards() throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.GetAllLeaderboardsBetweenDate);

            long now = new Date().getTime();
            stm.setLong(1, now);
            stm.setLong(2, now);

            ResultSet resultSet = stm.executeQuery();
            List<Leaderboard> leaderboards = new ArrayList<>();

            while(resultSet.next()) {
                leaderboards.add(Parser.toLeaderboard(resultSet));
            }

            return leaderboards;
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

    public static long countAllLeaderboards() throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.CountAllLeaderboards);

            ResultSet resultSet = stm.executeQuery();

            if(resultSet.next()) {
                return resultSet.getLong("Count");
            }

            return 0;
        }
    }

    public static long countLeaderboardsForGame(int gameId) throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.CountLeaderboardsForGame);
            stm.setInt(1, gameId);

            ResultSet resultSet = stm.executeQuery();

            if(resultSet.next()) {
                return resultSet.getLong("Count");
            }

            return 0;
        }
    }

    public static long countAllActiveLeaderboards() throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.CountAllLeaderboardsBetweenDate);

            long now = new Date().getTime();
            stm.setLong(1, now);
            stm.setLong(2, now);

            ResultSet resultSet = stm.executeQuery();

            if(resultSet.next()) {
                return resultSet.getLong("Count");
            }

            return 0;
        }
    }

    public static long countActiveLeaderboardsForGame(int gameId) throws Exception {
        try (Connection conn = DB.getConnection()) {
            PreparedStatement stm = conn.prepareStatement(LeaderboardQueries.CountLeaderboardsBetweenDateForGame);

            long now = new Date().getTime();
            stm.setLong(1, now);
            stm.setLong(2, now);
            stm.setInt(3, gameId);

            ResultSet resultSet = stm.executeQuery();

            if(resultSet.next()) {
                return resultSet.getLong("Count");
            }

            return 0;
        }
    }
}
