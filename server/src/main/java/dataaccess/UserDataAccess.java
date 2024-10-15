package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;


public class UserDataAccess {
    final private HashMap<String, UserData> UsersData = new HashMap<>();

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        if (checkForDuplicateEmails(email)) {
            throw new DataAccessException("User with email already exists");
        }
        if (UsersData.containsKey(username)) {
            throw new DataAccessException("User already exists");
        }

        UserData newUser = new UserData(username, password, email);

        UsersData.put(newUser.username(), newUser);

        return newUser;
    }

    public UserData getUser(String username) throws DataAccessException {

        if (!UsersData.containsKey(username)) {
            throw new DataAccessException("User does not exist");
        }

        return UsersData.get(username);
    }
    
    public boolean checkForDuplicateEmails (String newEmail) {
        boolean isDuplicate = false;
        for (String mapKey : UsersData.keySet()) {
            UserData currUser = UsersData.get(mapKey);

            if (Objects.equals(newEmail, currUser.email())) {
                isDuplicate = true;
            }
        }
        return isDuplicate;
    }

    public void clear() {
        UsersData.clear();
    }
}
