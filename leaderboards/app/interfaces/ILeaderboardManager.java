package interfaces;

import models.Leaderboard;
import common.LoggedException;

import java.util.Date;
import java.util.List;

public interface ILeaderboardManager {
    boolean createLeaderboard(int gameId, String leaderboardName, Date startTime, Date endTime, boolean descending) throws LoggedException;
    boolean createLeaderboard(Leaderboard leaderboard) throws LoggedException;

    boolean editLeaderboard(int gameId, String leaderboardName, Date startTime, Date endTime) throws LoggedException;
    boolean editLeaderboard(Leaderboard leaderboard) throws LoggedException;

    boolean deleteLeaderboard(int gameId, String leaderboardName) throws LoggedException;
    boolean deleteLeaderboard(Leaderboard leaderboard) throws LoggedException;

    Leaderboard getLeaderboard(int gameId, String leaderboardName) throws LoggedException;
    List<Leaderboard> getLeaderboardsForGame(int gameId) throws LoggedException;
    List<Leaderboard> getAllLeaderboards() throws LoggedException;
}
