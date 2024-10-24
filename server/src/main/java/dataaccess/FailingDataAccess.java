package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import service.BadGameIDException;
import service.ServiceException;

import java.util.Collection;
import java.util.List;

public class FailingDataAccess implements DataAccess {

    @Override
    public UserData createUser(String username, String password, String email) throws ServiceException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

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
        return null;
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
        throw new DataAccessException("Simulated database error");
    }
}
