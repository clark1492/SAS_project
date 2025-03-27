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

// Tests AssignTask
public class TestKitchenTasks5a {
    private static User createTestCook() {
        User cook = new User();
        cook.addRole(User.Role.CUOCO);
        return cook;
    }
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
            System.out.println("AddedTask: " + sheet.getTasks());
            User cook = CatERing.getInstance().getUserManager().getAllUsers().get(1); // Get a cook
            Shift shift = CatERing.getInstance().getShiftManager().createShift(
                    LocalDateTime.now(), LocalDateTime.now().plusHours(4),
                    "Main Kitchen", "Preparation", "Once", service.getId()
            );
            System.out.println("\nTEST ASSIGN TASK");
            System.out.println("Assigning task to cook and shift");
            CatERing.getInstance().getSheetManager().assignTask(task, cook, shift, 120, 4);
            System.out.println("Updated task: " + task);
            // Verify successful update and print the test result
            if (task.getCook().equals(cook) && task.getShift().equals(shift) &&
                    task.getEstimatedTime() == 120) {
                System.out.println("\nTEST PASSED: Task updated successfully");
            } else {
                System.out.println("\nTEST FAILED: Task not updated");
            }

        } catch (UseCaseLogicException e) {
            System.out.println("TEST FAILED - ERROR: " + e.getMessage());
        }
    }
}