package com.rasp.app;

import java.util.ArrayList;

public class LoginManager {
    ArrayList<UserToken> loggedInUsers = new ArrayList<>();

    public void addUserToken(String username, String token) {
        loggedInUsers.add(new UserToken(username, token));
    }

    public void removeUserToken(String username) {
        loggedInUsers.removeIf(userToken -> userToken.getUsername().equals(username));
    }

    public boolean isLoggedIn(String username) {
        return loggedInUsers.stream().anyMatch(userToken -> userToken.getUsername().equals(username));
    }

    public String getToken(String username) {
        return loggedInUsers.stream().filter(userToken -> userToken.getUsername().equals(username)).findFirst().get().getToken();
    }

    public void login(String username) {
        if (!isLoggedIn(username)) {
            return;
        }
        // Login the user and add the UserToken to loggedInUsers
    }

    // 1. Rashmi logs in
    // 2. Rashmi creates project
    // 3. Rashmi creates 5 tasks in the project
    // 4. Rashmi adds Adi as a collaborator to the project
    // 5. Rashmi assigns 2 tasks to Adi
    // 6. Rashmi logs out
    // 7. Adi logs in
    // 8. Adi views the project and the tasks assigned to him
    // 9. Adi updates the status of the tasks
    // 10. Adi logs out
    // 11. Rashmi logs in
    // 12. Rashmi views the project and the tasks assigned to Adi
    // 13. Rashmi updates the status of the tasks
    // 14. Rashmi logs out
}
