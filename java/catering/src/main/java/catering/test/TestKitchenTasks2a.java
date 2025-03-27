package catering.test;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.summarySheet.SummarySheet;
import catering.businesslogic.summarySheet.Task;

// Tests Remove task from summery sheet
public class TestKitchenTasks2a {
  public static void main(String[] args) {
    try {
      System.out.println("TEST FAKE LOGIN");
      CatERing.getInstance().getUserManager().fakeLogin("Lidia");
      System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

      System.out.println("\nTEST OPEN SUMMARY SHEET");
      EventInfo event = CatERing.getInstance().getEventManager().getEventInfo().get(0);
      ServiceInfo service = event.getServices().get(0);
      SummarySheet sheet = CatERing.getInstance().getSheetManager().openSummarySheet(service);
      System.out.println("Open Summary sheet: " + sheet.toString());
      System.out.println("\nTEST ADD TASK");
      Recipe recipe = CatERing.getInstance().getRecipeManager().getRecipes().get(0);
      CatERing.getInstance().getSheetManager().createTask(recipe);


      System.out.println("Tasks before removal: " + sheet.getTasks());

      // Get the task reference from the sheet
      Task task = sheet.getTasks().get(0);
      int numTaskBefore = sheet.getTasks().size();

      System.out.println("\nTEST REMOVE TASK");
      CatERing.getInstance().getSheetManager().deleteTask(task);
      System.out.println("Tasks after removal: " + sheet.getTasks());
      int numTaskAfter = sheet.getTasks().size();
      if (numTaskAfter == numTaskBefore - 1) {
        System.out.println("TEST PASSED: Task removed successfully");
      } else {
        System.out.println("TEST FAILED: Task not removed");
      }

    } catch (UseCaseLogicException e) {
      System.out.println("TEST FAILED: ERROR: " + e.getMessage());
    }
  }
}