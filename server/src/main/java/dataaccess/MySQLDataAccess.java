package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.BadGameIDException;
import service.ServiceException;

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
    public UserData createUser(UserData user) throws Exception {
        var statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(user);
        var id = executeUpdateUser(statement, user.username(), user.password(), user.email(), json);
        return new UserData(id, user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM userdata WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public UserData readUser(ResultSet rs);

    @Override
    public int createGame(String game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws BadGameIDException {
        return null;
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
    public AuthData createAuth(String username) throws DataAccessException {
        AuthData newAuthData = new AuthData(generateToken(), username);
        var statement = "INSERT INTO authdata (authtoken, username) VALUES (?, ?)";
        var json = new Gson().toJson(newAuthData);

        return newAuthData;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

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
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS authdata (
              `authtoken` varchar(512) NOT NULL,
              `username` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
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
              `json` TEXT DEFAULT NULL,
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

    private String executeUpdateUser(String statement, Object... params) throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getString(1);
                }

                return "";
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
