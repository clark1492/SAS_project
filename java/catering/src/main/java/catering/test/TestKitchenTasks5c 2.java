package catering.test;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.shift.Shift;
import catering.businesslogic.summarySheet.SummarySheet;

import java.time.LocalDateTime;

// Tests Mark shift as saturated
public class TestKitchenTasks5c {
    public static void main(String[] args) {
        try {
            System.out.println("TEST FAKE LOGIN");
            CatERing.getInstance().getUserManager().fakeLogin("Lidia");

            System.out.println("\nTEST OPEN SUMMARY SHEET");
            EventInfo event = CatERing.getInstance().getEventManager().getEventInfo().get(0);
            ServiceInfo service = event.getServices().get(0);
            SummarySheet sheet = CatERing.getInstance().getSheetManager().openSummarySheet(service);
            System.out.println("Open Summary sheet: " + sheet.toString());
            System.out.println("\nTEST MARK SHIFT AS SATURATED");
            Shift shift = CatERing.getInstance().getShiftManager().createShift(
                    LocalDateTime.now(), LocalDateTime.now().plusHours(4),
                    "Main Kitchen", "Preparation", "Once", service.getId()
            );
            CatERing.getInstance().getSheetManager().markShiftSaturated(shift);
            System.out.println("Shift marked as saturated: " + shift);
            // Verify successful update and print the test result
            if (shift.isSaturated()) {
                System.out.println("TEST PASSED: Shift marked as saturated");
            } else {
                System.out.println("TEST FAILED: Shift not marked as saturated");
            }

        } catch (UseCaseLogicException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}