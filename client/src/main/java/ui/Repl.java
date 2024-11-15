package ui;

import chess.ChessGame;
import client.ChessClient;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {

    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        System.out.println(WHITE_KING + "Welcome to Chess!" + BLACK_KING);
        System.out.print(client.printHelpMenu());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
                if (line.equals("join") || line.equals("observe")) {
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        String logInStatus = client.getState() == State.SIGNEDIN ? "LOGGED_IN" : "LOGGED_OUT";
        String status = String.format("\n[%s] >>> ", logInStatus);
        System.out.print(status + SET_TEXT_COLOR_GREEN);
    }
}
