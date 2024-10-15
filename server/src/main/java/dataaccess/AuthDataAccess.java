package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class AuthDataAccess {
    final private HashMap<String, AuthData> AuthDataMap = new HashMap<>();

    public AuthData createAuth(String authToken, String username) throws DataAccessException {
        if (AuthDataMap.containsKey(authToken)) {
            throw new DataAccessException("authToken already exists");
        }

        AuthData newAuthData = new AuthData(authToken, username);

        AuthDataMap.put(newAuthData.authToken(), newAuthData);

        return newAuthData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!AuthDataMap.containsKey(authToken)) {
            throw new DataAccessException("authToken does not exist");
        }

        return AuthDataMap.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!AuthDataMap.containsKey(authToken)) {
            throw new DataAccessException("authToken already does not exist");
        }

        AuthDataMap.remove(authToken);
    }

    public void clear() {
        AuthDataMap.clear();
    }
}
