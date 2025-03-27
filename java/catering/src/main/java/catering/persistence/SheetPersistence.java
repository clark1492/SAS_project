package catering.persistence;

import catering.businesslogic.summarySheet.*;
import catering.businesslogic.shift.Shift;

public class SheetPersistence implements SheetEventReceiver {

  @Override
  public void updateSummarySheetCreated(SummarySheet sheet) {
   SummarySheet.createSummarySheet(sheet);
  }

  @Override
  public void updateTaskCreated(Task task) {
    Task.createTask(task);
  }

  @Override
  public void updateTaskDeleted(Task task) {
    Task.deleteTask(task);
  }

  @Override
  public void updateTaskModified(Task task) {
    Task.modifyTask(task);
  }

  @Override
  public void updateTaskCompleted(Task task) {
    Task.taskCompleted(task);
  }

  @Override
  public void updateShiftSaturated(Shift shift) {
   Shift.updateShiftSaturated(shift);
  }

  @Override
  public void updateSummarySheetOpened(SummarySheet sheet) {
    // Nothing to persist for opening a sheet
  }
  @Override
  public void updateTaskSorted(SummarySheet sheet)
  {};
}