package das.tools.np.gui.controllers.search;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.plugin.PluginInfo;
import das.tools.np.entity.search.SearchParams;
import das.tools.np.gui.CargoNumbersTableViewProducerService;
import das.tools.np.gui.FieldNameService;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.controllers.search.options.SearchOptionsControls;
import das.tools.np.gui.menu.MenuService;
import das.tools.np.services.impl.LocalizeResourcesService;
import das.tools.np.services.impl.PluginServiceImpl;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @Scope("prototype")
@Slf4j
public class SearchControlsProduceService {
    protected static final int[] DEFAULT_SELECTED_FIELDS_INDEXES = {1, 2};
    protected static final double SEARCH_FIELDS_COMBO_BOX_WIDTH = 250.0;
    public static final String ALL_RADIO_BUTTON_ID = "RadioButtonAll";
    public static final String SELECTED_RADIO_BUTTON_ID = "RadioButtonSelected";
    private final FieldNameService fieldNameService;
    private final LocalizeResourcesService localizeService;
    private final GlyphFont glyphFont;
    private final SearchOptionsControls optionsControls;
    private final CargoNumbersTableViewProducerService tableViewProducerService;
    private final MenuService menuService;
    private final PluginServiceImpl pluginService;
    private final FxWeaver fxWeaver;
    private ToggleButton btSearchOptions;
    private Node vbHiddenOptions;
    private HiddenSidesPane pnHiddenOptions;
    private CheckComboBox<String> cbFields;
    private Label lbName;
    private TableView<CargoNumber> tvResults;
    private HBox searchFieldsBox;

    public SearchControlsProduceService(FieldNameService fieldNameService, LocalizeResourcesService localizeService, GlyphFont glyphFont, SearchOptionsControls optionsControls, CargoNumbersTableViewProducerService tableViewProducerService, MenuService menuService, PluginServiceImpl pluginService, FxWeaver fxWeaver) {
        this.fieldNameService = fieldNameService;
        this.localizeService = localizeService;
        this.glyphFont = glyphFont;
        this.optionsControls = optionsControls;
        this.tableViewProducerService = tableViewProducerService;
        this.menuService = menuService;
        this.pluginService = pluginService;
        this.fxWeaver = fxWeaver;
    }

    public HiddenSidesPane getOptionsPane(BorderPane content, SearchParams params) {
        if (pnHiddenOptions == null) {
            HiddenSidesPane pane = new HiddenSidesPane();
            pane.setContent(content);
            AnchorPane.setRightAnchor(pane, 10.0);
            AnchorPane.setLeftAnchor(pane, 10.0);
            AnchorPane.setTopAnchor(pane, 10.0);
            AnchorPane.setBottomAnchor(pane, 10.0);
            pnHiddenOptions = pane;
        }
        updateHiddenOptionsPane(pnHiddenOptions, params);
        return pnHiddenOptions;
    }

    public HiddenSidesPane getPnHiddenOptions() {
        return pnHiddenOptions;
    }

    public void updateHiddenOptionsPane(HiddenSidesPane pane, SearchParams params) {
        pane.setLeft(makeHiddenBox(params));
    }

    public void disableOptionsPane() {
        pnHiddenOptions.setLeft(null);
    }

    public void enableOptionsPane() {
        pnHiddenOptions.setLeft(getHiddenBox());
    }

    public Node getHiddenBox() {
        return vbHiddenOptions;
    }

    private Node makeHiddenBox(SearchParams params) {
        ScrollPane sp = new ScrollPane();
        Node allOptionsBox = optionsControls.getOptionsBox(params);
        VBox vb = new VBox(5, getCaptionBox(), allOptionsBox);
        vb.getStyleClass().add("search-options-pane");
        sp.setContent(vb);
        sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        vbHiddenOptions = sp;
        return vbHiddenOptions;
    }

    public SearchParams getSearchParams() {
        SearchParams params = optionsControls.getSearchParams();
        ObservableList<String> checkedItems = getSearchFieldsCombobox().getCheckModel().getCheckedItems();
        List<String> fields = new ArrayList<>(checkedItems.size());
        checkedItems.forEach(s -> fields.add(fieldNameService.getFieldName(s)));
        params.setFields(fields);
        return params;
    }

    private Node getCaptionBox() {
        Label lbCaption = new Label(localizeService.getLocalizedResource("search.label.options"));
        lbCaption.getStyleClass().add("search-caption-label");
        lbName = new Label();
        lbName.getStyleClass().addAll("search-name-label");
        VBox vb = new VBox(5, lbCaption, lbName);
        vb.setPadding(new Insets(10,10,10,10));
        vb.setAlignment(Pos.CENTER);
        return vb;
    }

    public void setSearchName(String name) {
        lbName.setText(name);
    }

    public ToggleButton getSearchOptionsButton() {
        if (btSearchOptions == null) {
            btSearchOptions = new ToggleButton("", glyphFont.create(FontAwesome.Glyph.GEAR));
            pnHiddenOptions.setPinnedSide(null);
            btSearchOptions.setOnAction(e -> {
                if (btSearchOptions.isSelected()) {
                    pnHiddenOptions.setPinnedSide(Side.LEFT);
                } else {
                    pnHiddenOptions.setPinnedSide(null);
                }
            });
        }
        return btSearchOptions;
    }

    public Button getButton(String text, FontAwesome.Glyph glyph) {
        Button bt = new Button("", glyphFont.create(glyph));
        bt.setTooltip(new Tooltip(text));
        return bt;
    }

    public Node getSearchFieldsBox() {
        if (searchFieldsBox == null) {
            CheckComboBox<String> cb = getSearchFieldsCombobox();
            Button btSelectAll = new Button("", glyphFont.create(FontAwesome.Glyph.CHECK_SQUARE_ALT));
            btSelectAll.setTooltip(new Tooltip(localizeService.getLocalizedResource("search.fields.tooltip.selectAll")));
            btSelectAll.setOnAction(e -> {
                cb.getCheckModel().checkAll();
                cb.getCheckModel().check(0);
            });
            Button btSelectNone = new Button("", glyphFont.create(FontAwesome.Glyph.CLOSE));
            btSelectNone.setTooltip(new Tooltip(localizeService.getLocalizedResource("search.fields.tooltip.SelectNone")));
            btSelectNone.setOnAction(e -> cb.getCheckModel().clearChecks());
            searchFieldsBox = new HBox(5, cb, btSelectAll, btSelectNone);
            searchFieldsBox.setDisable(true);
        }
        return searchFieldsBox;
    }

    public CheckComboBox<String> getSearchFieldsCombobox() {
        if (cbFields == null) {
            CheckComboBox<String> cb = new CheckComboBox<>();
            cb.setPrefWidth(SEARCH_FIELDS_COMBO_BOX_WIDTH);
            cb.getItems().addAll(fieldNameService.getSearchableFieldFullNames());
            cb.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
                Tooltip.install(cb, new Tooltip(String.format(
                        localizeService.getLocalizedResource("search.fields.tooltip"),
                        String.join(", ", change.getList())))
                );
            });
            cb.getCheckModel().checkIndices(DEFAULT_SELECTED_FIELDS_INDEXES);
            cbFields = cb;
        }
        return cbFields;
    }

    public TableView<CargoNumber> getSearchResultsTable() {
        if (tvResults == null) {
            tvResults = tableViewProducerService.getNewTable();
        }
        return tvResults;
    }

    public void numberColumnAutosize() {
        TableColumn<CargoNumber, ?> numberColumn = tvResults.getColumns().get(0);
        if (numberColumn != null) {
            tableViewProducerService.doColumnAutoSize(tvResults, numberColumn);
        }
    }

    public MenuButton createPluginsMenuButton(TableView<CargoNumber> numbersTable, CheckBox sendAllItems) {
        MenuButton menuButton = new MenuButton("", glyphFont.create(FontAwesome.Glyph.PLUG));
        menuButton.setTooltip(new Tooltip(localizeService.getLocalizedResource("search.button.plugins.tooltip")));
        ObservableList<MenuItem> items = menuService.getPluginsMenu().getItems();
        Menu menu = new Menu();
        for (MenuItem item : items) {
            if (!(item instanceof SeparatorMenuItem)) {
                correctPluginMenuEventHandler(item, menu, numbersTable, sendAllItems);
            }
        }
        menuButton.getItems().addAll(menu.getItems());
        return menuButton;
    }

    public void tableViewItemOnClick(MouseEvent e, TableView<CargoNumber> tableView, boolean isArchive) {
        CargoNumber selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String number = selectedItem.getNumber();
            MainController mainController = fxWeaver.loadController(MainController.class);
            if (!isArchive) {
                if (e.getClickCount() == 1 && e.isAltDown()) {
                    mainController.setSelectedNumber(number);
                } else if (e.getClickCount() == 2) {
                    mainController.showInfoWindow(number);
                }
            } else {
                mainController.showArchivedNumbersWindow(number);
            }
        }
    }

    private void correctPluginMenuEventHandler(MenuItem source, Menu result, TableView<CargoNumber> numbersTable, CheckBox sendAllItems) {
        if (source instanceof Menu) {
            Menu menu = new Menu(source.getText());
            result.getItems().add(menu);
            for (MenuItem item : ((Menu) source).getItems()) {
                correctPluginMenuEventHandler(item, menu, numbersTable, sendAllItems);
            }
        } else if (source.getUserData() != null) {
            PluginInfo info = (PluginInfo) source.getUserData();
            MenuItem mi = new MenuItem(info.getName());
            mi.setOnAction(e -> {
                if (numbersTable.getItems().size() > 0) {
                    if (sendAllItems.isSelected()) {
                        pluginService.launchPlugin(numbersTable.getItems(), info.getAbsolutePath());
                    } else {
                        pluginService.launchPlugin(numbersTable.getSelectionModel().getSelectedItems(), info.getAbsolutePath());
                    }
                }
            });
            mi.setUserData(info);
            result.getItems().add(mi);
        }
    }

    public CheckBox getSendAllItemsCheckbox() {
        CheckBox chb = new CheckBox(localizeService.getLocalizedResource("search.button.sendAllItems"));
        chb.setTooltip(new Tooltip(localizeService.getLocalizedResource("search.button.sendAllItems.tooltip")));
        chb.setSelected(true);
        return chb;
    }
}
