package catering.businesslogic.user;

import catering.businesslogic.shift.Shift;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User {

  private static Map<Integer, User> loadedUsers = new HashMap<Integer, User>();

  public static enum Role {SERVIZIO, CUOCO, CHEF, ORGANIZZATORE}

  ;

  private int id;
  private String username;
  private Set<Role> roles;

  private Map<Shift, Integer> shiftAssignments;

  public User() {
    id = 0;
    username = "";
    this.roles = new HashSet<>();
    this.shiftAssignments = new HashMap<>();
  }

  public boolean isChef() {
    return roles.contains(Role.CHEF);
  }

  public String getUserName() {
    return username;
  }

  public int getId() {
    return this.id;
  }

  public String toString() {
    String result = username;
    if (roles.size() > 0) {
      result += ": ";

      for (User.Role r : roles) {
        result += r.toString() + " ";
      }
    }
    return result;
  }

  // STATIC METHODS FOR PERSISTENCE

  public static User loadUserById(int uid) {
    if (loadedUsers.containsKey(uid)) return loadedUsers.get(uid);

    User load = new User();
    String userQuery = "SELECT * FROM Users WHERE id='" + uid + "'";
    PersistenceManager.executeQuery(userQuery, new ResultHandler() {
      @Override
      public void handle(ResultSet rs) throws SQLException {
        load.id = rs.getInt("id");
        load.username = rs.getString("username");
      }
    });
    if (load.id > 0) {
      loadedUsers.put(load.id, load);
      String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + load.id;
      PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
        @Override
        public void handle(ResultSet rs) throws SQLException {
          String role = rs.getString("role_id");
          switch (role.charAt(0)) {
            case 'c':
              load.roles.add(User.Role.CUOCO);
              break;
            case 'h':
              load.roles.add(User.Role.CHEF);
              break;
            case 'o':
              load.roles.add(User.Role.ORGANIZZATORE);
              break;
            case 's':
              load.roles.add(User.Role.SERVIZIO);
          }
        }
      });
    }
    return load;
  }

  public static User loadUser(String username) {
    User u = new User();
    String userQuery = "SELECT * FROM Users WHERE username='" + username + "'";
    PersistenceManager.executeQuery(userQuery, new ResultHandler() {
      @Override
      public void handle(ResultSet rs) throws SQLException {
        u.id = rs.getInt("id");
        u.username = rs.getString("username");
      }
    });
    if (u.id > 0) {
      loadedUsers.put(u.id, u);
      String roleQuery = "SELECT * FROM UserRoles WHERE user_id=" + u.id;
      PersistenceManager.executeQuery(roleQuery, new ResultHandler() {
        @Override
        public void handle(ResultSet rs) throws SQLException {
          String role = rs.getString("role_id");
          switch (role.charAt(0)) {
            case 'c':
              u.roles.add(User.Role.CUOCO);
              break;
            case 'h':
              u.roles.add(User.Role.CHEF);
              break;
            case 'o':
              u.roles.add(User.Role.ORGANIZZATORE);
              break;
            case 's':
              u.roles.add(User.Role.SERVIZIO);
          }
        }
      });
    }
    return u;
  }

  public boolean isAvailableForShift(Shift shift, int requestedMinutes) {
    // First check if user has cook role
    if (!this.isChef()) {
      return false;
    }

    // Check if shift is saturated
    if (shift.isSaturated()) {
      return false;
    }

    // Check for time conflicts with existing assignments
    for (Map.Entry<Shift, Integer> assignment : shiftAssignments.entrySet()) {
      Shift existingShift = assignment.getKey();
      if (shiftsOverlap(existingShift, shift)) {
        return false;
      }
    }

    // Check if requested time fits in shift duration
    int totalShiftMinutes = shift.getDuration();
    int currentlyAssignedMinutes = shiftAssignments.getOrDefault(shift, 0);
    int remainingMinutes = totalShiftMinutes - currentlyAssignedMinutes;

    return requestedMinutes <= remainingMinutes;
  }

  private boolean shiftsOverlap(Shift shift1, Shift shift2) {
    return !shift1.getEndTime().isBefore(shift2.getStartTime()) &&
            !shift1.getStartTime().isAfter(shift2.getEndTime());
  }

  public int getAssignedTimeForShift(Shift shift) {
    return shiftAssignments.getOrDefault(shift, 0);
  }

  public void assignToShift(Shift shift, int minutes) {
    shiftAssignments.merge(shift, minutes, Integer::sum);
  }

  public void addRole(Role role) {
    roles.add(role);
  }
}
