package das.tools.np.gui.menu;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.Group;
import das.tools.np.entity.plugin.PluginInfo;
import das.tools.np.gui.WindowListService;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.repository.GroupRepository;
import das.tools.np.services.LocalizeResourcesService;
import das.tools.np.services.PluginService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.controlsfx.control.action.ActionUtils.ACTION_SEPARATOR;

@Service
@Slf4j
public class MenuService {
    private final CommonMenuService commonMenu;
    private final FxWeaver fxWeaver;
    private final LocalizeResourcesService localizeService;
    private final GroupRepository groupRepository;
    private final ActionService actionService;
    private final PluginService pluginService;
    private final GlyphFont glyphFont;
    private final WindowListService windowService;
    private MainController mainController;
    private Menu pluginsMenu;

    public MenuService(CommonMenuService commonMenu, FxWeaver fxWeaver, LocalizeResourcesService localizeService, GroupRepository groupRepository, ActionService actionService, PluginService pluginService, GlyphFont glyphFont, WindowListService windowService) {
        this.commonMenu = commonMenu;
        this.fxWeaver = fxWeaver;
        this.localizeService = localizeService;
        this.groupRepository = groupRepository;
        this.actionService = actionService;
        this.pluginService = pluginService;
        this.glyphFont = glyphFont;
        this.windowService = windowService;
    }

    public void init() {
        mainController = fxWeaver.loadController(MainController.class);
    }

    public ContextMenu getContextMenu(CargoNumber number) {
        ContextMenu menu = new ContextMenu(commonMenu.getTitleCtxMenuItem(number.getNumber()));
        for (Action action : actionService.getNumberToolbarActions()) {
            menu.getItems().add(ActionUtils.createMenuItem(action));
        }
        menu.getItems().addAll(getMoveToGroupMenu(number),
                ActionUtils.createMenuItem(actionService.getNumberDetailAction()),
                getViewModeMenu(), getFilterModeMenu());
        return menu;
    }

    public ToolBar createToolBar() {
        ToolBar toolBar = ActionUtils.createToolBar(actionService.getNumberToolbarActions(), ActionUtils.ActionTextBehavior.HIDE);
        toolBar.getItems().add(getMoveToGroupMenuButton());
        toolBar.getItems().add(new Separator());
        toolBar.getItems().add(ActionUtils.createToolBar(actionService.getEditMenuActions(), ActionUtils.ActionTextBehavior.HIDE));
        toolBar.getItems().add(new Separator());
        toolBar.getItems().add(ActionUtils.createButton(actionService.getNumberDetailAction(), ActionUtils.ActionTextBehavior.HIDE));
        toolBar.getItems().add(ActionUtils.createToggleButton(actionService.getShowLogAction(), ActionUtils.ActionTextBehavior.HIDE));
        MenuButton viewMenuButton = getMenuButton(actionService.getViewModeGroup().getText(), glyphFont.create(FontAwesome.Glyph.EYE), getViewModeMenu());
        viewMenuButton.setOnShowing(e -> {
            viewMenuButton.getItems().clear();
            viewMenuButton.getItems().addAll(getViewModeMenu().getItems());
        });
        toolBar.getItems().add(viewMenuButton);
        MenuButton filterModeButton = getMenuButton(actionService.getFilterModeGroup().getText(), glyphFont.create(FontAwesome.Glyph.FILTER), getFilterModeMenu());
        filterModeButton.setOnShowing(e -> {
            filterModeButton.getItems().clear();
            filterModeButton.getItems().addAll(getFilterModeMenu().getItems());
        });
        toolBar.getItems().add(filterModeButton);
        return toolBar;
    }

    private MenuButton getMoveToGroupMenuButton() {
        Menu menu = new Menu();
        menu.getItems().addAll(getMoveToGroupMenu(mainController.getActiveSelectedNumber()).getItems());
        MenuButton button = getMenuButton(localizeService.getLocalizedResource("menu.group.moveToGroup"),
                glyphFont.create(FontAwesome.Glyph.SHARE_SQUARE_ALT), menu);
        button.setOnShowing(e -> {
            button.getItems().clear();
            button.getItems().addAll(getMoveToGroupMenu(mainController.getActiveSelectedNumber()).getItems());
        });
        return button;
    }

    private MenuButton getMenuButton(String text, Glyph glyph, Menu menu) {
        MenuButton button = new MenuButton("", glyph);
        button.setTooltip(new Tooltip(text));
        button.getItems().addAll(menu.getItems());
        return button;
    }

    public MenuBar createMenuBar() {
        MenuBar menuBar = ActionUtils.createMenuBar(actionService.getNumberMenuActionGroup());
        menuBar.getMenus().addAll(getEditMenu(), getViewMenu(), loadPluginsMenu(), loadWindowsMenu(), getHelpMenu());
        return menuBar;
    }

    private Menu getEditMenu() {
        Menu menu = new Menu(localizeService.getLocalizedResource("menu.group.edit"));
        for (Action action : actionService.getEditMenuActions()) {
            if (!action.equals(ACTION_SEPARATOR)) {
                menu.getItems().add(ActionUtils.createMenuItem(action));
            } else {
                menu.getItems().add(new SeparatorMenuItem());
            }
        }
        return menu;
    }

    private Menu getViewMenu() {
        Menu menu = new Menu(localizeService.getLocalizedResource("menu.group.view"));
        Menu viewModeMenu = getViewModeMenu();
        viewModeMenu.setOnShowing(e -> modeMenuShowing(viewModeMenu, getViewModeMenu().getItems()));
        Menu filterModeMenu = getFilterModeMenu();
        filterModeMenu.setOnShowing(e -> modeMenuShowing(filterModeMenu, getFilterModeMenu().getItems()));
        CheckMenuItem showDetailMenu = ActionUtils.createCheckMenuItem(actionService.getNumberDetailAction());
        CheckMenuItem showLogMenu = ActionUtils.createCheckMenuItem(actionService.getShowLogAction());
        menu.getItems().addAll(viewModeMenu, filterModeMenu, showDetailMenu, showLogMenu);
        return menu;
    }

    private void modeMenuShowing(Menu modeMenu, ObservableList<MenuItem> items) {
        modeMenu.getItems().clear();
        modeMenu.getItems().addAll(items);
    }

    private Menu getViewModeMenu() {
        Menu menu = new Menu(localizeService.getLocalizedResource("menu.group.viewMode"), glyphFont.create(FontAwesome.Glyph.EYE).color(Color.BLACK));
        menu.getItems().clear();
        menu.getItems().addAll(getSubMenuItems(actionService.getViewModeGroup().getActions()));
        return menu;
    }

    private Menu getFilterModeMenu() {
        Menu menu = new Menu(localizeService.getLocalizedResource("menu.group.filterMode"), glyphFont.create(FontAwesome.Glyph.FILTER).color(Color.BLACK));
        menu.getItems().clear();
        menu.getItems().addAll(getSubMenuItems(actionService.getFilterModeGroup().getActions()));
        return menu;
    }

    public ObservableList<MenuItem> getSubMenuItems(ObservableList<Action> actions) {
        List<MenuItem> items = new ArrayList<>();
        ToggleGroup group = new ToggleGroup();
        for (Action action : actions) {
            RadioMenuItem item = ActionUtils.createRadioMenuItem(action);
            item.setToggleGroup(group);
            item.setSelected(action.isSelected());
            items.add(item);
        }
        return FXCollections.observableList(items);
    }

    public Menu loadPluginsMenu() {
        pluginsMenu = new Menu();
        pluginsMenu.setText(localizeService.getLocalizedResource("menu.group.plugins"));
        pluginsMenu.getItems().add(ActionUtils.createMenuItem(actionService.getLoadPluginAction()));
        List<PluginInfo> plugins = pluginService.getAllPlugins();
        if (plugins != null && plugins.size() > 0) {
            addAllPluginsMenuItems(plugins);
        }
        return pluginsMenu;
    }

    public Menu getPluginsMenu() {
        if (pluginsMenu == null) {
            pluginsMenu = loadPluginsMenu();
        }
        return pluginsMenu;
    }

    private void addAllPluginsMenuItems(List<PluginInfo> plugins) {
        Map<String, List<MenuItem>> subItemsMap = getSubItemsMap(plugins);
        for (PluginInfo info : plugins) {
            String subdirectory = info.getPluginSubdirectory();
            if (!"".equals(subdirectory)) {
                Menu menu = new Menu(subdirectory);
                menu.getItems().addAll(subItemsMap.get(subdirectory));
                pluginsMenu.getItems().add(menu);
            }
        }
        // adding plugins menu items which in "Plugins" menu (w/o submenus) to the end of "Plugins" menu
        pluginsMenu.getItems().addAll(subItemsMap.get(""));
    }

    private Map<String,List<MenuItem>> getSubItemsMap(List<PluginInfo> plugins) {
        Map<String, List<PluginInfo>> subDirectoryWithPlugins = getSubDirectoryWithPlugins(plugins);
        Map<String,List<MenuItem>> map = new TreeMap<>();
        for (String subDir : getSortedSubDirs(plugins)) {
            List<MenuItem> items;
            if (map.containsKey(subDir)) {
                items = map.get(subDir);
            } else {
                items = new ArrayList<>();
            }
            for (PluginInfo info : subDirectoryWithPlugins.get(subDir)) {
                MenuItem mi = new MenuItem(info.getName());
                mi.setUserData(info);
                mi.setOnAction(e -> mainController.pluginMenuItemClicked(info));
                items.add(mi);
            }
            map.put(subDir, items);
        }
        return map;
    }

    private Map<String, List<PluginInfo>> getSubDirectoryWithPlugins(List<PluginInfo> plugins) {
        Map<String,List<PluginInfo>> subDirectoryWithPlugins = new HashMap<>(plugins.size());
        for (PluginInfo info : plugins) {
            List<PluginInfo> pi;
            String subdirectory = info.getPluginSubdirectory();
            if (subDirectoryWithPlugins.containsKey(subdirectory)) {
                pi = subDirectoryWithPlugins.get(subdirectory);
            } else {
                pi = new ArrayList<>();
            }
            pi.add(info);
            subDirectoryWithPlugins.put(subdirectory, pi);
        }
        return subDirectoryWithPlugins;
    }

    private String[] getSortedSubDirs(List<PluginInfo> plugins) {
        List<String> subDirs = new ArrayList<>(plugins.size());
        plugins.forEach(s -> subDirs.add(s.getPluginSubdirectory()));
        String[] array = subDirs.toArray(new String[0]);
        Arrays.sort(array); // sort sub dirs by its names asc.
        return array;
    }

    private Menu loadWindowsMenu() {
        Menu menu = new Menu(localizeService.getLocalizedResource("menu.group.windows"));
        for (Action action : actionService.getWindowsMenuActions()) {
            if (!action.equals(ACTION_SEPARATOR)) {
                menu.getItems().add(ActionUtils.createMenuItem(action));
            } else {
                menu.getItems().add(new SeparatorMenuItem());
            }
        }
        menu.setOnShowing(e -> {
            menu.getItems().remove(5, menu.getItems().size());
            menu.getItems().addAll(getWindowsItems());
        });
        menu.getItems().addAll(getWindowsItems());
        return menu;
    }

    private List<MenuItem> getWindowsItems() {
        List<Stage> windowsList = windowService.getWindowsList();
        List<MenuItem> items = new ArrayList<>(windowsList.size() + 1);
        if (windowsList.size() > 0) {
            items.add(new SeparatorMenuItem());
            for (Stage stage : windowsList) {
                MenuItem item = new MenuItem(stage.getTitle());
                item.setOnAction(e -> stage.requestFocus());
                items.add(item);
            }
        }
        return FXCollections.observableList(items);
    }

    private Menu getHelpMenu() {
        Menu menu = new Menu(localizeService.getLocalizedResource("menu.group.help"));
        menu.getItems().add(ActionUtils.createMenuItem(actionService.getAboutAction()));
        return menu;
    }

    public ContextMenu getLogContextMenu() {
        ContextMenu menu = new ContextMenu();
        for (Action action : actionService.getLogMenuActions()) {
            menu.getItems().add(ActionUtils.createMenuItem(action));
        }
        return menu;
    }

    private Menu getMoveToGroupMenu(CargoNumber number) {
        Menu menu = commonMenu.getMenu(localizeService.getLocalizedResource("menu.group.moveToGroup"),
                glyphFont.create(FontAwesome.Glyph.SHARE_SQUARE_ALT));
        if (number == null) {
            number = mainController.getActiveSelectedNumber();
        }
        if (number != null) {
            List<Group> groups = groupRepository.getAll();
            ToggleGroup toggleGroup = new ToggleGroup();
            for (Group g : groups) {
                RadioMenuItem item = commonMenu.getGroupMenuItem(g, number,
                        e -> mainController.moveToGroup(g.getId()));
                item.setToggleGroup(toggleGroup);
                menu.getItems().add(item);
            }
        }
        return menu;
    }
}
