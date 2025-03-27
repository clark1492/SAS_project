package catering.businesslogic.shift;

import catering.businesslogic.UseCaseLogicException;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ShiftManager {
    private ArrayList<Shift> shifts;

    public ShiftManager() {
      this.shifts = new ArrayList<>();
    }

    public ArrayList<Shift> getShifts() {
      return shifts;
    }

    public Shift createShift(LocalDateTime startTime, LocalDateTime endTime, String location, String type, String frequency, int serviceId) throws UseCaseLogicException {
      Shift shift = new Shift(startTime, endTime, location, type, frequency, serviceId);
      shifts.add(shift);
      return shift;
    }

    public void deleteShift(Shift shift) throws UseCaseLogicException {
      shifts.remove(shift);
      // Delete from database
    }

    public void updateShift(Shift shift) throws UseCaseLogicException {
      // Update in database
    }

    public void loadAllShifts() {
      String query = "SELECT * FROM Shift";
      PersistenceManager.executeQuery(query, new ResultHandler() {
        @Override
        public void handle(ResultSet rs) throws SQLException {
          Shift shift = new Shift(
                  rs.getInt("id"),
                  rs.getTimestamp("start_datetime").toLocalDateTime(),
                  rs.getTimestamp("end_datetime").toLocalDateTime(),
                  rs.getString("location"),
                  rs.getString("type"),
                  rs.getString("frequency"),
                  rs.getInt("service_id"),
                  rs.getTimestamp("frozen_date").toLocalDateTime()
          );
          shifts.add(shift);
        }
      });
    }

    public void saveAllShifts() {
      String shiftInsertSQL = "INSERT INTO Shift (start_datetime, end_datetime, location, type, frequency, service_id, frozen_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

      PersistenceManager.executeBatchUpdate(shiftInsertSQL, shifts.size(), new BatchUpdateHandler() {
        @Override
        public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
          Shift shift = shifts.get(batchCount);
          ps.setTimestamp(1, Timestamp.valueOf(shift.getStartTime()));
          ps.setTimestamp(2, Timestamp.valueOf(shift.getEndTime()));
          ps.setString(3, shift.getLocation());
          ps.setString(4, shift.getType());
          ps.setString(5, shift.getFrequency());
          ps.setInt(6, shift.getServiceId());
          ps.setTimestamp(7, Timestamp.valueOf(shift.getFrozenDate()));
        }

        @Override
        public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
          if (count >= 0 && shifts.size() > count) {
            shifts.get(count).setId(rs.getInt(1));
          }
        }
      });
    }
  }
