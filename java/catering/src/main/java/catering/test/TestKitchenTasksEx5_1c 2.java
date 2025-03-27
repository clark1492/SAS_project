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

// Tests Time estimate exceeds available time in shift
public class TestKitchenTasksEx5_1c {
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
            // Create a 2-hour shift
            Shift shift = CatERing.getInstance().getShiftManager().createShift(
                    LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                    "Main Kitchen", "Preparation", "Once", service.getId()
            );

            User cook = CatERing.getInstance().getUserManager().getAllUsers().get(1);
            System.out.println("\nShift duration:"+ shift.getDuration());

            System.out.println("Attempting to assign a 20-hour task with time exceeding shift duration...");
            // Try to assign a 20-hour task to a 2-hour shift
            CatERing.getInstance().getSheetManager().assignTask(task, cook, shift, 20*60, 10);

        } catch (UseCaseLogicException e) {
            System.out.println("TEST PASSED: Caught expected exception - " + e.getMessage());
        }
    }
}
