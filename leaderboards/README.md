Leaderboards
============

The leaderboards service provides an API for storing a list of users sorted by a score. In addition to this it can also hold a blob of data for each user.

It also provides a UI for managing, creating and editing leaderboards.

The API consists of a very simple set of JSON REST endpoints:

POST    /api/v1/get-leaderboard-count

    Takes:
        gameId: Int
        leaderboardName: String

    Returns:
        Long: amount of people in the leaderboard


POST    /api/v1/set-score

    Takes:
        gameId: Int
        leaderboardName: String
        userId: String
        userExtraData: String, Additional data to store with the user
        newUserScore: Double

    Returns:
        Boolean

POST    /api/v1/increment-score

    Takes:
        gameId: Int
        leaderboardName: String
        userId: String
        userExtraData: String, Additional data to store with the user
        increment: Double

    Returns:
        Boolean

POST    /api/v1/get-ranked-users

    Takes:
        gameId: Int
        leaderboardName: String
        startRank: Int, Starting rank from which to fetch users, 0 indexed
        count: Int

    Returns:
        List<LeaderboardUser>

POST    /api/v1/get-user

    Takes:
        gameId: Int
        leaderboardName: String
        userId: String

    Returns:
        LeaderboardUser

POST    /api/v1/get-user-score

    Takes:
        gameId: Int
        leaderboardName: String
        userId: String

    Returns:
        Double

POST    /api/v1/get-user-rank

    Takes:
        gameId: Int
        leaderboardName: String
        userId: String

    Returns:
        Long

POST    /api/v1/get-active-leaderboards

    Takes:
        gameId: Int

    Returns:
        List<Leaderboard>
