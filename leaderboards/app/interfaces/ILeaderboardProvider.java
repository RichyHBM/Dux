package interfaces;

import models.LeaderboardUser;
import java.util.List;

public interface ILeaderboardProvider {
    long getUserCountInLeaderboard(int gameId, String leaderboardId);

    List<LeaderboardUser> getRankedUsers(int gameId, String leaderboardId, boolean descending, long fromRank, int count);
    LeaderboardUser getUser(int gameId, String leaderboardId, boolean descending, String userId);
    long getRankForUser(int gameId, String leaderboardId, boolean descending, String userId);
    double getScoreForUser(int gameId, String leaderboardId, String userId);

    boolean setScoreForUser(int gameId, String leaderboardId, String userId, double score, String extra);
    boolean incrementScoreForUser(int gameId, String leaderboardId, String userId, double amount, String extra);

    boolean removeUser(int gameId, String leaderboardId, String userId);
    boolean deleteLeaderboard(int gameId, String leaderboardId);
}
