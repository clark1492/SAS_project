package catering.test;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.summarySheet.SummarySheet;
import catering.businesslogic.recipe.Recipe;

// Tests the basic kitchen task creation and summary sheet management
public class TestKitchenTasksBase {
    public static void main(String[] args) {
        try {
            System.out.println("TEST FAKE LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");
            System.out.println(CatERing.getInstance().getUserManager().getCurrentUser());

            System.out.println("\nTEST OPEN SUMMARY SHEET");
            EventInfo event = CatERing.getInstance().getEventManager().getEventInfo().get(0);
            ServiceInfo service = event.getServices().get(0);
            SummarySheet sheet = CatERing.getInstance().getSheetManager().openSummarySheet(service);
            System.out.println("Summary sheet opened for service: " + service);

            System.out.println("\nTEST ADD TASK");
            System.out.println("Tasks in summary sheet before adding task: " + sheet.getTasks());
            int numTasksBefore = sheet.getTasks().size();
            Recipe recipe = CatERing.getInstance().getRecipeManager().getRecipes().get(0);
            CatERing.getInstance().getSheetManager().createTask(recipe);
            System.out.println("Tasks in summary sheet after adding task: " + sheet.getTasks());
            int numTasksAfter = sheet.getTasks().size();
            if (numTasksAfter == numTasksBefore + 1) {
                System.out.println("TEST PASSED: Task added successfully");
            } else {
                System.out.println("TEST FAILED: Task not added");
            }


        } catch (UseCaseLogicException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}