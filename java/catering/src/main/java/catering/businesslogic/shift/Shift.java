package catering.businesslogic.shift;

import catering.businesslogic.user.User;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Shift {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String frequency;
    private String type;
    private int serviceId;
    private LocalDateTime deadLine;

    private static Map<Integer, catering.businesslogic.shift.Shift> loadedShifts = new HashMap<>();


    private boolean saturated;
    private Map<User, Integer> assignedTimes = new HashMap<>();

    public Shift(int id, LocalDateTime startDateTime, LocalDateTime endDateTime, String location, String type, String frequency, int serviceId, LocalDateTime frozenDate) {
      this.id = id;
      this.startTime = startDateTime;
      this.endTime = endDateTime;
      this.location = location;
      this.type = type;
      this.frequency = frequency;
      this.serviceId = serviceId;
      this.deadLine = frozenDate;
      this.saturated = false;
    }

    public Shift(LocalDateTime startDateTime, LocalDateTime endDateTime, String location, String type, String frequency, int serviceId) {
      this(0, startDateTime, endDateTime, location, type, frequency, serviceId, LocalDateTime.now());
    }

    // Getters and setters

  @Override
  public String toString() {
    return "Shift{" +
            "id=" + id +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", location='" + location + '\'' +
            ", frequency='" + frequency + '\'' +
            ", type='" + type + '\'' +
            ", serviceId=" + serviceId +
            ", deadLine=" + deadLine +
            ", saturated=" + saturated +
            '}';
  }

  // Getters
    public int getId() { return id; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getFrequency() { return frequency; }
    public int getServiceId() { return serviceId; }
    public LocalDateTime getFrozenDate() { return deadLine; }
    public boolean isSaturated() { return saturated; }


    public void setSaturated(boolean saturated) {
      this.saturated = saturated;
    }

    public int getAssignedTime(User cook) {
      return assignedTimes.getOrDefault(cook, 0);
    }

    public void addAssignedTime(User cook, int minutes) {
      assignedTimes.merge(cook, minutes, Integer::sum);
    }

    public int getDuration() {
      // Return shift duration in minutes
      return (int) ChronoUnit.MINUTES.between(startTime, endTime);
    }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFrozenDate(LocalDateTime frozenDate) { this.deadLine = frozenDate; }

    public static Shift loadShiftById(int shiftId) {
      if (loadedShifts.containsKey(shiftId)) {
        return loadedShifts.get(shiftId);
      }

      String query = "SELECT * FROM Shifts WHERE id = " + shiftId;
      final catering.businesslogic.shift.Shift[] loadedShift = new catering.businesslogic.shift.Shift[1];  // Using array to modify in lambda

      PersistenceManager.executeQuery(query, new ResultHandler() {
        @Override
        public void handle(ResultSet rs) throws SQLException {
          LocalDateTime start = rs.getTimestamp("start_datetime").toLocalDateTime();
          LocalDateTime end = rs.getTimestamp("end_datetime").toLocalDateTime();
          String location = rs.getString("location");
          String type = rs.getString("type");
          String frequency = rs.getString("frequency");
          int serviceId = rs.getInt("service_id");

          loadedShift[0] = new catering.businesslogic.shift.Shift(start, end, location, type, frequency, serviceId);
          loadedShift[0].id = rs.getInt("id");
          loadedShift[0].saturated = rs.getBoolean("saturated");

          if (rs.getTimestamp("frozen_date") != null) {
            loadedShift[0].deadLine = rs.getTimestamp("frozen_date").toLocalDateTime();
          }
        }
      });

      if (loadedShift[0] != null) {
        // Load assigned times for this shift
        loadAssignedTimes(loadedShift[0]);
        loadedShifts.put(loadedShift[0].getId(), loadedShift[0]);
        return loadedShift[0];
      }
      return null;
    }

    private static void loadAssignedTimes(catering.businesslogic.shift.Shift shift) {
      String query = "SELECT * FROM ShiftAssignedTimes WHERE shift_id = " + shift.getId();

      PersistenceManager.executeQuery(query, new ResultHandler() {
        @Override
        public void handle(ResultSet rs) throws SQLException {
          User cook = User.loadUserById(rs.getInt("cook_id"));
          int minutes = rs.getInt("assigned_minutes");
          shift.assignedTimes.put(cook, minutes);
        }
      });
    }
  public static void updateShiftSaturated(Shift shift) {
    String shiftUpdate = "UPDATE Shifts SET saturated = true WHERE id = ?";
    PersistenceManager.executeBatchUpdate(shiftUpdate, 1, new BatchUpdateHandler() {
      @Override
      public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
        ps.setInt(1, shift.getId());
      }

      @Override
      public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
        // No IDs to handle for update
      }
    });
  }


  }

