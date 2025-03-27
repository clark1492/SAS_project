package catering.businesslogic.user;

import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
  private User currentUser;
  private ArrayList<User> users;

  public UserManager() {
    users = new ArrayList<>();
  }

  public void fakeLogin(String username) //TODO: bisogna implementare il login vero!
  {
    this.currentUser = User.loadUser(username);
  }

  public User getCurrentUser() {
    return this.currentUser;
  }

  public ArrayList<User> getAllUsers() {
    // Load all users if not already loaded
    if (users.isEmpty()) {
      String query = "SELECT * FROM Users WHERE true";
      PersistenceManager.executeQuery(query, new ResultHandler() {
        @Override
        public void handle(ResultSet rs) throws SQLException {
          String username = rs.getString("username");
          User user = User.loadUser(username);
          if (user != null && !users.contains(user)) {
            users.add(user);
          }
        }
      });
    }
    return users;
  }
}
