package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.chess.ChessPositionTests;
import passoff.model.*;
import requests.CreateGameRequest;
import requests.CreateUserRequest;
import results.CreateGameResult;
import results.CreateUserResult;
import results.LogInResult;
import server.Server;
import service.ChessService;
import spark.utils.Assert;

import java.security.Provider;

public class ServiceUnitTests {

    public ChessService service = new ChessService(new MemoryDataAccess());

    @AfterEach
    public void clear() throws Exception {
        service.clear();
    }

    @Test
    @DisplayName("CreateUserSuccess")
    public void createUserSuccess() throws Exception {

        UserData expected = new UserData("Jeremy", "1234", "j@gmail.com");

        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");

        service.createUser(userRequest);
        UserData actual = service.getDataAccess().getUser("Jeremy");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CreateUserFailure")
    public void createUserFailure() throws Exception {

        UserData expected = new UserData("Jeremy", "1234", "j@gmail.com");

        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");

        service.createUser(userRequest);

        Assertions.assertThrows(ServiceException.class, () -> service.createUser(userRequest));
    }

    @Test
    @DisplayName("LogInSuccess")
    public void logInSuccess() throws Exception {

        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");

        CreateUserResult userResult = service.createUser(userRequest);

        service.logoutUser(userResult.authToken());

        Assertions.assertDoesNotThrow(() -> service.loginUser(userRequest.username(), userRequest.password()));
    }

    @Test
    @DisplayName("LogInFailure")
    public void logInFailure() throws Exception {

        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");

        CreateUserResult userResult = service.createUser(userRequest);

        service.logoutUser(userResult.authToken());

        Assertions.assertThrows(DataAccessException.class, () -> service.loginUser("Jermy", userRequest.password()));
    }

    @Test
    @DisplayName("LogOutSuccess")
    public void logOutSuccess() throws Exception {

        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");

        CreateUserResult userResult = service.createUser(userRequest);

        Assertions.assertDoesNotThrow(() -> service.logoutUser(userResult.authToken()));
    }

    @Test
    @DisplayName("LogOutFailure")
    public void logOutFailure() throws Exception {

        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");

        CreateUserResult userResult = service.createUser(userRequest);

        Assertions.assertThrows(DataAccessException.class, () -> service.logoutUser("userResult.authToken()"));
    }

    @Test
    @DisplayName("GameListSuccess")
    public void gameListSuccess() throws Exception {
        AuthData authData = service.getDataAccess().createAuth("ooo");

        Assertions.assertDoesNotThrow(() -> service.listGames(authData.authToken()));
    }

    @Test
    @DisplayName("GameListFailure")
    public void gameListFailure() throws Exception {
        AuthData authData = service.getDataAccess().createAuth("ooo");

        Assertions.assertThrows(DataAccessException.class, () -> service.listGames("Wrong authToken"));
    }

    @Test
    @DisplayName("CreateGameSuccess")
    public void createGameSuccess() throws Exception {
        AuthData authData = service.getDataAccess().createAuth("ooo");

        Assertions.assertDoesNotThrow(() -> service.createGame(authData.authToken(), "fun"));
    }

    @Test
    @DisplayName("CreateGameFailure")
    public void createGameFailure() throws Exception {
        AuthData authData = service.getDataAccess().createAuth("ooo");

        Assertions.assertThrows(DataAccessException.class, () -> service.createGame("No auth data", "fun"));
    }

    @Test
    @DisplayName("JoinGameFailure")
    public void joinGameFailure() throws Exception {
        AuthData authData = service.getDataAccess().createAuth("ooo");
        service.createGame(authData.authToken(), "fun");


        Assertions.assertThrows(DataAccessException.class, () -> service.joinGame("No auth data","WHITE" ,1));
    }

    @Test
    @DisplayName("JoinGameSuccess")
    public void joinGameSuccess() throws Exception {
        AuthData authData = service.getDataAccess().createAuth("ooo");
        service.createGame(authData.authToken(), "fun");

        Assertions.assertDoesNotThrow(() -> service.joinGame(authData.authToken(),"WHITE", 1));
    }

    @Test
    @DisplayName("ClearSuccess")
    public void clearSuccess() throws Exception {
        CreateUserResult userResult = service.createUser(new CreateUserRequest("a", "p", "e"));

        service.createGame(userResult.authToken(), "Yep");

        Assertions.assertDoesNotThrow(() -> service.clear());
    }

}
