package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;
import results.CreateUserResult;

import java.util.Collection;

public interface DataAccess  {
    CreateUserResult createUser (String username, String password, String email) throws DataAccessException;

    UserData getUser (String username) throws DataAccessException;

    GameData createGame (GameData game) throws DataAccessException;

    GameData getGame (int gameID) throws DataAccessException;

    Collection<GameData> listGames () throws DataAccessException;

    GameData updateGame (GameData newGame) throws DataAccessException;

    AuthData createAuth (String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth (String authToken) throws DataAccessException;

    void clear ();
}
