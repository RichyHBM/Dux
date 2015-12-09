package database;

import models.Leaderboard;
import org.junit.Before;
import org.junit.Test;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.*;

public class LeaderboardModifyTest {

    Leaderboard[] testLeaderboards = new Leaderboard[]{
            new Leaderboard(11, "test1", null, new Date(), true),
            new Leaderboard(12, "test2", new Date(), new Date(), true),
            new Leaderboard(13, "test3", new Date(), null, false),
            new Leaderboard(14, "test4", new Date(), new Date(), true),
            new Leaderboard(15, "test5", null, null, false)
    };

    @Before
    public void setUp() throws SQLException {
        DatabaseRunner.runDatabaseTests(() -> {
            try (Connection conn = DB.getConnection()) {
                PreparedStatement stm = conn.prepareStatement(String.format("DELETE FROM %s", Structure.Leaderboards.Name));
                stm.executeUpdate();

                stm = conn.prepareStatement(LeaderboardQueries.InsertLeaderboard);

                for (int i = 0; i < testLeaderboards.length; i++) {
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
    public void createLeaderboardTest() throws Exception {
        DatabaseRunner.runDatabaseTests( () -> {
            Leaderboard[] leaderboards = new Leaderboard[]{
                new Leaderboard(12311, "test2", null, new Date(), true),
                new Leaderboard(121422, "test2", new Date(), new Date(), true),
                new Leaderboard(1533, "test3", new Date(), null, false),
                new Leaderboard(1634, "test4", new Date(), new Date(), true),
                new Leaderboard(13455, "test4", null, null, false)
            };

            for(Leaderboard l : leaderboards) {
                assertTrue(LeaderboardModify.createLeaderboard(
                        l.gameId(),
                        l.leaderboardName(),
                        l.startTime(),
                        l.endTime(),
                        l.descending()));

                assertNotNull(LeaderboardRead.getLeaderboard(l.gameId(), l.leaderboardName()));
            }
        });
    }

    @Test
    public void createExistingLeaderboardTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            Leaderboard l = testLeaderboards[0];
            try {
                LeaderboardModify.createLeaderboard(l.gameId(), l.leaderboardName(), l.startTime(), l.endTime(), l.descending());
                assertTrue("Creating exiting leaderboard should throw", false);
            } catch (Exception e) {
                assertTrue(true);
            }
        });
    }

    @Test
    public void editLeaderboardTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            for(Leaderboard l : testLeaderboards) {
                Leaderboard newL = new Leaderboard(l.gameId(), l.leaderboardName(), null, new Date(123456), true);

                assertTrue(LeaderboardModify.editLeaderboard(
                        newL.gameId(),
                        newL.leaderboardName(),
                        newL.startTime(),
                        newL.endTime()));

                Leaderboard fetched = LeaderboardRead.getLeaderboard(newL.gameId(), newL.leaderboardName());
                assertNotNull(fetched);
                assertEquals(newL.gameId(), fetched.gameId());
                assertEquals(newL.leaderboardName(), fetched.leaderboardName());
                assertEquals(newL.startTime(), fetched.startTime());
                assertEquals(newL.endTime(), fetched.endTime());
                assertEquals(l.descending(), fetched.descending());
            }
        });
    }

    @Test
    public void editNonExistentLeaderboardTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertFalse(LeaderboardModify.editLeaderboard(123, "abc", new Date(), new Date()));
        });
    }

    @Test
    public void deleteLeaderboardTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertTrue(LeaderboardModify.deleteLeaderboard(testLeaderboards[0].gameId(), testLeaderboards[0].leaderboardName()));
            assertFalse(LeaderboardModify.deleteLeaderboard(testLeaderboards[0].gameId(), testLeaderboards[0].leaderboardName()));
        });
    }

    @Test
    public void deleteNonExistentLeaderboardTest() {
        DatabaseRunner.runDatabaseTests( () -> {
            assertFalse(LeaderboardModify.deleteLeaderboard(123, "abc"));
        });
    }

}
