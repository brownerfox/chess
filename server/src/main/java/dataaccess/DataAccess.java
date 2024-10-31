package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;
import service.BadGameIDException;
import service.ServiceException;

import java.sql.SQLException;
import java.util.Collection;

public interface DataAccess {
    UserData createUser (UserData user) throws DataAccessException, ServiceException;

    UserData getUser (String username) throws DataAccessException;

    int createGame (String game) throws DataAccessException, ServiceException;

    GameData getGame (int gameID) throws BadGameIDException, DataAccessException, ServiceException;

    Collection<GameData> listGames () throws DataAccessException, ServiceException;

    GameData updateGame (GameData newGame) throws DataAccessException, ServiceException, SQLException;

    AuthData createAuth (String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException, ServiceException;

    void deleteAuth (String authToken) throws DataAccessException;

    void clear () throws DataAccessException, ServiceException;
}
