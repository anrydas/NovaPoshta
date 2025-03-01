package das.tools.np.gui.menu;

import das.tools.np.gui.FilteringModeService;
import das.tools.np.gui.ViewModeService;
import das.tools.np.gui.WindowListService;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.enums.FilteringMode;
import das.tools.np.gui.enums.ViewMode;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionCheck;
import org.controlsfx.control.action.ActionGroup;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.controlsfx.control.action.ActionUtils.ACTION_SEPARATOR;

@Service
@Slf4j
public class ActionService {
    private static final Map<ApplicationAction,Action> ALL_ACTIONS = new HashMap<>();
    private final FxWeaver fxWeaver;
    private final LocalizeResourcesService localizeService;
    private final ViewModeService viewModeService;
    private final FilteringModeService filteringModeService;
    private final GlyphFont glyphFont;
    private final WindowListService windowService;
    private MainController mainController;

    public ActionService(FxWeaver fxWeaver, LocalizeResourcesService localizeService, ViewModeService viewModeService, FilteringModeService filteringModeService, GlyphFont glyphFont, WindowListService windowService) {
        this.fxWeaver = fxWeaver;
        this.localizeService = localizeService;
        this.viewModeService = viewModeService;
        this.filteringModeService = filteringModeService;
        this.glyphFont = glyphFont;
        this.windowService = windowService;
    }

    public void initActions() {
        mainController = fxWeaver.loadController(MainController.class);
        ALL_ACTIONS.put(ApplicationAction.NM_ADD, getRegularAction(localizeService.getLocalizedResource("menu.number.add"), glyphFont.create(FontAwesome.Glyph.PLUS),
                        e -> mainController.addNumber(), new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.NM_UPDATE, getRegularAction(localizeService.getLocalizedResource("menu.number.update"), glyphFont.create(FontAwesome.Glyph.REPEAT),
                e -> mainController.updateNumber(), new KeyCodeCombination(KeyCode.F5)));
        ALL_ACTIONS.put(ApplicationAction.NM_UPDATE_UNCOMPLETED, getRegularAction(localizeService.getLocalizedResource("menu.number.updateUncompleted"), glyphFont.create(FontAwesome.Glyph.RETWEET),
                e -> mainController.updateUncompleted(), new KeyCodeCombination(KeyCode.F5, KeyCombination.SHIFT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.NM_MOVE_TO_ARCHIVE, getRegularAction(localizeService.getLocalizedResource("menu.number.moveToArchive"), glyphFont.create(FontAwesome.Glyph.COMPRESS),
                e -> mainController.moveToArchive(), null));
        ALL_ACTIONS.put(ApplicationAction.NM_ADD_TO_CUSTOM_LIST, getRegularAction(localizeService.getLocalizedResource("menu.number.addToCustom"), glyphFont.create(FontAwesome.Glyph.LIST_ALT),
                e -> mainController.addToCustomList(), null));
        ALL_ACTIONS.put(ApplicationAction.NM_RESTORE_FROM_ARCHIVE, getRegularAction(localizeService.getLocalizedResource("menu.number.restoreFromArchive"), glyphFont.create(FontAwesome.Glyph.EXPAND),
                e -> mainController.restoreFromArchive(), null));
        ALL_ACTIONS.put(ApplicationAction.NM_EXIT, getRegularAction(localizeService.getLocalizedResource("menu.number.exit"), glyphFont.create(FontAwesome.Glyph.CLOSE).color(Color.RED),
                e -> mainController.applicationExit(), new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN)));
        // Edit:
        ALL_ACTIONS.put(ApplicationAction.ED_NEW_GROUP, getRegularAction(localizeService.getLocalizedResource("menu.edit.newGroup"), glyphFont.create(FontAwesome.Glyph.PLUS_SQUARE_ALT),
                e -> mainController.addNewGroup(), new KeyCodeCombination(KeyCode.G, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.ED_EDIT_GROUPS, getRegularAction(localizeService.getLocalizedResource("menu.edit.editGroups"), glyphFont.create(FontAwesome.Glyph.PLUS_SQUARE),
                e -> mainController.editGroups(), new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.ED_PHONES, getRegularAction(localizeService.getLocalizedResource("menu.edit.editPhones"), glyphFont.create(FontAwesome.Glyph.PHONE_SQUARE),
                e -> mainController.editPhones(), new KeyCodeCombination(KeyCode.P, KeyCombination.ALT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.ED_SEARCH_OPTIONS, getRegularAction(localizeService.getLocalizedResource("menu.edit.editSearchOptions"), glyphFont.create(FontAwesome.Glyph.LIST_OL),
                e -> mainController.editSearchOptions(), new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.ED_SEARCH, getRegularAction(localizeService.getLocalizedResource("menu.edit.search"), glyphFont.create(FontAwesome.Glyph.SEARCH),
                e -> mainController.search(), new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.ED_CONFIGURATION, getRegularAction(localizeService.getLocalizedResource("menu.edit.config"), glyphFont.create(FontAwesome.Glyph.GEARS),
                e -> mainController.showConfigWindow(), new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN)));
        // View actions:
        //   View mode
        initViewModeActions();
        //   Filter mode
        initFilterModeActions();
        //   Other
        ALL_ACTIONS.put(ApplicationAction.VW_DETAILED_INFO, getRegularAction(localizeService.getLocalizedResource("menu.view.detail"), glyphFont.create(FontAwesome.Glyph.INFO_CIRCLE),
                e -> mainController.showInfoWindow(mainController.getActiveSelectedNumberStr()), new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.VW_SHOW_LOG, getCheckAction(localizeService.getLocalizedResource("menu.view.showLog"), mainController.isLogVisible(), glyphFont.create(FontAwesome.Glyph.LIST),
                e -> mainController.toggleLogPanel(), new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN)));
        // Plugins actions
        ALL_ACTIONS.put(ApplicationAction.PG_LOAD_PLUGINS, getRegularAction(localizeService.getLocalizedResource("menu.plugins.loadPlugins"), glyphFont.create(FontAwesome.Glyph.DOWNLOAD),
                e -> mainController.loadPlugins(), null));
        // Windows actions
        ALL_ACTIONS.put(ApplicationAction.WN_CUSTOM_LIST, getRegularAction(localizeService.getLocalizedResource("menu.windows.customList"), glyphFont.create(FontAwesome.Glyph.LIST_ALT),
                e -> mainController.showCustomNumbersWindow(), null));
        ALL_ACTIONS.put(ApplicationAction.WN_ARCHIVED_LIST, getRegularAction(localizeService.getLocalizedResource("menu.windows.archivedList"), glyphFont.create(FontAwesome.Glyph.ARCHIVE),
                e -> mainController.showArchivedNumbersWindow(""), null));
        ALL_ACTIONS.put(ApplicationAction.WN_CASCADE, getRegularAction(localizeService.getLocalizedResource("menu.windows.cascade"), glyphFont.create(FontAwesome.Glyph.ANGLE_DOUBLE_UP),
                e -> windowService.showAllCascade(), null));
        ALL_ACTIONS.put(ApplicationAction.WN_CLOSE_ALL, getRegularAction(localizeService.getLocalizedResource("menu.windows.closeAll"), glyphFont.create(FontAwesome.Glyph.ANGLE_DOWN),
                e -> windowService.closeAll(), null));
        // Help actions
        ALL_ACTIONS.put(ApplicationAction.HP_ABOUT, getRegularAction(localizeService.getLocalizedResource("menu.help.about"), glyphFont.create(FontAwesome.Glyph.QUESTION),
                e -> mainController.about(), null));
        // LOG actions
        ALL_ACTIONS.put(ApplicationAction.LOG_COPY, getRegularAction(localizeService.getLocalizedResource("menu.log.copy"), glyphFont.create(FontAwesome.Glyph.COPY),
                e -> mainController.copyLog(), null));
        ALL_ACTIONS.put(ApplicationAction.LOG_CLEAR, getRegularAction(localizeService.getLocalizedResource("menu.log.clear"), glyphFont.create(FontAwesome.Glyph.CLOSE),
                e -> mainController.clearLog(), null));
    }

    private Action getAction(ApplicationAction action) {
        return ALL_ACTIONS.get(action);
    }

    public Action getNumberDetailAction() {
        return getAction(ApplicationAction.VW_DETAILED_INFO);
    }
    public Action getShowLogAction() {
        return getAction(ApplicationAction.VW_SHOW_LOG);
    }
    public Action getExitAction() {
        return getAction(ApplicationAction.NM_EXIT);
    }
    public Action getRestoreAction() {
        return getAction(ApplicationAction.NM_RESTORE_FROM_ARCHIVE);
    }

    public List<ActionGroup> getNumberMenuActionGroup() {
        return List.of(new ActionGroup(localizeService.getLocalizedResource("menu.group.number"),
                getNumberMenuActions()
        ));
    }

    private List<Action> getNumberMenuActions() {
        return List.of(
                getAction(ApplicationAction.NM_ADD),
                getAction(ApplicationAction.NM_UPDATE),
                getAction(ApplicationAction.NM_UPDATE_UNCOMPLETED),
                ACTION_SEPARATOR,
                getAction(ApplicationAction.NM_ADD_TO_CUSTOM_LIST),
                getAction(ApplicationAction.NM_MOVE_TO_ARCHIVE),
                ACTION_SEPARATOR,
                getAction(ApplicationAction.NM_EXIT)
        );
    }

    public List<Action> getNumberToolbarActions() {
        return List.of(
                getAction(ApplicationAction.NM_ADD),
                getAction(ApplicationAction.NM_UPDATE),
                getAction(ApplicationAction.NM_UPDATE_UNCOMPLETED),
                getAction(ApplicationAction.NM_ADD_TO_CUSTOM_LIST),
                getAction(ApplicationAction.NM_MOVE_TO_ARCHIVE)
        );
    }

    public List<Action> getEditMenuActions() {
        return Arrays.asList(
                getAction(ApplicationAction.ED_NEW_GROUP),
                getAction(ApplicationAction.ED_EDIT_GROUPS),
                getAction(ApplicationAction.ED_PHONES),
                getAction(ApplicationAction.ED_SEARCH_OPTIONS),
                ACTION_SEPARATOR,
                getAction(ApplicationAction.ED_SEARCH),
                ACTION_SEPARATOR,
                getAction(ApplicationAction.ED_CONFIGURATION)
        );
    }

    public List<Action> getWindowsMenuActions() {
        return Arrays.asList(
                getAction(ApplicationAction.WN_CUSTOM_LIST),
                getAction(ApplicationAction.WN_ARCHIVED_LIST),
                ACTION_SEPARATOR,
                getAction(ApplicationAction.WN_CASCADE),
                getAction(ApplicationAction.WN_CLOSE_ALL)
        );
    }

    public Action getLoadPluginAction() {
        return getAction(ApplicationAction.PG_LOAD_PLUGINS);
    }

    public List<Action> getLogMenuActions() {
        return Arrays.asList(
                getAction(ApplicationAction.LOG_COPY),
                getAction(ApplicationAction.LOG_CLEAR)
        );
    }

    public Action getAboutAction() {
        return getAction(ApplicationAction.HP_ABOUT);
    }

    private Action getRegularAction(String text, Node image, Consumer<ActionEvent> handler, KeyCodeCombination keyCode) {
        return new AppAction(text, image, handler, keyCode);
    }

    private CheckAppAction getCheckAction(String text, boolean checked, Node image, Consumer<ActionEvent> handler, KeyCodeCombination keyCode) {
        return new CheckAppAction(text, checked,image, handler, keyCode);
    }

    public ActionGroup getViewModeGroup() {
        initViewModeActions();
        return new ActionGroup(localizeService.getLocalizedResource("menu.group.viewMode"),
                getAction(ApplicationAction.VM_NUMBERS),
                getAction(ApplicationAction.VM_GROUPS),
                getAction(ApplicationAction.VM_CALENDAR_CREATE),
                getAction(ApplicationAction.VM_CALENDAR_UPDATE)
        );
    }

    private void initViewModeActions() {
        ALL_ACTIONS.put(ApplicationAction.VM_NUMBERS, getViewModeAction(ViewMode.VM_NUMBERS, new KeyCodeCombination(KeyCode.N, KeyCombination.ALT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.VM_GROUPS, getViewModeAction(ViewMode.VM_GROUPS, new KeyCodeCombination(KeyCode.G, KeyCombination.ALT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.VM_CALENDAR_CREATE, getViewModeAction(ViewMode.VM_CALENDAR_CREATE, new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.VM_CALENDAR_UPDATE, getViewModeAction(ViewMode.VM_CALENDAR_UPDATE, new KeyCodeCombination(KeyCode.U, KeyCombination.ALT_DOWN)));
    }

    private CheckAppAction getViewModeAction(ViewMode viewMode, KeyCodeCombination keyCode) {
        return new CheckAppAction(viewModeService.getModeName(viewMode), viewModeService.getCurrentMode().equals(viewMode), null,
                e -> mainController.updateViewMode(viewMode), keyCode);
    }

    ActionGroup getFilterModeGroup() {
        initFilterModeActions();
        return new ActionGroup(localizeService.getLocalizedResource("menu.group.filterMode"),
            getAction(ApplicationAction.FM_ALL),
            getAction(ApplicationAction.FM_IN),
            getAction(ApplicationAction.FM_OUT),
            getAction(ApplicationAction.FM_UNCOMPLETED)
        );
    }

    private void initFilterModeActions() {
        ALL_ACTIONS.put(ApplicationAction.FM_ALL, getFilterModeAction(FilteringMode.ALL, new KeyCodeCombination(KeyCode.A, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.FM_IN, getFilterModeAction(FilteringMode.INBOUND, new KeyCodeCombination(KeyCode.I, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.FM_OUT, getFilterModeAction(FilteringMode.OUTBOUND, new KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN)));
        ALL_ACTIONS.put(ApplicationAction.FM_UNCOMPLETED, getFilterModeAction(FilteringMode.UNCOMPLETED, new KeyCodeCombination(KeyCode.U, KeyCombination.ALT_DOWN, KeyCombination.SHIFT_DOWN)));
    }

    private CheckAppAction getFilterModeAction(FilteringMode mode, KeyCodeCombination keyCode) {
        return new CheckAppAction(filteringModeService.getModeName(mode), filteringModeService.getCurrentMode().equals(mode), null,
                e -> mainController.updateFilterMode(mode), keyCode);
    }

    public static class AppAction extends Action {
        public AppAction(String text, Node image, Consumer<ActionEvent> eventHandler, KeyCodeCombination key) {
            super(text, eventHandler);
            setGraphic(image);
            setAccelerator(key);
        }
    }

    @ActionCheck
    private static class CheckAppAction extends Action {
        public CheckAppAction(String text, boolean checked, Node image, Consumer<ActionEvent> eventHandler, KeyCodeCombination key) {
            super(text, eventHandler);
            setGraphic(image);
            setSelected(checked);
            setAccelerator(key);
        }
    }
}
