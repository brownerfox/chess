package results;

import model.GameData;

import java.util.HashSet;

public record ListGamesResult(HashSet<GameData> games) {
}
