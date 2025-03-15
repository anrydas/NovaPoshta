package das.tools.np.gui.controllers;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.SimpleNumber;
import das.tools.np.entity.plugin.PluginInfo;
import das.tools.np.gui.enums.FilteringMode;
import das.tools.np.gui.enums.ViewMode;
import javafx.scene.image.Image;
import javafx.stage.WindowEvent;

public interface MainController {
    int LOG_HIDDEN_POSITION = 1;
    double LOG_SHOWN_POSITION = 0.75;
    double LOG_MAX_VISIBLE_POSITION = 0.9;
    String APPLICATION_TITLE = "Nova Poshta Desktop: Tracker and Organizer";
    String NEED_TO_BE_CLOSED_ON_EXIT = "NeedToBeClosedWindowOnApplicationExit";

    Image getWindowIcon();

    void setSelectedNumber(String number);

    String getActiveSelectedNumberStr();

    CargoNumber getActiveSelectedNumber();

    void updateViewMode(ViewMode viewMode);

    void updateFilterMode(FilteringMode mode);

    void showConfigWindow();

    void loadPlugins();

    void showInfoWindow(String number);

    void showCustomNumbersWindow();

    void showArchivedNumbersWindow(String number);

    void updateNumberLists(SimpleNumber number);

    void addNumber();

    void applicationExit();

    void moveToArchive();

    void restoreFromArchive();

    void addToCustomList();

    void pluginMenuItemClicked(PluginInfo pluginInfo);

    void about();

    void updateNumber();

    void updateUncompleted();

    void addNewGroup();

    void editGroups();

    void editPhones();

    void editSearchOptions();

    void search();

    void moveToGroup(long newGroupId);

    void onShowingStage();

    void onClosingStage(WindowEvent e);

    void scheduledNumbersUpdate(boolean showDialog);

    void toggleLogPanel();

    boolean isLogVisible();

    void logInfo(String message);

    void logWarn(String message);

    void logError(String message);

    void copyLog();

    void clearLog();
}
