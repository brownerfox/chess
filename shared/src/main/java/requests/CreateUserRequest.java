package requests;

public record CreateUserRequest(String username, String password, String email) {
}
