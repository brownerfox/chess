import chess.*;
import server.*;

import java.rmi.ServerError;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        server.run(8080);
    }


}