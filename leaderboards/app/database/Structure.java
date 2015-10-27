package database;

public class Structure {
    public class Leaderboards {
        public static final String Name = "Leaderboards";

        public class Columns {
            public static final String Id = "Id";
            public static final String GameId = "GameId";
            public static final String LeaderboardName = "LeaderboardName";
            public static final String StartTime = "StartTime";
            public static final String EndTime = "EndTime";
            public static final String Descending = "Descending";
        }
    }
}
