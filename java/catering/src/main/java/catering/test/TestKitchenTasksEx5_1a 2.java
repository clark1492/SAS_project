package catering.test;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.summarySheet.SummarySheet;
import catering.businesslogic.summarySheet.Task;
import catering.businesslogic.user.User;

import java.time.LocalDateTime;

// Tests Attempt to assign task to saturated shift
public class TestKitchenTasksEx5_1a {
  public static void main(String[] args) {
    try {
      System.out.println("TEST FAKE LOGIN");
      CatERing.getInstance().getUserManager().fakeLogin("Lidia");

      System.out.println("\nTEST ASSIGN TO SATURATED SHIFT");
      EventInfo event = CatERing.getInstance().getEventManager().getEventInfo().get(0);
      ServiceInfo service = event.getServices().get(0);
      SummarySheet sheet = CatERing.getInstance().getSheetManager().openSummarySheet(service);
      System.out.println("Open Summary sheet: " + sheet.toString());
      Recipe recipe = CatERing.getInstance().getRecipeManager().getRecipes().get(0);
      CatERing.getInstance().getSheetManager().createTask(recipe);
      Task task = sheet.getTasks().get(0);
      System.out.println("\nTask to add:"+ task);
      Shift shift = CatERing.getInstance().getShiftManager().createShift(
              LocalDateTime.now(), LocalDateTime.now().plusHours(4),
              "Main Kitchen", "Preparation", "Once", service.getId()
      );
      CatERing.getInstance().getSheetManager().markShiftSaturated(shift);

      User cook = CatERing.getInstance().getUserManager().getAllUsers().get(4);
      System.out.println("\nSaturated Shift :"+ shift);
      System.out.println("\nAttempting to assign task to saturated shift...");
      CatERing.getInstance().getSheetManager().assignTask(task, cook, shift, 60, 10);

    } catch (UseCaseLogicException e) {
      System.out.println("TEST PASSED: Caught expected exception - " + e.getMessage());
    }
  }
}