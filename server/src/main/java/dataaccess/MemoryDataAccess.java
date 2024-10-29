package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import model.GameData;
import service.BadGameIDException;
import service.ServiceException;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<String, UserData> usersData = new HashMap<>();
    final private HashMap<String, AuthData> authDataMap = new HashMap<>();
    final private HashMap<Integer, GameData> gameList = new HashMap<>();

    private int nextId = 1;

    public UserData createUser(UserData user) throws DataAccessException, ServiceException {

        if (checkForDuplicateEmails(user.email())) {
            throw new ServiceException("Error: already taken");
        }
        if (usersData.containsKey(user.username())) {
            throw new ServiceException("Error: already taken");
        }

        UserData newUser = new UserData(user.username(), user.password(), user.email());

        usersData.put(newUser.username(), newUser);

        return newUser;
    }

    public UserData getUser(String username) throws DataAccessException {

        if (!usersData.containsKey(username)) {
            throw new DataAccessException("User does not exist");
        }

        return usersData.get(username);
    }

    public int createGame(String gameName) throws DataAccessException {
        GameData newGame = new GameData(nextId++, null, null, gameName, new ChessGame());

        if (gameList.get(newGame.gameID()) != null) {
            throw new DataAccessException("Game with ID already exists");
        }

        gameList.put(newGame.gameID(), newGame);

        return newGame.gameID();
    }

    public GameData getGame(int gameID) throws BadGameIDException {
        if (gameList.get(gameID) == null) {
            throw new BadGameIDException("There is no game with given ID");
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

    public AuthData createAuth(String username) {

        AuthData newAuthData = new AuthData(generateToken(), username);

        authDataMap.put(newAuthData.authToken(), newAuthData);

        return newAuthData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("authToken does not exist");
        }

        return authDataMap.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("authToken already does not exist");
        }

        authDataMap.remove(authToken);
    }

    public void clear() {
        usersData.clear();
        gameList.clear();
        authDataMap.clear();
    }

    public boolean checkForDuplicateEmails (String newEmail) {
        boolean isDuplicate = false;
        for (String mapKey : usersData.keySet()) {
            UserData currUser = usersData.get(mapKey);

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
