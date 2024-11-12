package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.BadGameIDException;
import service.ServiceException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException{
        String checkStatement = "SELECT COUNT(*) FROM userdata WHERE username = ?";
        String insertStatement = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var checkPs = conn.prepareStatement(checkStatement)) {
                checkPs.setString(1, user.username());
                try (var rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new DataAccessException("Username already exists.");
                    }
                }
            }

            // If the username doesn't exist, insert the new user
            executeUpdate(insertStatement, user.username(), user.password(), user.email()).toString();
            return new UserData(user.username(), user.password(), user.email());

        } catch (Exception e) {
            throw new DataAccessException("Unable to create user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM userdata WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    } else {
                        throw new DataAccessException("");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("");
        }
    }

    public UserData readUser(ResultSet rs) throws Exception {
        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
    }

    @Override
    public int createGame(String gameName) throws DataAccessException, ServiceException {
        var statement = "INSERT INTO gamedata (gamename, game) VALUES (?, ?)";
        String game = new Gson().toJson(new ChessGame());
        var id = executeUpdate(statement, gameName, game);
        return (int) id;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameid, whiteusername, blackusername, gamename, game FROM gamedata WHERE gameid=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    } else {
                        throw new BadGameIDException("");
                    }
                }
            }
        } catch (Exception e) {
            throw new BadGameIDException("");
        }
    }

    public GameData readGame(ResultSet rs) throws Exception {
        int gameID = rs.getInt("gameid");
        String whiteUsername = rs.getString("whiteusername");
        String blackUsername = rs.getString("blackusername");
        String gameName = rs.getString("gamename");
        ChessGame game = new Gson().fromJson((String) rs.getObject("game"), ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameid, whiteusername, blackusername, gamename, game FROM gamedata";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public GameData updateGame(GameData newGame) throws DataAccessException, SQLException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE gamedata SET whiteusername = ?, blackusername = ?, gamename = ?, game = ? WHERE gameid = ?";
            String game = new Gson().toJson(newGame.game());
            executeUpdate(statement, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), game, newGame.gameID());
        }
        return newGame;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        AuthData newAuthData = new AuthData(generateToken(), username);
        var statement = "INSERT INTO authdata (authtoken, username) VALUES (?, ?)";
        executeUpdate(statement, newAuthData.authToken(), newAuthData.username());

        return newAuthData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authtoken, username FROM authdata WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    } else {
                        throw new DataAccessException("");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("");
        }
    }

    public AuthData readAuth(ResultSet rs) throws Exception {
        return new AuthData(rs.getString("authtoken"), rs.getString(("username")));
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var checkStatement = "SELECT EXISTS (SELECT 1 FROM authdata WHERE authtoken = ?)";
        var deleteStatement = "DELETE FROM authdata WHERE authtoken = ?";

        try (var conn = DatabaseManager.getConnection()) {
            try (var psCheck = conn.prepareStatement(checkStatement)) {
                psCheck.setString(1, authToken);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getBoolean(1)) {
                        executeUpdate(deleteStatement, authToken);
                    } else {
                        throw new DataAccessException("");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String[] tables = {"gamedata", "userdata", "authdata"};
        try (var conn = DatabaseManager.getConnection()) {
            for (String table : tables) {
                var statement = "TRUNCATE TABLE " + table;
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("");
        }

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userdata (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS authdata (
              `authtoken` varchar(512) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authtoken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS gamedata (
              `gameid` int NOT NULL AUTO_INCREMENT,
              `whiteusername` varchar(256) DEFAULT NULL,
              `blackusername` varchar(256) DEFAULT NULL,
              `gamename` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameid`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private PreparedStatement iterateOverParameters (PreparedStatement ps, Object... params) throws SQLException {
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> {
                }
            }
        }

        return ps;
    }

    private Object executeUpdate(String statement, Object... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                var newPS = iterateOverParameters(ps, params);

                // System.out.print(ps.toString());

                newPS.executeUpdate();

                var rs = newPS.getGeneratedKeys();
                if (rs.next()) {
                    Object key;
                    int generatedInt = rs.getInt(1);
                    if (!rs.wasNull()) {
                        key = generatedInt;
                    } else {
                        key = rs.getString(1);
                    }
                    return key;
                }
                return 0;
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("");
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
