package exception;

public class ResponseException extends Exception {
    final private int statusCode;
    private String message;

    public ResponseException(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {return this.message;}
}