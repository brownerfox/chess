package dataaccess;

import model.UserData;
import java.util.Collection;
import java.util.HashMap;


public class UserDataAccess {
    final private HashMap<String, UserData> UsersData = new HashMap<>();

    public UserData createUser(String username, String password, String email) {
        UserData newUser = new UserData(username, password, email);

        UsersData.put(newUser.username(), newUser);

        return newUser;
    }

    public UserData getUser(String username) {
        return UsersData.get(username);
    }

    public void clear() {
        UsersData.clear();
    }
}
