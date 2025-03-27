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
//Tests Assign task to cook during overlapping shift
public class TestKitchenTasksEx5_1b {
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
            Task task = sheet.getTasks().get(0);
            System.out.println("\nTask to add:"+ task);
            // Create shifts with overlapping times
            LocalDateTime now = LocalDateTime.now();
            Shift firstShift = CatERing.getInstance().getShiftManager().createShift(
                    now, now.plusHours(4),
                    "Main Kitchen", "Preparation", "Once", service.getId()
            );

            // Get a cook and assign them to the first shift
            User cook = CatERing.getInstance().getUserManager().getAllUsers().get(1);
            cook.assignToShift(firstShift, 120); // Assign 2 hours of work

            System.out.println("\nFirst shift:"+ firstShift);
            // Create second overlapping shift
            Shift secondShift = CatERing.getInstance().getShiftManager().createShift(
                    now.plusHours(2), now.plusHours(6), // Overlaps with firstShift
                    "Main Kitchen", "Preparation", "Once", service.getId()
            );
            System.out.println("\nSecond shift:"+ secondShift);
            System.out.println("Attempting to assign task to cook during overlapping shift...");
            CatERing.getInstance().getSheetManager().assignTask(task, cook, secondShift, 60, 10);
            System.out.println("TEST FAILED: Was able to assign task to unavailable cook");

        } catch (UseCaseLogicException e) {
            System.out.println("TEST PASSED: Caught expected exception - " + e.getMessage());
        }
    }
}