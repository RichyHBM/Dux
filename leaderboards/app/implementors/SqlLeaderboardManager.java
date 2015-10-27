package implementors;

import database.LeaderboardModify;
import database.LeaderboardRead;
import interfaces.ILeaderboardManager;
import models.Leaderboard;
import common.LoggedException;

import java.util.Date;
import java.util.List;

public class SqlLeaderboardManager implements ILeaderboardManager {

    @Override
    public boolean createLeaderboard(int gameId, String leaderboardName, Date startTime, Date endTime, boolean descending) throws LoggedException {
        try {
            return LeaderboardModify.createLeaderboard(gameId, leaderboardName, startTime, endTime, descending);
        } catch (Exception e) {
            throw new LoggedException("Error creating new leaderboard", e.getMessage());
        }
    }

    @Override
    public boolean createLeaderboard(Leaderboard leaderboard) throws LoggedException {
        return createLeaderboard(
                leaderboard.gameId(),
                leaderboard.leaderboardName(),
                leaderboard.startTime(),
                leaderboard.endTime(),
                leaderboard.descending());
    }

    @Override
    public boolean editLeaderboard(int gameId, String leaderboardName, Date startTime, Date endTime) throws LoggedException {
        try {
            return LeaderboardModify.editLeaderboard(gameId, leaderboardName, startTime, endTime);
        } catch (Exception e) {
            throw new LoggedException("Error modifying leaderboard", e.getMessage());
        }
    }

    @Override
    public boolean editLeaderboard(Leaderboard leaderboard) throws LoggedException {
        return editLeaderboard(
                leaderboard.gameId(),
                leaderboard.leaderboardName(),
                leaderboard.startTime(),
                leaderboard.endTime());
    }

    @Override
    public boolean deleteLeaderboard(int gameId, String leaderboardName) throws LoggedException {
        try {
            return LeaderboardModify.deleteLeaderboard(gameId, leaderboardName);
        } catch (Exception e) {
            throw new LoggedException("Error deleting leaderboard", e.getMessage());
        }
    }

    @Override
    public boolean deleteLeaderboard(Leaderboard leaderboard) throws LoggedException {
        return deleteLeaderboard(leaderboard.gameId(), leaderboard.leaderboardName());
    }

    @Override
    public Leaderboard getLeaderboard(int gameId, String leaderboardName) throws LoggedException {
        try {
            return LeaderboardRead.getLeaderboard(gameId, leaderboardName);
        } catch (Exception e) {
            throw new LoggedException("Error getting leaderboard", e.getMessage());
        }
    }

    @Override
    public List<Leaderboard> getLeaderboardsForGame(int gameId) throws LoggedException {
        try {
            return LeaderboardRead.getLeaderboardsForGame(gameId);
        } catch (Exception e) {
            throw new LoggedException("Error getting leaderboards for game", e.getMessage());
        }
    }

    @Override
    public List<Leaderboard> getAllLeaderboards() throws LoggedException {
        try {
            return LeaderboardRead.getAllLeaderboards();
        } catch (Exception e) {
            throw new LoggedException("Error getting all leaderboards", e.getMessage());
        }
    }
}
