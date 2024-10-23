package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import requests.CreateGameRequest;
import requests.CreateUserRequest;
import server.Server;
import service.ChessService;

import java.security.Provider;

public class ServiceUnitTests {

    public ChessService service = new ChessService(new MemoryDataAccess());

    @Test
    @DisplayName("CreateUserSuccess")
    public void CreateUserSuccess() throws Exception {
        UserData expected = new UserData("Jeremy", "1234", "j@gmail.com");

        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");

        service.createUser(userRequest);
        UserData actual = service.getDataAccess().getUser("Jeremy");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("CreateUserFailure")
    public void CreateUserFailure() throws Exception {
        UserData expected = new UserData("Jeremy", "1234", "j@gmail.com");

        CreateUserRequest userRequest = new CreateUserRequest("Jeremy", "1234", "j@gmail.com");

        service.createUser(userRequest);

        Assertions.assertThrows(ServiceException.class, () -> service.createUser(userRequest));
    }

    

}
