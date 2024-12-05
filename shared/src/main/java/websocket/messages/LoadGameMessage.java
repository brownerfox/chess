package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    public ChessGame game;

    public LoadGameMessage (String message, ChessGame game) {
        super(ServerMessageType.LOAD_GAME, message);
        this.game = game;
    }

    public ChessGame getGame() {return this.game;}
}
