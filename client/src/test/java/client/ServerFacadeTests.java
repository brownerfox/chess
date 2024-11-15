package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.CreateUserRequest;
import requests.JoinGameRequest;
import requests.LogInRequest;
import results.CreateGameResult;
import results.ListGamesResult;
import server.Server;
import client.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:"+port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    void clearServer() {
        serverFacade.clear();
    }

    @Test
    @DisplayName("LogInUserSuccess")
    public void logInUserSuccess() throws Exception {
        serverFacade.createUser(new CreateUserRequest("taft", "password", "email"));
        serverFacade.logOutUser();

        String actual = serverFacade.logInUser(new LogInRequest("taft", "password"));
        String expected = "You signed in as taft!";

        Assertions.assertEquals(expected, actual);
    }


    @Test
    @DisplayName("LogInUserFailure")
    public void logInUserFailure() throws Exception {
        LogInRequest logInRequest = new LogInRequest("nonexistentUser", "wrongPassword");

        String actual = serverFacade.logInUser(logInRequest);
        String expected = "Failed to login to user!";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("LogOutUserSuccess")
    public void logOutUserSuccess() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("jeremy", "jeremyiscool", "email");
        serverFacade.createUser(createUserRequest);

        String actual = serverFacade.logOutUser();
        String expected = "You've successfully logged out!";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("LogOutUserFailure")
    public void logOutUserFailure() throws Exception {
        String actual = serverFacade.logOutUser();
        String expected = "Failed to logout user!";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("ListGameSuccess")
    public void listGameSuccess() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("taft", "taftiscool", "e");
        serverFacade.createUser(createUserRequest);
        serverFacade.createGame("gamename");
        ListGamesResult result = serverFacade.listGames();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.games().isEmpty(), "Expected at least one game in the list.");
    }

    @Test
    @DisplayName("ListGameFailure")
    public void listGameFailure() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.listGames();});
    }

    @Test
    @DisplayName("CreateGameSuccess")
    public void createGameSuccess() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("jeremy", "jeremyiscool", "email");
        serverFacade.createUser(createUserRequest);

        String gameName = "TestGame";

        Assertions.assertDoesNotThrow(() -> serverFacade.createGame(gameName));
    }

    @Test
    @DisplayName("CreateGameFailure")
    public void createGameFailure() throws Exception {
        Assertions.assertThrows(NullPointerException.class, () -> {
            serverFacade.createGame(null); // Invalid game name
        });
    }

    @Test
    @DisplayName("JoinGameSuccess")
    public void joinGameSuccess() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("jeremy", "jeremyiscool", "email");
        serverFacade.createUser(createUserRequest);
        serverFacade.createGame("gamename");
        JoinGameRequest joinGameRequest = new JoinGameRequest(serverFacade.getAuthToken(), "white", 1);
        String actual = serverFacade.joinGame(joinGameRequest);
        String expected = "jeremy joined as white player!";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("JoinGameFailure")
    public void joinGameFailure() throws Exception {
        JoinGameRequest joinGameRequest = new JoinGameRequest(serverFacade.getAuthToken(), "white", -1);

        String actual = serverFacade.joinGame(joinGameRequest);
        String expected = "Couldn't join game!";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("clearSuccess")
    public void clearSuccess() throws Exception {
        String actual = serverFacade.clear();
        String expected = "Successfully cleared out the database!";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("clearFailure")
    public void clearFailure() throws Exception {
        String actual = serverFacade.clear();
        String expected = "Successfully cleared out the database!";

        Assertions.assertEquals(expected, actual);
    }
}
