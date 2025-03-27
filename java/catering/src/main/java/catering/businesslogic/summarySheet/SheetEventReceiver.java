package catering.businesslogic.summarySheet;

import catering.businesslogic.shift.Shift;

public interface SheetEventReceiver {
  void updateSummarySheetCreated(SummarySheet sheet);
  void updateSummarySheetOpened(SummarySheet sheet);
  void updateTaskCreated(Task task);
  void updateTaskDeleted(Task task);
  void updateTaskSorted(SummarySheet sheet);
  void updateTaskModified(Task task);
  void updateTaskCompleted(Task task);
  void updateShiftSaturated(Shift shift);
}
