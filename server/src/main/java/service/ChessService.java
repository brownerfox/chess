package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.GameData;
import model.UserData;
import dataaccess.DataAccessException;

public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(MemoryDataAccess dataAccess) {
        this.dataAccess = new MemoryDataAccess();
    }

    // I want this next function to return a username field and an authToken field, maybe I will fix this when I make a
    // response class?

    public Object createUser(UserData user) throws DataAccessException {
        return dataAccess.createUser(user.username(), user.password(), user.email());
    }

    // I want this function to return a username field and an authtoken field

    public Object loginUser(Username username, Password password) throws DataAccessException {
        return 0;
    }

    // I want this function check my authtoken first and then delete my authtoken

    public Object logoutUser(AuthToken authToken) throws DataAccessException {
        return 0;
    }

    // I want this function to check to see if my authToken is valid and then it should return a list of games

    public Collection<GameData> listGames (AuthToken authToken) throws DataAccessException {
        return 0;
    }

    // I want this function to check my auth token, check for a game and then add a new game to the game list

    public Object createGame(AuthToken authToken, GameName gameName) throws DataAccessException {
        return 0;
    }

    // This function first checks to see if our auth token is valid, then it checks for the desired game, if the
    // game exists, then it will check to see if there is already a player for the desired team. If all goes well
    // it will let the user join the game and update the game

    public Object joinGame(AuthToken authToken, TeamColor teamColor, GameID gameID) throws DataAccessException {
        return 0;
    }

    // This function calls the clear function for each of our dataaccess objects and cleans out the database

    public Object clear() throws DataAccessException {
        return 0;
    }


}
