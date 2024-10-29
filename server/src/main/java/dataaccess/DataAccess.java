package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;
import service.BadGameIDException;
import service.ServiceException;

import java.util.Collection;

public interface DataAccess  {
    UserData createUser (UserData user) throws DataAccessException, ServiceException;

    UserData getUser (String username) throws DataAccessException, ServiceException;

    int createGame (String game) throws DataAccessException, ServiceException;

    GameData getGame (int gameID) throws BadGameIDException, ServiceException;

    Collection<GameData> listGames () throws DataAccessException, ServiceException;

    GameData updateGame (GameData newGame) throws DataAccessException, ServiceException;

    AuthData createAuth (String username) throws DataAccessException, ServiceException;

    AuthData getAuth(String authToken) throws DataAccessException, ServiceException;

    void deleteAuth (String authToken) throws DataAccessException, ServiceException;

    void clear () throws DataAccessException, ServiceException;
}
