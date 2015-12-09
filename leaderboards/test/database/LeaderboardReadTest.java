package database;

import models.Leaderboard;
import org.junit.Before;
import org.junit.Test;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class LeaderboardReadTest {

    Leaderboard[] testLeaderboards = new Leaderboard[]{
            new Leaderboard(11, "test1", null, new Date( new Date().getTime() - 500 ), true),
            new Leaderboard(11, "test2", new Date( new Date().getTime() - 50000 ), new Date( new Date().getTime() + 50000 ), true),
            new Leaderboard(13, "test3", new Date(), null, false),
            new Leaderboard(14, "test4", new Date( new Date().getTime() - 1000 ), new Date( new Date().getTime() + 50000 ), true),
            new Leaderboard(15, "test5", null, null, false)
    };

    @Before
    public void setUp() throws SQLException {
        DatabaseRunner.runDatabaseTests( () -> {
            try (Connection conn = DB.getConnection()) {
                PreparedStatement stm = conn.prepareStatement(String.format("DELETE FROM %s", Structure.Leaderboards.Name));
                stm.executeUpdate();

                stm = conn.prepareStatement(LeaderboardQueries.InsertLeaderboard);

                for(int i = 0; i < testLeaderboards.length; i++) {
                    stm.clearParameters();
                    stm.setInt(1, testLeaderboards[i].gameId());
                    stm.setString(2, testLeaderboards[i].leaderboardName());
                    stm.setLong(3, testLeaderboards[i].startTime() == null ? -1 : testLeaderboards[i].startTime().getTime());
                    stm.setLong(4, testLeaderboards[i].endTime() == null ? -1 : testLeaderboards[i].endTime().getTime());
                    stm.setInt(5, testLeaderboards[i].descending() ? 1 : 0);

                    stm.executeUpdate();
                }
            }
        });
    }

    @Test
    public void getLeaderboardTest() throws Exception {
        DatabaseRunner.runDatabaseTests( () -> {
            for(int i = 0; i < testLeaderboards.length; i++) {
                Leaderboard l = LeaderboardRead.getLeaderboard(testLeaderboards[i].gameId(), testLeaderboards[i].leaderboardName());
                assertNotNull("Shits null " + i, l);
                assertEquals(testLeaderboards[i].gameId(), l.gameId());
                assertEquals(testLeaderboards[i].leaderboardName(), l.leaderboardName());
                assertEquals(testLeaderboards[i].startTime(), l.startTime());
                assertEquals(testLeaderboards[i].endTime(), l.endTime());
                assertEquals(testLeaderboards[i].descending(), l.descending());
            }

        });
    }

    @Test
    public void getNonExistentLeaderboardTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            try {
                Leaderboard l = LeaderboardRead.getLeaderboard(123, "test");
                assertNull(l);
            } catch (Exception e) {
                assertTrue("Non existent leaderboard should return null", false);
            }
        });
    }

    @Test
    public void getLeaderboardsForGameTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            List<Leaderboard> l = LeaderboardRead.getLeaderboardsForGame(11);
            assertEquals("test1", l.get(0).leaderboardName());
            assertEquals("test2", l.get(1).leaderboardName());
            assertEquals(2, l.size());
        });
    }

    @Test
    public void getNonExistentLeaderboardsForGameTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            List<Leaderboard> l = LeaderboardRead.getLeaderboardsForGame(15431);
            assertEquals(0, l.size());
        });
    }

    @Test
    public void getAllLeaderboardsTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            List<Leaderboard> l = LeaderboardRead.getAllLeaderboards();
            assertEquals("test1", l.get(0).leaderboardName());
            assertEquals("test2", l.get(1).leaderboardName());
            assertEquals("test3", l.get(2).leaderboardName());
            assertEquals("test4", l.get(3).leaderboardName());
            assertEquals("test5", l.get(4).leaderboardName());
            assertEquals(5, l.size());
        });
    }

    @Test
    public void getActiveLeaderboardsForGameTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            List<Leaderboard> l = LeaderboardRead.getActiveLeaderboardsForGame(11);
            assertEquals("test2", l.get(0).leaderboardName());
            assertEquals(1, l.size());
        });
    }

    @Test
    public void getNonExistentActiveLeaderboardsForGameTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            List<Leaderboard> l = LeaderboardRead.getActiveLeaderboardsForGame(15431);
            assertEquals(0, l.size());
        });
    }

    @Test
    public void getAllActiveLeaderboardsTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            List<Leaderboard> l = LeaderboardRead.getAllActiveLeaderboards();
            assertEquals("test2", l.get(0).leaderboardName());
            assertEquals("test4", l.get(1).leaderboardName());
            assertEquals(2, l.size());
        });
    }

    @Test
    public void countAllLeaderboards() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertEquals(testLeaderboards.length, LeaderboardRead.countAllLeaderboards());
        });
    }

    @Test
    public void countLeaderboardsForNonExsistentGame() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertEquals(0, LeaderboardRead.countLeaderboardsForGame(123));
        });
    }

    @Test
    public void countLeaderboardsForGame() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertEquals(2, LeaderboardRead.countLeaderboardsForGame(11));
        });
    }

    @Test
    public void countAllActiveLeaderboards() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertEquals(2, LeaderboardRead.countAllActiveLeaderboards());
        });
    }

    @Test
    public void countActiveLeaderboardsForNonExsistentGame() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertEquals(0, LeaderboardRead.countActiveLeaderboardsForGame(123));
        });
    }

    @Test
    public void countActiveLeaderboardsForGame() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertEquals(1, LeaderboardRead.countActiveLeaderboardsForGame(11));
        });
    }
}
