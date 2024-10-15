package dataaccess;


import model.GameData;

import java.util.Collection;
import java.util.HashMap;

//GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)

public class GameDataAccess {
    private int nextId = 1;
    final private HashMap<Integer, GameData> gameList = new HashMap<>();

    public GameData createGame(GameData game) {
        GameData newGame = new GameData(nextId++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        gameList.put(newGame.gameID(), newGame);

        return newGame;
    }

    public GameData getGame(int gameID) {
        return gameList.get(gameID);
    }

    public Collection<GameData> listGames() {
        return gameList.values();
    }

    public void updateGame(GameData newGame) {
        gameList.put(newGame.gameID(), newGame);
    }

    public void clear() {
        gameList.clear();
    }


}
