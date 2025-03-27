package catering.businesslogic;

import catering.businesslogic.event.EventManager;
import catering.businesslogic.summarySheet.SheetManager;
import catering.persistence.SheetPersistence;
import catering.businesslogic.menu.MenuManager;
import catering.businesslogic.recipe.RecipeManager;
import catering.businesslogic.shift.ShiftManager;
import catering.businesslogic.user.UserManager;

public class CatERing {
  private static CatERing singleInstance;
  private SheetManager sheetManager;
  private SheetPersistence kitchenTaskPersistence;

  public static CatERing getInstance() {
    if (singleInstance == null) {
      singleInstance = new CatERing();
    }
    return singleInstance;
  }

  private MenuManager menuMgr;
  private RecipeManager recipeMgr;
  private UserManager userMgr;
  private EventManager eventMgr;
  private SheetManager kitchenTaskMgr;
  private ShiftManager shiftMgr;

  private CatERing() {
    menuMgr = new MenuManager();
    recipeMgr = new RecipeManager();
    userMgr = new UserManager();
    eventMgr = new EventManager();
    kitchenTaskMgr = new SheetManager();
    shiftMgr = new ShiftManager();
    sheetManager = new SheetManager();

    kitchenTaskPersistence = new SheetPersistence();
    sheetManager.addEventReceiver(kitchenTaskPersistence);
  }

  public MenuManager getMenuManager() {
    return menuMgr;
  }

  public RecipeManager getRecipeManager() {
    return recipeMgr;
  }

  public UserManager getUserManager() {
    return userMgr;
  }

  public EventManager getEventManager() {
    return eventMgr;
  }

  public SheetManager getSheetManager() {
    return kitchenTaskMgr;
  }

  public ShiftManager getShiftManager() {
    return shiftMgr;
  }
}