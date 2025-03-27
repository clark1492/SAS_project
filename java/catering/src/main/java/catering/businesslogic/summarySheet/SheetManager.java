package catering.businesslogic.summarySheet;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.user.User;

import java.util.ArrayList;
import java.util.Optional;

public class SheetManager {

  private SummarySheet currentSheet;
  private ArrayList<SummarySheet> summarySheets;

  private ArrayList<SheetEventReceiver> eventReceivers;

  public SheetManager() {
    eventReceivers = new ArrayList<>();
    summarySheets = new ArrayList<>();
  }

  public SummarySheet openSummarySheet(ServiceInfo service) throws UseCaseLogicException {
    checkCurrentUserIsChef();

    Optional<SummarySheet> existingSheet = summarySheets.stream()
            .filter(s -> s.getService().equals(service))
            .findFirst();

    if (existingSheet.isPresent()) {
      currentSheet = existingSheet.get();
      notifySheetOpened(currentSheet);
      return currentSheet;
    }

    User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
    currentSheet = new SummarySheet(service, currentUser);
    summarySheets.add(currentSheet);
    notifySheetCreated(currentSheet);
    return currentSheet;
  }

  public void createTask(Recipe recipe) throws UseCaseLogicException {
    if (currentSheet == null) {
      throw new UseCaseLogicException();
    }
    Task task = new Task(recipe);
    this.currentSheet.addTask(task);
    notifyTaskCreated(task);
  }

  public void deleteTask(Task task) throws UseCaseLogicException {
    if (currentSheet == null) {
      throw new UseCaseLogicException();
    }
    if (!this.currentSheet.getTasks().contains(task)) {
      throw new UseCaseLogicException("Task not found in current sheet");
    }

    currentSheet.removeTask(task);
    this.notifyTaskDeleted(task);
  }

  public void sortTask(Task task, int position) throws UseCaseLogicException {
    if (currentSheet == null || currentSheet.getTaskPosition(task) < 0) throw new UseCaseLogicException();
    if (position < 0 || position >= currentSheet.getTaskCount()) throw new IllegalArgumentException();
    this.currentSheet.sortTask(task, position);

    this.notifyTaskSorted(currentSheet);
  }

  public void defineTask(Task task, int requiredTime, int portions) throws UseCaseLogicException {
    if (!this.currentSheet.getTasks().contains(task)) {
      throw new UseCaseLogicException("Task not found in current sheet");
    }
    if (requiredTime < 0 || portions < 0) throw new IllegalArgumentException();

    this.currentSheet.defineTask(task, requiredTime, portions);

    this.notifyTaskModified(task);
  }

  public void setTaskPrapared(Task task) throws UseCaseLogicException {
    if (currentSheet == null) {
      throw new UseCaseLogicException();
    }
    if (!currentSheet.getTasks().contains(task)) {
      throw new UseCaseLogicException("Task not found in current sheet");
    }
    task.setCompleted();
    this.notifyTaskCompleted(task);
  }

  public void markShiftSaturated(Shift shift) throws UseCaseLogicException {
    if (currentSheet == null) {
      throw new UseCaseLogicException();
    }
    if (shift.isSaturated()) {
      throw new UseCaseLogicException("Shift is already marked as saturated");
    }

    shift.setSaturated(true);
    notifyShiftSaturated(shift);
  }

  public void assignTask(Task task, User cook, Shift shift, Integer estimatedTime, Integer quantity) throws UseCaseLogicException {
    if (currentSheet == null) {
      throw new UseCaseLogicException();
    }

    // Set estimated time first so it can be used in validation
    if (estimatedTime != null) {
      task.setEstimatedTime(estimatedTime);
    }

    validateTaskAssignment(task, cook, shift);

    if (cook != null && shift != null) {
      task.assign(cook, shift);
      // Record the time assignment
      cook.assignToShift(shift, task.getEstimatedTime());
    }

    if (quantity != null) {
      task.setQuantity(quantity);
    }

    notifyTaskModified(task);
  }

  private void validateTaskAssignment(Task task, User cook, Shift shift)
          throws UseCaseLogicException {
    if (!currentSheet.getTasks().contains(task)) {
      throw new UseCaseLogicException("Task not found in current sheet");
    }

    if (shift != null && shift.isSaturated()) {
      throw new UseCaseLogicException("Cannot assign task to saturated shift");
    }

    if (cook != null) {
      if (!cook.isChef()) {
        throw new UseCaseLogicException("Selected user is not a cook");
      }

      if (shift != null) {
        // Check if task time fits in shift
        int estimatedTime = task.getEstimatedTime();
        if (!cook.isAvailableForShift(shift, estimatedTime)) {
          int shiftDuration = shift.getDuration();
          int assignedTime = cook.getAssignedTimeForShift(shift);
          throw new UseCaseLogicException(
                  String.format("Estimated time (%d minutes) exceeds available time in shift (remaining: %d minutes of %d total)",
                          estimatedTime, shiftDuration - assignedTime, shiftDuration)
          );
        }
      }
    }
  }

  private void notifyShiftSaturated(Shift shift) {
    for (SheetEventReceiver er : this.eventReceivers) {
      er.updateShiftSaturated(shift);
    }
  }

  private void notifyTaskCompleted(Task task) {
    for (SheetEventReceiver er : this.eventReceivers) {
      er.updateTaskCompleted(task);
    }
  }

  private void notifySheetCreated(SummarySheet ss) {
    for (SheetEventReceiver er : this.eventReceivers) {
      er.updateSummarySheetCreated(ss);
    }
  }

  private void notifyTaskCreated(Task t) {
    for (SheetEventReceiver er : this.eventReceivers) {
      er.updateTaskCreated(t);
    }
  }

  private void notifyTaskDeleted(Task task) {
    for (SheetEventReceiver er : this.eventReceivers) {
      er.updateTaskDeleted(task);
    }
  }

  private void notifyTaskModified(Task t) {
    for (SheetEventReceiver er : this.eventReceivers) {
      er.updateTaskModified(t);
    }
  }

  private void notifyTaskSorted(SummarySheet ss) {
    for (SheetEventReceiver er : this.eventReceivers) {
      er.updateTaskSorted(ss);
    }
  }

  private void notifySheetOpened(SummarySheet ss) {
    for (SheetEventReceiver er : this.eventReceivers) {
      er.updateSummarySheetOpened(ss);
    }
  }


  public SummarySheet getCurrentSheet() {
    return currentSheet;
  }

  public void setCurrentSheet(SummarySheet currentSheet) {
    this.currentSheet = currentSheet;
  }

  public void addEventReceiver(SheetEventReceiver rec) {
    this.eventReceivers.add(rec);
  }

  public void removeEventReceiver(SheetEventReceiver rec) {
    this.eventReceivers.remove(rec);
  }

  private void checkCurrentUserIsChef() throws UseCaseLogicException {
    User currentUser = CatERing.getInstance().getUserManager().getCurrentUser();
    if (!currentUser.isChef()) {
      throw new UseCaseLogicException("Only chefs can manage kitchen tasks");
    }
  }
}