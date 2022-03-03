package com.david.potterapp.data;

import com.david.potterapp.data.model.LoggedInUser;
import com.david.potterapp.data.model.User;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password, UserDAO userDAO) {

        try {
            // TODO: handle loggedInUser authentication
//            LoggedInUser fakeUser =
//                    new LoggedInUser(
//                            java.util.UUID.randomUUID().toString(),
//                            "Jane Doe");
            if(userDAO.login(username, password)){
                User user = userDAO.getUser(username, password);
                LoggedInUser loggedUser = new LoggedInUser(String.valueOf(user.getId()), user.getUsername());
                return new Result.Success<>(loggedUser);
            }else{
                return new Result.Error(new Exception("Username or Password was not found in users"));
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}