package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import results.CreateGameResult;
import service.BadGameIDException;
import service.ServiceException;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException, ServiceException {
        var statement = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
        var id = executeUpdate(statement, user.username(), user.password(), user.email()).getClass().getName();
        return new UserData(id, user.password(), user.email());
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
                        throw new DataAccessException("User not found for username: " + username);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public UserData readUser(ResultSet rs) throws Exception {
        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
    }

    @Override
    public int createGame(String gameName) throws DataAccessException, ServiceException {
        var statement = "INSERT INTO gamedata (gameID, whiteusername, blackusername, gamename, game) VALUES (?, ?, ?, ?, ?)";
        GameData newGame = new GameData(1, "", "", gameName, new ChessGame());
        var id = executeUpdate(statement, newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), newGame.game());
        return (int) id;
    }

    @Override
    public GameData getGame(int gameID) throws BadGameIDException, DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameid, whiteusername, blackusername, gamename, game FROM gamedata WHERE gameid=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    } else {
                        throw new BadGameIDException("User not found for username: " + gameID);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public GameData readGame(ResultSet rs) throws Exception {
        return new GameData(rs.getInt("gameid"), rs.getString("whiteusername"), rs.getString("blackusername"), rs.getString("gamename"), rs.getObject("game", ChessGame.class));
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData updateGame(GameData newGame) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException, ServiceException {
        AuthData newAuthData = new AuthData(generateToken(), username);
        var statement = "INSERT INTO authdata (authtoken, username) VALUES (?, ?)";
        executeUpdate(statement, newAuthData.authToken(), newAuthData.username()).getClass().getName();

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
                        throw new DataAccessException("User not found for username: " + authToken);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public AuthData readAuth(ResultSet rs) throws Exception {
        return new AuthData(rs.getString("authtoken"), rs.getString(("username")));
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, ServiceException {
        var statement = "DELETE FROM authdata WHERE authtoken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userdata (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS authdata (
              `authtoken` varchar(512) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authtoken`),
              INDEX(username),
              FOREIGN KEY (`username`) REFERENCES user(`username`) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS gamedata (
              `gameid` int NOT NULL AUTO_INCREMENT,
              `whiteusername` varchar(256) NOT NULL,
              `blackusername` varchar(256) NOT NULL,
              `gamename` varchar(256) NOT NULL,
              `game` varchar(1024) NOT NULL,
              PRIMARY KEY (`gameid`),
              INDEX(whiteusername),
              INDEX(blackusername),
              INDEX(gamename),
              INDEX(game),
              FOREIGN KEY (`whiteusername`) REFERENCES user(`username`) ON DELETE SET NULL,
              FOREIGN KEY (`blackusername`) REFERENCES user(`username`) ON DELETE SET NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private Object executeUpdate(String statement, Object... params) throws DataAccessException, ServiceException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
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
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
