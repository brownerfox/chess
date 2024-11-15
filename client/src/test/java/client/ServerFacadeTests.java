package client;

import org.junit.jupiter.api.*;
import requests.CreateUserRequest;
import requests.LogInRequest;
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


    @Test
    @DisplayName("CreateUserSuccess")
    public void createUserSuccess() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");
        String actual = serverFacade.createUser(userRequest);
        String expected = String.format("You signed in as %s!", userRequest.username());

        Assertions.assertEquals(actual, expected);
    }

    @Test
    @DisplayName("CreateUserFailure")
    public void createUserFailure() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", null, "j@gmail.com");
        String actual = serverFacade.createUser(userRequest);
        String expected = "You need to insert your username, password, and email!";

        Assertions.assertEquals(actual, expected);
    }

    @Test
    @DisplayName("LogInUserSuccess")
    public void logInUserSuccess() throws Exception {
        LogInRequest logInRequest = new LogInRequest("jeremy", "jeremyiscool");

        String actual = serverFacade.logInUser(logInRequest);
        String expected = "You signed in as jeremy!";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("LogInUserFailure")
    public void logInUserFailure() throws Exception {

    }

    @Test
    @DisplayName("LogOutUserSuccess")
    public void logOutUserSuccess() throws Exception {

    }

    @Test
    @DisplayName("LogOutUserFailure")
    public void logOutUserFailure() throws Exception {

    }

    @Test
    @DisplayName("ListGameSuccess")
    public void listGameSuccess() throws Exception {

    }

    @Test
    @DisplayName("ListGameFailure")
    public void listGameFailure() throws Exception {

    }

    @Test
    @DisplayName("CreateGameSuccess")
    public void createGameSuccess() throws Exception {

    }

    @Test
    @DisplayName("CreateGameFailure")
    public void createGameFailure() throws Exception {

    }

    @Test
    @DisplayName("JoinGameSuccess")
    public void joinGameSuccess() throws Exception {

    }

    @Test
    @DisplayName("JoinGameFailure")
    public void joinGameFailure() throws Exception {

    }

    @Test
    @DisplayName("clearSuccess")
    public void clearSuccess() throws Exception {

    }
}
