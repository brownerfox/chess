package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class AuthDataAccess {
    final private HashMap<String, AuthData> AuthDataMap = new HashMap<>();

    public AuthData createAuth(String authToken, String username) {
        AuthData newAuthData = new AuthData(authToken, username);

        AuthDataMap.put(newAuthData.authToken(), newAuthData);

        return newAuthData;
    }

    public AuthData getAuth(String authToken) {
        return AuthDataMap.get(authToken);
    }

    public void deleteAuth(String authToken) {
        AuthDataMap.remove(authToken);
    }
}
