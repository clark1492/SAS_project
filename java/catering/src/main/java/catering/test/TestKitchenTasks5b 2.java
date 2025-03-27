package catering.test;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.summarySheet.SummarySheet;
import catering.businesslogic.summarySheet.Task;

// Tests Mark preparation as ready
public class TestKitchenTasks5b {
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
            Task task = sheet.getTasks().get(0); // Get the task we just added
            System.out.println("Task added:" + sheet.getTasks());

            System.out.println("\nTEST MARK TASK AS READY");
            CatERing.getInstance().getSheetManager().setTaskPrapared(task);
            System.out.println("Task marked as ready: " + task);

            // Verify successful update and print the test result
            if (task.isCompleted()) {
                System.out.println("TEST PASSED: Task marked as ready");
            } else {
                System.out.println("TEST FAILED: Task not marked as ready");
            }

        } catch (UseCaseLogicException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}