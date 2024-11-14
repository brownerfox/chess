package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.BadGameIDException;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private MySQLDataAccess dataAccess;

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }


    @Test
    @DisplayName("Create User Success")
    public void createUserSuccess() {
        UserData user = new UserData("testuser", "password", "test@example.com");
        assertDoesNotThrow(() -> {
            dataAccess.createUser(user);
        });
    }

    @Test
    @DisplayName("Create User Success")
    public void createUserFailure() {
        UserData user = new UserData("testuser", "password", "test@example.com");
        assertDoesNotThrow(() -> {
            dataAccess.createUser(user); // First attempt, should succeed
        });

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(user); // Second attempt, should fail
        });
    }

    @Test
    @DisplayName("Get User Success")
    public void getUserSuccess() {
        UserData user = new UserData("testuser", "password", "test@example.com");
        assertDoesNotThrow(() -> {dataAccess.createUser(user);}); // Create user for testing
        assertDoesNotThrow(() -> {
            UserData retrievedUser = dataAccess.getUser("testuser");
            assertNotNull(retrievedUser);
            assertEquals(user.username(), retrievedUser.username());
        });
    }

    @Test
    @DisplayName("Get User Failure")
    public void getUserFailure () {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getUser("nonexistentuser");
        });
    }

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess () {
        assertDoesNotThrow(() -> {
            int gameId = dataAccess.createGame("Test Game");
            assertTrue(gameId > 0); // Assuming gameId should be positive
        });
    }

    @Test
    @DisplayName("Create Game Failure")
    public void createGameFailure () {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.createGame(null); // Attempting to create a game with an empty name
        });
    }

    @Test
    @DisplayName("Get Game Success")
    public void getGameSuccess () {
        try {
            int gameId = dataAccess.createGame("Test Game");
            assertDoesNotThrow(() -> {
                GameData gameData = dataAccess.getGame(gameId);
                assertNotNull(gameData);
                assertEquals("Test Game", gameData.gameName());
            });
        } catch (Exception e) {
            System.out.print("Failure");
        }
    }

    @Test
    @DisplayName("Get Game Failure")
    public void getGameFailure () {
        assertThrows(BadGameIDException.class, () -> {
            dataAccess.getGame(-1); // Using an invalid game ID
        });
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess () {

    }

    @Test
    @DisplayName("List Games Failure")
    public void listGamesFailure () {
        assertDoesNotThrow(() -> {
            dataAccess.createGame("Test Game 1");
            dataAccess.createGame("Test Game 2");
            var games = dataAccess.listGames();
            assertFalse(games.isEmpty());
        });
    }

    @Test
    @DisplayName("Update Game Success")
    public void updateGameSuccess () {
        try {
            int gameId = dataAccess.createGame("Old Game Name"); // Create initial game
            GameData gameData = new GameData(gameId, "user1", "user2", "New Game Name", new ChessGame());
            assertDoesNotThrow(() -> {
                dataAccess.updateGame(gameData);
                GameData updatedGame = dataAccess.getGame(gameId);
                assertEquals("New Game Name", updatedGame.gameName());
            });
        } catch (Exception e) {
            System.out.print("Failure");
        }
    }

    @Test
    @DisplayName("Update Game Failure")
    public void updateGameFailure () {
        GameData gameData = new GameData(-1, "user1", "user2", "New Game Name", new ChessGame());
        assertThrows(DataAccessException.class, () -> {
            dataAccess.updateGame(gameData);
        });
    }

    @Test
    @DisplayName("Create Authorization Success")
    public void createAuthSuccess () {
        assertDoesNotThrow(() -> {
            AuthData authData = dataAccess.createAuth("testuser");
            assertNotNull(authData);
            assertEquals("testuser", authData.username());
        });
    }

    @Test
    @DisplayName("Create Authorization Failure")
    public void createAuthFailure () {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.createAuth(null); // Attempting to create auth with an empty username
        });
    }

    @Test
    @DisplayName("Get Authorization Success")
    public void getAuthSuccess () {
        try {
            AuthData authData = dataAccess.createAuth("testuser");
            assertDoesNotThrow(() -> {
                AuthData retrievedAuth = dataAccess.getAuth(authData.authToken());
                assertNotNull(retrievedAuth);
                assertEquals(authData.username(), retrievedAuth.username());
            });
        } catch (Exception e) {
            System.out.print("Failure");
        }
    }

    @Test
    @DisplayName("Get Authorization Failure")
    public void getAuthFailure () {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.getAuth("invalidToken"); // Attempting to get auth with an invalid token
        });
    }

    @Test
    @DisplayName("Delete Authorization Success")
    public void deleteAuthSuccess () {
        try {
            AuthData authData = dataAccess.createAuth("testuser");
            assertDoesNotThrow(() -> {
                dataAccess.deleteAuth(authData.authToken()); // Should succeed
            });
        } catch (Exception e) {
            System.out.print("Failure");
        }
    }

    @Test
    @DisplayName("Delete Authorization Failure")
    public void deleteAuthFailure () {
        assertThrows(DataAccessException.class, () -> {
            dataAccess.deleteAuth("invalidToken"); // Attempting to delete with an invalid token
        });
    }

    @Test
    @DisplayName("Clear")
    public void clearSuccess () {
        assertDoesNotThrow(() -> {
            dataAccess.clear(); // Should succeed
        });
    }


}
