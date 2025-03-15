package das.tools.np.gui.controllers;

import das.tools.np.entity.WindowPosition;
import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.Group;
import das.tools.np.entity.db.SimpleNumber;
import das.tools.np.entity.plugin.PluginInfo;
import das.tools.np.gui.*;
import das.tools.np.gui.controllers.edit.EditGroupsController;
import das.tools.np.gui.controllers.edit.EditPhonesController;
import das.tools.np.gui.controllers.edit.EditSearchOptionsController;
import das.tools.np.gui.controllers.search.SearchController;
import das.tools.np.gui.controllers.windows.ArchivedNumbersController;
import das.tools.np.gui.controllers.windows.CustomNumbersListController;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.enums.FilteringMode;
import das.tools.np.gui.enums.ViewMode;
import das.tools.np.gui.menu.ActionService;
import das.tools.np.gui.menu.MenuService;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.services.*;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;


@Component
@FxmlView("/fxml/Main.fxml")
@Slf4j
public class MainControllerImpl implements MainController, Localized {
    @FXML private VBox vbMenu;
    @FXML private BorderPane pnTop;
    @FXML private AnchorPane pnLog;
    @FXML private AnchorPane pnNumbers;
    @FXML private ComboBox<ViewMode> cbViewMode;
    @FXML private ComboBox<FilteringMode> cbViewFilter;
    @FXML private TreeView<String> tvGroups;
    @FXML private TreeView<String> tvCalendar;
    @FXML private AnchorPane pnRoot;
    @FXML private HBox hbNumber;
    @FXML private Label lbNumber;
    @FXML private ListView<SimpleNumber> lvNumbers;
    @FXML private SplitPane spLog;
    @FXML private SplitPane spNumberList;
    @FXML private TextFlow txLog;
    @FXML private HBox hbViewMode;
    @FXML private HBox hbViewFilter;
    private Labeled openButton;
    private Labeled copyButton;
    private final ConversionService conversionService;
    private final CargoNumberRepository numberRepository;
    private final NumberListService numberListService;
    private final NumberTreeViewService numberTreeService;
    private final NumberInfoViewService numberInfoViewService;
    private final FxWeaver fxWeaver;
    private final CargoNumberService numberService;
    private final MenuService menuService;
    private final ViewModeService viewModeService;
    private final FilteringModeService filterModeService;
    private final LocalizeResourcesService localizeService;
    private final GroupService groupService;
    private final AlertService alertService;
    private final CommonService commonService;
    private final SwitchNumberViewService numberViewService;
    private final ConfigService configService;
    private final FieldNameService fieldNameService;
    private final CargoStatusService cargoStatusService;
    private final CargoTypeService cargoTypeService;
    private final ActionService actionService;
    private final GlyphFont glyphFont;
    private final PluginService pluginService;
    private final CustomNumbersListController customNumbersController;
    private final ArchivedNumbersController archivedNumbersController;
    private final ApplicationLogService logService;
    private final BuildProperties buildProperties;

    private Image windowIcon;
    private ContextMenu logContextMenu;

    public MainControllerImpl(ConversionService conversionService, CargoNumberRepository numberRepository, NumberListService numberListService, NumberTreeViewService numberTreeService, NumberInfoViewService numberInfoViewService, FxWeaver fxWeaver, CargoNumberService numberService, MenuService menuService, ViewModeService viewModeService, FilteringModeService filterModeService, LocalizeResourcesService localizeService, GroupService groupService, AlertService alertService, CommonService commonService, SwitchNumberViewService numberViewService, ConfigService configService, FieldNameService fieldNameService, CargoStatusService cargoStatusService, CargoTypeService cargoTypeService, ActionService actionService, GlyphFont glyphFont, PluginService pluginService, CustomNumbersListController customNumbersController, ArchivedNumbersController archivedNumbersController, ApplicationLogService logService, BuildProperties buildProperties) {
        this.conversionService = conversionService;
        this.numberRepository = numberRepository;
        this.numberListService = numberListService;
        this.numberTreeService = numberTreeService;
        this.numberInfoViewService = numberInfoViewService;
        this.fxWeaver = fxWeaver;
        this.numberService = numberService;
        this.menuService = menuService;
        this.viewModeService = viewModeService;
        this.filterModeService = filterModeService;
        this.localizeService = localizeService;
        this.groupService = groupService;
        this.alertService = alertService;
        this.commonService = commonService;
        this.numberViewService = numberViewService;
        this.configService = configService;
        this.fieldNameService = fieldNameService;
        this.cargoStatusService = cargoStatusService;
        this.cargoTypeService = cargoTypeService;
        this.actionService = actionService;
        this.glyphFont = glyphFont;
        this.pluginService = pluginService;
        this.customNumbersController = customNumbersController;
        this.archivedNumbersController = archivedNumbersController;
        this.logService = logService;
        this.buildProperties = buildProperties;
    }

    @FXML
    public void initialize() {
        this.windowIcon = new Image("/images/np_app_icon.png");
        menuService.init();
        initControls();
        if (configService.isFirstLaunch()) {
            alertService.showInfo(localizeService.getLocalizedResource("alert.firstLaunch.title"),
                    localizeService.getLocalizedResource("alert.firstLaunch.header"),
                    localizeService.getLocalizedResource("alert.firstLaunch.message"));
            showConfigWindow();
        }
    }

    private void initControls() {
        initMenus();
        customNumbersController.init();
        archivedNumbersController.init();
        logService.initLogControl(txLog);
        tvGroups.setShowRoot(false);
        tvCalendar.setShowRoot(false);
        numberViewService.addControls(lvNumbers, tvGroups, tvCalendar);
        openButton = numberInfoViewService.getOpenButton();
        openButton.setOnMouseClicked(e -> showInfoWindow(getActiveSelectedNumberStr()));
        copyButton = numberInfoViewService.getCopyButton(lbNumber);
        hbNumber.getChildren().clear();
        hbNumber.getChildren().addAll(openButton, lbNumber, copyButton);
        hbNumber.setVisible(false);
        lvNumbers.setCellFactory(p -> numberListService.getNumberListView());
        lvNumbers.getSelectionModel().selectedItemProperty().addListener(observable -> listNumberSelected());
        tvGroups.setCellFactory(p -> numberTreeService.getTreeView());
        tvGroups.getSelectionModel().selectedItemProperty().addListener(observable -> treeNumberSelected(tvGroups.getSelectionModel().getSelectedItem()));
        tvCalendar.setCellFactory(p -> numberTreeService.getTreeView());
        tvCalendar.getSelectionModel().selectedItemProperty().addListener(observable -> treeNumberSelected(tvCalendar.getSelectionModel().getSelectedItem()));
        cbViewMode.setConverter(new StringConverter<>() {
            @Override
            public String toString(ViewMode mode) {
                return viewModeService.getModeName(mode);
            }
            @Override
            public ViewMode fromString(String s) {
                return viewModeService.getModeValue(s);
            }
        });
        cbViewMode.valueProperty().addListener((observableValue, viewMode, newViewMode) -> setViewMode(newViewMode));
        cbViewMode.getSelectionModel().select(0);
        cbViewFilter.setConverter(new StringConverter<>() {
            @Override
            public String toString(FilteringMode mode) {
                return filterModeService.getModeName(mode);
            }
            @Override
            public FilteringMode fromString(String s) {
                return filterModeService.getModeValue(s);
            }
        });
        cbViewFilter.valueProperty().addListener((observableValue, filteringMode, newFilteringMode) -> setViewMode(cbViewMode.getValue()));
        cbViewFilter.getSelectionModel().select(0);
        hbViewMode.getChildren().add(0, glyphFont.create(FontAwesome.Glyph.EYE));
        hbViewFilter.getChildren().add(0, glyphFont.create(FontAwesome.Glyph.FILTER));
        SplitPane.setResizableWithParent(pnNumbers, false);
        SplitPane.setResizableWithParent(pnLog, false);
        txLog.setOnMouseClicked(this::showLogPopup);
        spNumberList.setDividerPosition(0, configService.getNumbersDividerPosition());
        lvNumbers.getItems().addListener((Observable o) -> listNumberSelected());
    }

    private void initMenus() {
        actionService.initActions();
        vbMenu.getChildren().clear();
        vbMenu.getChildren().add(0, menuService.createMenuBar());
        vbMenu.getChildren().add(menuService.createToolBar());
    }

    @Override
    public Image getWindowIcon() {
        return windowIcon;
    }

    private void setViewMode(ViewMode viewMode) {
        viewModeService.setCurrentMode(viewMode);
        filterModeService.setCurrentMode(cbViewFilter.getValue());
        String selectedNumber = getActiveSelectedNumberStr();
        if (ViewMode.VM_NUMBERS == viewMode) {
            lvNumbers.getItems().clear();
            new RunInThread(() -> loadItemsToList(selectedNumber)).run();
            numberViewService.makeControlVisible(lvNumbers);
        } else if (ViewMode.VM_GROUPS == viewMode) {
            tvGroups.setRoot(null);
            new RunInThread(() -> loadItemsToGroupsTree(selectedNumber)).run();
            numberViewService.makeControlVisible(tvGroups);
        } else if (ViewMode.VM_CALENDAR_CREATE == viewMode) {
            tvCalendar.setRoot(null);
            new RunInThread(() -> loadItemsToCreateCalendarTree(selectedNumber)).run();
            numberViewService.makeControlVisible(tvCalendar);
        } else if (ViewMode.VM_CALENDAR_UPDATE == viewMode) {
            tvCalendar.setRoot(null);
            new RunInThread(() -> loadItemsToUpdateCalendarTree(selectedNumber)).run();
            numberViewService.makeControlVisible(tvCalendar);
        }
    }

    @Override
    public void setSelectedNumber(String number) {
        ViewMode currentMode = viewModeService.getCurrentMode();
        switch (currentMode) {
            case VM_NUMBERS -> setSelectedItem(lvNumbers, number);
            case VM_GROUPS -> setSelectedItem(tvGroups, number);
            case VM_CALENDAR_CREATE, VM_CALENDAR_UPDATE -> setSelectedItem(tvCalendar, number);
        }
    }

    private void setSelectedItem(ListView<SimpleNumber> control, String number) {
        new RunInThread(() -> {
            for (SimpleNumber n : control.getItems()) {
                if (n.getNumber().equals(number)) {
                    control.getSelectionModel().select(n);
                    control.scrollTo(n);
                    control.getScene().getWindow().requestFocus();
                    return;
                }
            }
        }).run();
    }

    private void setSelectedItem(TreeView<String> control, String number) {
        new RunInThread(() -> {
            TreeItem < String > root = control.getRoot();
            selectTreeItem(control, root, number);
            control.getScene().getWindow().requestFocus();
        }).run();
    }

    private void selectTreeItem(TreeView<String> control, TreeItem<String> root, String number) {
        for (TreeItem<String> child: root.getChildren()){
            if (child.getChildren().isEmpty()) {
                if (child.getValue().equals(number)) {
                    control.getSelectionModel().select(child);
                    child.getParent().setExpanded(true);
                    control.scrollTo(control.getSelectionModel().getSelectedIndex());
                    return;
                }
            } else {
                selectTreeItem(control, child, number);
            }
        }
    }

    @Override
    public String getActiveSelectedNumberStr() {
        return lbNumber.getText();
    }

    @Override
    public CargoNumber getActiveSelectedNumber() {
        return numberRepository.findByNumber(lbNumber.getText());
    }

    @Override
    public void updateViewMode(ViewMode viewMode) {
        cbViewMode.setValue(viewMode);
    }

    @Override
    public void updateFilterMode(FilteringMode mode) {
        cbViewFilter.setValue(mode);
    }

    @Override
    public void showConfigWindow() {
        ConfigController controller = fxWeaver.loadController(ConfigController.class);
        controller.show();
        if (controller.isConfigChanged()) {
            initLocale();
            actionService.initActions();
            pluginService.loadPlugins();
            initMenus();
            setViewMode(cbViewMode.getValue());
        }
    }

    @Override
    public void loadPlugins() {
        pluginService.loadPlugins();
    }

    @Override
    public void showInfoWindow(String number) {
        DetailedInfoController controller = fxWeaver.loadController(DetailedInfoController.class);
        if (commonService.isNotEmpty(number)) {
            controller.show(numberRepository.findByNumber(number));
        }
    }

    @Override
    public void showCustomNumbersWindow() {
        customNumbersController.show();
    }

    @Override
    public void showArchivedNumbersWindow(String number) {
        archivedNumbersController.show(number);
    }

    @Override
    public void updateNumberLists(SimpleNumber number) {
        switch (viewModeService.getCurrentMode()) {
            case VM_NUMBERS -> loadItemsToList(number.getNumber());
            case VM_GROUPS -> loadItemsToGroupsTree(number.getNumber());
            case VM_CALENDAR_UPDATE -> loadItemsToUpdateCalendarTree(number.getNumber());
            case VM_CALENDAR_CREATE -> loadItemsToCreateCalendarTree(number.getNumber());
        }
    }

    @Override
    public void addNumber() {
        AddNumberController controller = fxWeaver.loadController(AddNumberController.class);
        controller.setUserData(NEED_TO_BE_CLOSED_ON_EXIT);
        controller.show();
    }

    @Override
    public void applicationExit() {
        onClosingStage(new WindowEvent(lvNumbers.getScene().getWindow(), WindowEvent.WINDOW_HIDDEN));
    }

    @Override
    public void moveToArchive() {
        ArchivedNumbersController controller = fxWeaver.loadController(ArchivedNumbersController.class);
        String number = getActiveSelectedNumberStr();
        controller.moveNumberToArchive(number);
        removeNumber();
        CustomNumbersListController customNumbersController = fxWeaver.loadController(CustomNumbersListController.class);
        customNumbersController.removeNumberFromList(number);
    }

    private void removeNumber() {
        switch (viewModeService.getCurrentMode()) {
            case VM_NUMBERS -> {
                lvNumbers.getItems().remove(lvNumbers.getSelectionModel().getSelectedItem());
            }
            case VM_GROUPS -> removeTreeItem(tvGroups);
            case VM_CALENDAR_UPDATE, VM_CALENDAR_CREATE -> removeTreeItem(tvCalendar);
        }
    }

    private void removeTreeItem(TreeView<String> treeView) {
        TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
        item.getParent().getChildren().remove(item);
    }

    @Override
    public void restoreFromArchive() {
        ArchivedNumbersController controller = fxWeaver.loadController(ArchivedNumbersController.class);
        String number = controller.getSelectedNumber();
        if (commonService.isNotEmpty(number)) {
            controller.restoreNumber(number);
            setViewMode(viewModeService.getCurrentMode());
        }
    }

    @Override
    public void addToCustomList() {
        customNumbersController.putNumberIntoTable(getActiveSelectedNumber());
    }

    @Override
    public void pluginMenuItemClicked(PluginInfo pluginInfo) {
        pluginService.launchPlugin(numberRepository.findAll(), pluginInfo.getAbsolutePath());
    }

    @Override
    public void about() {
        alertService.showInfo("About", APPLICATION_TITLE + " v." + buildProperties.getVersion(), "Created by -=:dAs:=-");
    }

    @Override
    public void updateNumber() {
        if (viewModeService.getCurrentMode() == ViewMode.VM_NUMBERS) {
            new RunInThread(() -> {
                numberService.updateNumberData(lvNumbers.getSelectionModel().getSelectedItem(), lvNumbers.getItems(), txLog);
                listNumberSelected();
            }).run();
        } else if (viewModeService.getCurrentMode() == ViewMode.VM_GROUPS) {
            SimpleNumber number = conversionService.convert(getActiveSelectedNumber(), SimpleNumber.class);
            if (number != null) {
                new RunInThread(() -> {
                    numberService.updateNumberData(number, tvGroups.getSelectionModel().getSelectedItem(), txLog);
                    treeNumberSelected(tvGroups.getSelectionModel().getSelectedItem());
                }).run();
            }
        } else if (viewModeService.getCurrentMode() == ViewMode.VM_CALENDAR_UPDATE || viewModeService.getCurrentMode() == ViewMode.VM_CALENDAR_CREATE) {
            SimpleNumber number = conversionService.convert(getActiveSelectedNumber(), SimpleNumber.class);
            if (number != null) {
                new RunInThread(() -> {
                    numberService.updateNumberData(number, tvCalendar.getSelectionModel().getSelectedItem(), txLog);
                    treeNumberSelected(tvCalendar.getSelectionModel().getSelectedItem());
                }).run();
            }
            if (number != null) {
                if (viewModeService.getCurrentMode() == ViewMode.VM_CALENDAR_CREATE) {
                    loadItemsToCreateCalendarTree(number.getNumber());
                } else {
                    loadItemsToUpdateCalendarTree(number.getNumber());
                }
            }
        }
    }

    @Override
    public void updateUncompleted() {
        numberService.updateUncompleted(lvNumbers.getItems(), txLog, true);
    }

    @Override
    public void addNewGroup() {
        AddGroupController controller = fxWeaver.loadController(AddGroupController.class);
        controller.show();
    }

    @Override
    public void editGroups() {
        EditGroupsController controller = fxWeaver.loadController(EditGroupsController.class);
        controller.initialize();
        controller.show();
    }

    @Override
    public void editPhones() {
        EditPhonesController controller = fxWeaver.loadController(EditPhonesController.class);
        controller.initialize();
        controller.show();
    }

    @Override
    public void editSearchOptions() {
        EditSearchOptionsController controller = fxWeaver.loadController(EditSearchOptionsController.class);
        controller.initialize();
        controller.show();
    }

    @Override
    public void search() {
        SearchController controller = fxWeaver.loadController(SearchController.class);
        controller.show();
    }

    @Override
    public void moveToGroup(long newGroupId) {
        numberService.moveNumberToGroup(lvNumbers.getSelectionModel().getSelectedItem(), lvNumbers.getItems(), newGroupId);
    }

    @Override
    public void onShowingStage() {
        initLocale();
        viewModeService.setAllModes(cbViewMode.getItems());
        filterModeService.setAllModes(cbViewFilter.getItems());
        cbViewMode.getSelectionModel().select(ViewMode.valueOf(configService.getConfigValue(ConfigService.CONFIG_VIEW_MODE_KEY)));
        cbViewFilter.getSelectionModel().select(FilteringMode.valueOf(configService.getConfigValue(ConfigService.CONFIG_FILTER_MODE_KEY)));
        setViewMode(cbViewMode.getValue());
    }

    @Override
    public void onClosingStage(WindowEvent e) {
        new RunInThread(this::storeConfiguration).run();
        ObservableList<Window> windows = Window.getWindows();
        for (Window w : windows) {
            if (MainController.NEED_TO_BE_CLOSED_ON_EXIT.equals(w.getUserData())) {
                alertService.showError(localizeService.getLocalizedResource("alert.CouldNotExit.title"),
                        localizeService.getLocalizedResource("alert.CouldNotExit.message"));
                w.requestFocus();
                e.consume();
                return;
            }
        }
        Platform.exit();
        System.exit(0);
    }

    private void storeConfiguration() {
        Properties props = configService.getCurrentConfig();
        props.setProperty(ConfigService.CONFIG_VIEW_MODE_KEY, String.valueOf(cbViewMode.getValue()));
        props.setProperty(ConfigService.CONFIG_FILTER_MODE_KEY, String.valueOf(cbViewFilter.getValue()));
        props.setProperty(ConfigService.CONFIG_IS_FIRST_LAUNCH, "false");
        props.setProperty(ConfigService.CONFIG_CUSTOM_NUMBERS, customNumbersController.getSeparatedNumbers());
        WindowPosition windowPosition = configService.getWindowPosition((Stage) lvNumbers.getScene().getWindow());
        props.setProperty(ConfigService.MAIN_WINDOW_POSITION, conversionService.convert(windowPosition, String.class));
        configService.saveConfig(props);
    }

    private void loadItemsToList(String selected) {
        List<CargoNumber> numbers = numberService.getAllFiltered(cbViewFilter.getValue(), null);
        TypeDescriptor sourceType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(CargoNumber.class));
        TypeDescriptor destType = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(SimpleNumber.class));
        List<SimpleNumber> simpleNumbers = (List<SimpleNumber>) conversionService.convert(numbers, sourceType, destType);
        if (lvNumbers.getItems().size() > 0) {
            lvNumbers.getItems().clear();
        }
        if (simpleNumbers != null) {
            lvNumbers.getItems().addAll(simpleNumbers);
            if (selected != null && !"".equals(selected)) {
                for (SimpleNumber n : lvNumbers.getItems()) {
                    if (n.getNumber().equals(selected)) {
                        lvNumbers.getSelectionModel().select(n);
                        break;
                    }
                }
            } else if (simpleNumbers.size() > 0){
                lvNumbers.getSelectionModel().select(0);
            }
        }
    }

    private void loadItemsToGroupsTree(String selected) {
        TreeItem<String> root = new TreeItem<>(NumberTreeViewService.ROOT_ITEM_NAME);
        tvGroups.setRoot(root);
        for (Group g : groupService.getAll()) {
            TreeItem<String> groupItem = new TreeItem<>(g.getName());
            root.getChildren().add(groupItem);
            for (CargoNumber number : numberService.getAllFiltered(cbViewFilter.getValue(), g)) {
                TreeItem<String> item = new TreeItem<>(number.getNumber());
                groupItem.getChildren().add(item);
                if (item.getValue().equals(selected)) {
                    tvGroups.getSelectionModel().select(item);
                }
            }
        }
    }

    private void loadItemsToUpdateCalendarTree(String selected) {
        TreeItem<String> root = new TreeItem<>(NumberTreeViewService.ROOT_ITEM_NAME);
        tvCalendar.setRoot(root);
        List<CargoNumber> allNumbers = numberService.getAllFilteredSortedByUpdateDate(cbViewFilter.getValue());
        for (String date : numberService.getNumbersUpdateDates(allNumbers)) {
            TreeItem<String> dateItem = new TreeItem<>(date);
            root.getChildren().add(dateItem);
            for (CargoNumber number : allNumbers) {
                if (date.equals(CargoNumberService.TREE_DATE_FORMAT.format(number.getUpdated()))) {
                    TreeItem<String> item = new TreeItem<>(number.getNumber());
                    dateItem.getChildren().add(item);
                    if (item.getValue().equals(selected)) {
                        tvCalendar.getSelectionModel().select(item);
                    }
                }
            }
        }
    }

    private void loadItemsToCreateCalendarTree(String selected) {
        TreeItem<String> root = new TreeItem<>(NumberTreeViewService.ROOT_ITEM_NAME);
        tvCalendar.setRoot(root);
        List<CargoNumber> allNumbers = numberService.getAllFilteredSortedByCreateDate(cbViewFilter.getValue());
        for (String date : numberService.getNumbersCreateDates(allNumbers)) {
            TreeItem<String> dateItem = new TreeItem<>(date);
            root.getChildren().add(dateItem);
            for (CargoNumber number : allNumbers) {
                if (date.equals(CargoNumberService.TREE_DATE_FORMAT.format(number.getCreated()))) {
                    TreeItem<String> item = new TreeItem<>(number.getNumber());
                    dateItem.getChildren().add(item);
                    if (item.getValue().equals(selected)) {
                        tvCalendar.getSelectionModel().select(item);
                    }
                }
            }
        }
    }

    private void listNumberSelected() {
        SimpleNumber simpleNumber = lvNumbers.getSelectionModel().getSelectedItem();
        if (simpleNumber != null) {
            CargoNumber number = numberRepository.findById(simpleNumber.getId());
            updateNumberInfoControls(number);
            lvNumbers.setContextMenu(menuService.getContextMenu(number));
        }
    }

    private void treeNumberSelected(TreeItem<String> item) {
        if (item != null) {
            String value = item.getValue();
            CargoNumber number = numberRepository.findByNumber(value);
            if (number != null) {
                updateNumberInfoControls(number);
                numberViewService.getVisibleControl().setContextMenu(menuService.getContextMenu(number));
                Event.fireEvent(item, new TreeItem.TreeModificationEvent<>(TreeItem.valueChangedEvent(), item, value));
            }
        }
    }

    private void updateNumberInfoControls(CargoNumber number) {
        if (number != null) {
            hbNumber.setVisible(true);
            lbNumber.setText(number.getNumber());
            numberInfoViewService.showInfo(pnRoot, number, false);
        } else {
            hbNumber.setVisible(false);
            numberInfoViewService.clearFields(pnRoot);
        }
    }

    private void showLogPopup(MouseEvent e) {
        if (logContextMenu == null){
            logContextMenu = menuService.getLogContextMenu();
        }
        if (logContextMenu.isShowing()) {
            logContextMenu.hide();
        }
        if (e.getButton() == MouseButton.SECONDARY) {
            logContextMenu.show(txLog, e.getScreenX(), e.getScreenY());
        } else if (e.getButton() == MouseButton.PRIMARY) {
            logContextMenu.hide();
        }
    }

    @Override
    public void scheduledNumbersUpdate(boolean showDialog) {
        if (configService.isAutoUpdateEnabled()) {
            numberService.updateUncompleted(lvNumbers.getItems(), txLog, showDialog);
        }
    }

    @Override
    public void toggleLogPanel() {
        if (isLogVisible()) {
            spLog.setDividerPosition(0, LOG_HIDDEN_POSITION);
        } else {
            spLog.setDividerPosition(0, LOG_SHOWN_POSITION);
        }
    }

    @Override
    public boolean isLogVisible() {
        return spLog.getDividerPositions()[0] <= LOG_MAX_VISIBLE_POSITION;
    }

    @Override
    public void initLocale() {
        new RunInThread(() -> {
            viewModeService.initLocale();
            filterModeService.initLocale();
            fieldNameService.initLocale();
            cargoStatusService.initLocale();
            cargoTypeService.initLocale();
            numberInfoViewService.installTooltip(hbViewMode, "label.vm.tooltip");
            numberInfoViewService.installTooltip(hbViewFilter, "label.fm.filter.tooltip");
            numberInfoViewService.installTooltip(openButton, "info.button.tooltip.open");
            numberInfoViewService.installTooltip(copyButton, "info.button.tooltip.copy");
            numberInfoViewService.invalidateComboBox(cbViewMode);
            numberInfoViewService.invalidateComboBox(cbViewFilter);
        }).run();
    }

    @Override
    public void logInfo(String message) {
        logService.populateInfoMessage(message);
    }

    @Override
    public void logWarn(String message) {
        logService.populateWarnMessage(message);
    }

    @Override
    public void logError(String message) {
        logService.populateErrorMessage(message);
    }

    @Override
    public void copyLog() {
        logService.copyLogToClip();

    }

    @Override
    public void clearLog() {
        logService.clearLog();
    }
}
