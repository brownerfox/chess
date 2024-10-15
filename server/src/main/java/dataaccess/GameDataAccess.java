package dataaccess;


import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;

public class GameDataAccess {
    private int nextId = 1;
    final private HashMap<Integer, GameData> gameList = new HashMap<>();

    public GameData createGame(GameData game) throws DataAccessException {
        GameData newGame = new GameData(nextId++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        if (gameList.get(newGame.gameID()) != null) {
            throw new DataAccessException("Game with ID already exists");
        }

        gameList.put(newGame.gameID(), newGame);

        return newGame;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (gameList.get(gameID) != null) {
            throw new DataAccessException("There is no game with given ID");
        }

        return gameList.get(gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        if (gameList.isEmpty()) {
            throw new DataAccessException("The game list is empty");
        }

        return gameList.values();
    }

    public void updateGame(GameData newGame) throws DataAccessException {
        if (gameList.get(newGame.gameID()) == null) {
            throw new DataAccessException("There is no game with given ID");
        }
        gameList.put(newGame.gameID(), newGame);
    }

    public void clear() {
        gameList.clear();
    }


}
