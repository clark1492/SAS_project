package catering.businesslogic.summarySheet;

import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.user.User;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Task {

  private int id;


  private SummarySheet summarySheet;
  private Recipe recipe;
  private User cook;
  private Shift shift;

  private int requiredTime; // in minutes
  private int quantity;
  private int portions;

  public User getCook() {
    return cook;
  }

  public Shift getShift() {
    return shift;
  }

  private boolean completed;

  private int estimatedTime; // in minutes

  public SummarySheet getSummarySheet() {
    return summarySheet;
  }

  public int getEstimatedTime() {
    return estimatedTime;
  }

  public void setEstimatedTime(int estimatedTime) {
    this.estimatedTime = estimatedTime;
  }

  public Task(Recipe recipe) {
    this.recipe = recipe;
    this.completed = false;
  }

  @Override
  public String toString() {
    return "Compito (" +
            "id=" + id +
            ", Ricetta=" + recipe +
            ", Cuoco=" + cook +
            ", Turno=" + shift +
            ", tempo richiesto=" + requiredTime +
            ", quantità =" + quantity +
            ", porzini =" + portions +
            ", pronto =" + completed +
            ", tempo stimato =" + estimatedTime +
            '}';
  }

  public void setRequiredTime(int time) {
    this.requiredTime = time;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setCompleted() {
    this.completed = true;
  }

  public boolean isCompleted() {
    return this.completed;
  }

  public void setPortions(int portions) {
    this.portions = portions;
  }

  public void assign(User cook, Shift shift) {
    this.cook = cook;
    this.shift = shift;
  }

  public int getId() {
    return id;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public int getRequiredTime() {
    return requiredTime;
  }

  public void setId(int id) {
    this.id = id;
  }


  public int getQuantity() {
    return quantity;
  }

  // STATIC METHODS FOR PERSISTENCE

  public static void createTask(Task task) {
    String taskInsert = "INSERT INTO KitchenTasks (summary_sheet_id, recipe_id, estimated_time, quantity, status) VALUES (?, ?, ?, ?, ?)";
    PersistenceManager.executeBatchUpdate(taskInsert, 1, new BatchUpdateHandler() {
      @Override
      public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
        ps.setInt(1, task.getSummarySheet().getId());
        ps.setInt(2, task.getRecipe().getId());
        ps.setInt(3, task.getEstimatedTime());
        ps.setInt(4, task.getQuantity());
        ps.setBoolean(5, task.isCompleted());
      }

      @Override
      public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
        if (count == 0) {
          task.setId(rs.getInt(1));
        }
      }
    });
  }

  public static void deleteTask(Task task) {
    String taskDelete = "DELETE FROM KitchenTasks WHERE id = " + task.getId();
    PersistenceManager.executeUpdate(taskDelete);
  }

  public static void modifyTask(Task task) {
    String taskUpdate = "UPDATE KitchenTasks SET cook_id = ?, shift_id = ?, " +
            "estimated_time = ?, quantity = ?, status = ? WHERE id = ?";
    PersistenceManager.executeBatchUpdate(taskUpdate, 1, new BatchUpdateHandler() {
      @Override
      public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
        User cook = task.getCook();
        Shift shift = task.getShift();

        if (cook != null) ps.setInt(1, cook.getId());
        else ps.setNull(1, java.sql.Types.INTEGER);

        if (shift != null) ps.setInt(2, shift.getId());
        else ps.setNull(2, java.sql.Types.INTEGER);

        ps.setInt(3, task.getEstimatedTime());
        ps.setInt(4, task.getQuantity());
        ps.setBoolean(5, task.isCompleted());
        ps.setInt(6, task.getId());
      }

      @Override
      public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
        // No IDs to handle for update
      }
    });
  }

  public static void taskCompleted(Task task) {
    String readyUpdate = "UPDATE KitchenTasks SET status = ?, cook_id = NULL, shift_id = NULL WHERE id = ?";
    PersistenceManager.executeBatchUpdate(readyUpdate, 1, new BatchUpdateHandler() {
      @Override
      public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
        ps.setBoolean(1, true);
        ps.setInt(2, task.getId());
      }

      @Override
      public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
        // No IDs to handle for update
      }
    });
  }


}
