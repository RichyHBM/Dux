package implementors;

import interfaces.ILeaderboardProvider;
import models.LeaderboardUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import redis.clients.jedis.*;

public class RedisLeaderboardProvider implements ILeaderboardProvider {

    private final JedisPool jedisPool;

    @Inject
    public RedisLeaderboardProvider(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public static String redisKey(int gameId, String leaderboardId){
        return gameId + "-" + leaderboardId;
    }

    public static String redisKeyExtra(int gameId, String leaderboardId){
        return gameId + "-" + leaderboardId + "-extra";
    }

    public long getUserCountInLeaderboard(int gameId, String leaderboardId) {
        Jedis j = jedisPool.getResource();
        try {
            String key = redisKey(gameId, leaderboardId);
            return j.zcard(key);
        } catch(Exception e) {
            return 0;
        } finally {
            jedisPool.returnResource(j);
        }
    }

    public List<LeaderboardUser> getRankedUsers(int gameId, String leaderboardId, long fromRank, int count) {
        Jedis j = jedisPool.getResource();
        try {
            List<LeaderboardUser> users = new ArrayList<LeaderboardUser>();

            Transaction t = j.multi();
            String key = redisKey(gameId, leaderboardId);

            //zrangeWithScores wants both start and end ranks inclusive, so subtract 1 from count
            Response<Set<Tuple>> rangeWithScores = t.zrangeWithScores(key, fromRank, fromRank + count - 1);
            t.exec();

            String extraKey = redisKeyExtra(gameId, leaderboardId);
            for(Tuple tuple : rangeWithScores.get()) {
                String extra = j.hget(extraKey, tuple.getElement());
                users.add(new LeaderboardUser(tuple.getElement(), fromRank, tuple.getScore(), extra));
                fromRank++;
            }
            return users;
        } catch(Exception e) {
            return null;
        }  finally {
            jedisPool.returnResource(j);
        }
    }

    public LeaderboardUser getUser(int gameId, String leaderboardId, String userId) {
        Jedis j = jedisPool.getResource();
        try {
            Transaction t = j.multi();
            String key = redisKey(gameId, leaderboardId);
            Response<Long> rank = t.zrank(key, userId);
            Response<Double> score = t.zscore(key, userId);

            String extraKey = redisKeyExtra(gameId, leaderboardId);
            Response<String> extra = t.hget(extraKey, userId);
            t.exec();
            return new LeaderboardUser(userId, rank.get(), score.get(), extra.get());
        } catch(Exception e) {
            return null;
        } finally {
            jedisPool.returnResource(j);
        }
    }

    public long getRankForUser(int gameId, String leaderboardId, String userId) {
        Jedis j = jedisPool.getResource();
        String key = redisKey(gameId, leaderboardId);
        try {
            return j.zrank(key, userId);
        } catch(Exception e) {
            try {
                return j.zcard(key);
            } catch(Exception e2){
                return 0;
            }
        } finally {
            jedisPool.returnResource(j);
        }
    }

    public double getScoreForUser(int gameId, String leaderboardId, String userId) {
        Jedis j = jedisPool.getResource();
        try {
            String key = redisKey(gameId, leaderboardId);
            return j.zscore(key, userId);
        } catch(Exception e) {
            return 0;
        } finally {
            jedisPool.returnResource(j);
        }
    }

    public boolean setScoreForUser(int gameId, String leaderboardId, String userId, double score, String extra) {
        Jedis j = jedisPool.getResource();
        try {
            String key = redisKey(gameId, leaderboardId);
            Transaction t = j.multi();
            t.zadd(key, score, userId);

            if(extra == null){
                extra = "";
            }

            String extraKey = redisKeyExtra(gameId, leaderboardId);
            t.hset(extraKey, userId, extra);

            t.exec();
            return true;
        } catch(Exception e) {
            return false;
        } finally {
            jedisPool.returnResource(j);
        }
    }

    public boolean incrementScoreForUser(int gameId, String leaderboardId, String userId, double amount, String extra) {
        Jedis j = jedisPool.getResource();
        try {
            String key = redisKey(gameId, leaderboardId);
            Transaction t = j.multi();
            t.zincrby(key, amount, userId);

            if(extra == null){
                extra = "";
            }

            String extraKey = redisKeyExtra(gameId, leaderboardId);
            t.hset(extraKey, userId, extra);

            t.exec();

            return true;
        } catch(Exception e) {
            return false;
        } finally {
            jedisPool.returnResource(j);
        }
    }

    public boolean removeUser(int gameId, String leaderboardId, String userId) {
        Jedis j = jedisPool.getResource();
        try {
            String key = redisKey(gameId, leaderboardId);
            if(j.zrem(key, userId) != 1)
                return false;

            String extraKey = redisKeyExtra(gameId, leaderboardId);
            return j.hdel(extraKey, userId) == 1;
        } catch(Exception e) {
            return false;
        } finally {
            jedisPool.returnResource(j);
        }
    }

    public boolean deleteLeaderboard(int gameId, String leaderboardId) {
        Jedis j = jedisPool.getResource();
        try {
            String key = redisKey(gameId, leaderboardId);
            j.del(key);
            String extraKey = redisKeyExtra(gameId, leaderboardId);
            j.del(extraKey);

            return true;
        } catch(Exception e) {
            return false;
        } finally {
            jedisPool.returnResource(j);
        }
    }
}
