package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<String, UserData> UsersData = new HashMap<>();
    final private HashMap<String, AuthData> AuthDataMap = new HashMap<>();
    final private HashMap<Integer, GameData> gameList = new HashMap<>();

    private int nextId = 1;

    public UserData createUser(String username, String password, String email) throws DataAccessException {

        if (checkForDuplicateEmails(email)) {
            throw new DataAccessException("User with email already exists");
        }
        if (UsersData.containsKey(username)) {
            throw new DataAccessException("User already exists");
        }

        UserData user = new UserData(username, password, email);

        UsersData.put(username, user);

        return user;
    }

    public UserData getUser(String username) throws DataAccessException {

        if (!UsersData.containsKey(username)) {
            throw new DataAccessException("User does not exist");
        }

        return UsersData.get(username);
    }

    public int createGame(String gameName) throws DataAccessException {
        GameData newGame = new GameData(nextId++, null, null, gameName, new ChessGame());

        if (gameList.get(newGame.gameID()) != null) {
            throw new DataAccessException("Game with ID already exists");
        }

        gameList.put(newGame.gameID(), newGame);

        return newGame.gameID();
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (gameList.get(gameID) != null) {
            throw new DataAccessException("There is no game with given ID");
        }

        return gameList.get(gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return gameList.values();
    }

    public GameData updateGame(GameData newGame) throws DataAccessException {
        if (gameList.get(newGame.gameID()) == null) {
            throw new DataAccessException("There is no game with given ID");
        }
        gameList.put(newGame.gameID(), newGame);

        return newGame;
    }

    public AuthData createAuth(String username) throws DataAccessException {
        if (AuthDataMap.containsKey(username)) {
            throw new DataAccessException("authToken already exists");
        }

        AuthData newAuthData = new AuthData(generateToken(), username);

        AuthDataMap.put(newAuthData.authToken(), newAuthData);

        return newAuthData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!AuthDataMap.containsKey(authToken)) {
            throw new DataAccessException("authToken does not exist");
        }

        return AuthDataMap.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!AuthDataMap.containsKey(authToken)) {
            throw new DataAccessException("authToken already does not exist");
        }

        AuthDataMap.remove(authToken);
    }

    public void clear() {
        UsersData.clear();
        gameList.clear();
        AuthDataMap.clear();
    }

    public boolean checkForDuplicateEmails (String newEmail) {
        boolean isDuplicate = false;
        for (String mapKey : UsersData.keySet()) {
            UserData currUser = UsersData.get(mapKey);

            if (Objects.equals(newEmail, currUser.email())) {
                isDuplicate = true;
            }
        }
        return isDuplicate;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
