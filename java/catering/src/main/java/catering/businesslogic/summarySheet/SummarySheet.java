package catering.businesslogic.summarySheet;


import catering.businesslogic.event.ServiceInfo;

import catering.businesslogic.user.User;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SummarySheet {

  private static Map<Integer, SummarySheet> loadedSheets = new HashMap();
  private int id;
  private ServiceInfo service;
  private User owner;
  private ArrayList<Task> tasks;

  public SummarySheet(ServiceInfo service, User owner) {

    this.service = service;
    this.owner = owner;
    this.tasks = new ArrayList<>();

  }

  public void addTask(Task task) {
    tasks.add(task);
  }

  public void removeTask(Task task) {
    tasks.remove(task);
  }

  public ArrayList<Task> getTasks() {
    return tasks;
  }

  public ServiceInfo getService() {
    return service;
  }

  public User getOwner() {
    return owner;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTaskPosition(Task task) {
    return this.tasks.indexOf(task);
  }

  public int getTaskCount() {
    return tasks.size();
  }

  public void sortTask(Task task, int position) {
    tasks.remove(task);
    tasks.add(position, task);
  }

  public void defineTask(Task task, int requiredTime, int portions) {

  }

  // STATIC METHODS FOR PERSISTENCE
  public static void createSummarySheet(SummarySheet sheet) {
    String sheetInsert = "INSERT INTO SummarySheets (service_id, owner_id) VALUES (?, ?)";
    PersistenceManager.executeBatchUpdate(sheetInsert, 1, new BatchUpdateHandler() {
      @Override
      public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
        ps.setInt(1, sheet.getService().getId());
        ps.setInt(2, sheet.getOwner().getId());
      }

      @Override
      public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
        if (count == 0) {
          sheet.setId(rs.getInt(1));
        }
      }
    });
  }

  @Override
  public String toString() {
    return "Foglio Riepilogativo{" +
            "id=" + id +
            ", Servizio=" + service +
            ", Creatore=" + owner +
            ", Compiti=" + tasks +
            '}';
  }


}
