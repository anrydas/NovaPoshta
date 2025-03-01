package das.tools.np.gui.controllers.search;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.SearchOptions;
import das.tools.np.entity.search.SearchParams;
import das.tools.np.gui.FieldNameService;
import das.tools.np.gui.Localized;
import das.tools.np.gui.WindowListService;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.repository.SearchHistoryRepository;
import das.tools.np.repository.SearchOptionsRepository;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import das.tools.np.services.SearchService;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component @Scope("prototype")
@FxmlView("/fxml/Search.fxml")
@Slf4j
public class SearchController implements Localized {
    private final GlyphFont glyphFont;
    @FXML private HBox hbResultsHeader;
    @FXML private AnchorPane root;
    @FXML private BorderPane bpMain;
    @FXML private HBox hbSearchFor;
    @FXML private HBox hbSearchOptions;
    @FXML private Label lbSearchOptions;
    @FXML private ToggleGroup tgSearchOptions;
    @FXML private RadioButton rbSimpleSearch;
    @FXML private RadioButton rbExtSearch;
    @FXML private Label lbSearchResults;
    @FXML private Label lbSearchFor;
    @FXML private TextField edSearchText;
    @FXML private Button btSearch;
    @FXML private VBox vbParams;
    private Stage stage;
    private final FxWeaver fxWeaver;
    private final LocalizeResourcesService localizeService;
    private final SearchControlsProduceService controlsProducer;
    private final FieldNameService fieldNameService;
    private final SearchOptionsRepository optionsRepository;
    private final AlertService alertService;
    private final CommonService commonService;
    private final SearchService searchService;
    private final SearchHistoryRepository searchHistory;
    private final WindowListService windowService;
    private final ConfigService configService;
    private TableView<CargoNumber> tvSearchResults;
    private HBox hbOptionsButtonsBox;
    private Button btSaveOptions;
    private Button btRestoreOptions;
    private MainController mainController;
    private MenuButton mbPlugins;
    private CheckBox chbSearchArchive;
    private CheckBox chbSendAll;

    public SearchController(FxWeaver fxWeaver, LocalizeResourcesService localizeService, SearchControlsProduceService controlsProducer, GlyphFont glyphFont, FieldNameService fieldNameService, SearchOptionsRepository optionsRepository, AlertService alertService, CommonService commonService, SearchService searchService, SearchHistoryRepository searchHistory, WindowListService windowService, ConfigService configService) {
        this.fxWeaver = fxWeaver;
        this.localizeService = localizeService;
        this.controlsProducer = controlsProducer;
        this.glyphFont = glyphFont;
        this.fieldNameService = fieldNameService;
        this.optionsRepository = optionsRepository;
        this.alertService = alertService;
        this.commonService = commonService;
        this.searchService = searchService;
        this.searchHistory = searchHistory;
        this.windowService = windowService;
        this.configService = configService;
    }

    @FXML
    public void initialize() {
        mainController = fxWeaver.loadController(MainController.class);
        this.stage = new Stage();
        this.stage.getIcons().add(mainController.getWindowIcon());
        stage.setOnCloseRequest(e -> {
            windowService.remove(stage);
            configService.saveWindowPosition(stage, WindowType.SEARCH);
        });
        Scene scene = new Scene(root);
        this.stage.setScene(scene);
        scene.getStylesheets().add(commonService.loadResource("/css/search-controls.css"));
        initControls();
    }

    public void show() {
        initLocale();
        windowService.put(stage);
        configService.populateWindowPosition(stage, WindowType.SEARCH);
        stage.show();
        edSearchText.requestFocus();
    }

    private void initControls() {
        root.getChildren().remove(bpMain);
        HiddenSidesPane pnSearchOptions =  controlsProducer.getOptionsPane(bpMain, null);
        stage.getScene().setOnKeyPressed(getKeyPressedHandler(pnSearchOptions));
        root.getChildren().add(0, pnSearchOptions);
        btSearch.setGraphic(glyphFont.create(FontAwesome.Glyph.SEARCH));
        btSearch.setOnAction(e -> proceedSearch());
        btSearch.setDisable(true);
        edSearchText.textProperty().addListener(e -> setSearchButtonDisabled());
        rbSimpleSearch.setOnAction(e -> setSearchButtonDisabled());
        rbExtSearch.setOnAction(e -> setSearchButtonDisabled());
        bindAutoCompletion();
        hbOptionsButtonsBox = new HBox(5,
                controlsProducer.getSearchOptionsButton(),
                getSaveButton(),
                getRestoreButton());
        hbOptionsButtonsBox.setAlignment(Pos.CENTER_LEFT);
        hbOptionsButtonsBox.setDisable(true);
        hbSearchFor.getChildren().addAll(hbOptionsButtonsBox);
        hbSearchOptions.getChildren().add(controlsProducer.getSearchFieldsBox());
        chbSearchArchive = new CheckBox();
        chbSearchArchive.setOnAction(e -> {
            if (!chbSearchArchive.isSelected() && tvSearchResults.getItems().size() > 0) {
                tvSearchResults.getItems().clear();
                mainController.logWarn(localizeService.getLocalizedResource("search.log.searchMethodChanged"));
            }
        });
        hbSearchOptions.getChildren().add(chbSearchArchive);
        tvSearchResults = controlsProducer.getSearchResultsTable();
        tvSearchResults.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        bpMain.setCenter(tvSearchResults);
        tvSearchResults.setOnMouseClicked(e -> controlsProducer.tableViewItemOnClick(e, tvSearchResults, chbSearchArchive.isSelected()));
        controlsProducer.disableOptionsPane();
        tgSearchOptions.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            controlsProducer.getSearchFieldsBox().setDisable(rbSimpleSearch.isSelected());
            hbOptionsButtonsBox.setDisable(rbSimpleSearch.isSelected());
            if (rbSimpleSearch.isSelected()) {
                controlsProducer.disableOptionsPane();
            } else {
                controlsProducer.enableOptionsPane();
            }
        });
        chbSendAll = controlsProducer.getSendAllItemsCheckbox();
        chbSendAll.setDisable(true);
        mbPlugins = controlsProducer.createPluginsMenuButton(tvSearchResults, chbSendAll);
        hbResultsHeader.getChildren().addAll(mbPlugins, chbSendAll);
        mbPlugins.setDisable(true);
    }

    private void setSearchButtonDisabled() {
        btSearch.setDisable(commonService.isEmpty(edSearchText.getText()) && !rbExtSearch.isSelected());
    }

    private EventHandler<KeyEvent> getKeyPressedHandler(HiddenSidesPane pnSearchOptions) {
        return e -> {
            if (e.getCode() == KeyCode.ESCAPE && pnSearchOptions.getPinnedSide() != null) {
                pnSearchOptions.setPinnedSide(null);
                controlsProducer.getSearchOptionsButton().setSelected(false);
            }
            if (e.getCode() == KeyCode.P && e.isControlDown()) {
                controlsProducer.getSearchOptionsButton().fire();
            }
            if (e.getCode() == KeyCode.ENTER) {
                btSearch.fire();
            }
            if (e.isControlDown()) {
                if (e.getCode() == KeyCode.S && !getSaveButton().isDisable()) {
                    getSaveButton().fire();
                } else if (e.getCode() == KeyCode.O && !getRestoreButton().isDisable()) {
                    getRestoreButton().fire();
                } else if (e.getCode() == KeyCode.M) {
                    rbSimpleSearch.fire();
                } else if (e.getCode() == KeyCode.E) {
                    rbExtSearch.fire();
                }
            }
        };
    }

    private Button getSaveButton() {
        if (btSaveOptions == null) {
            btSaveOptions = controlsProducer.getButton(localizeService.getLocalizedResource("search.button.save"), FontAwesome.Glyph.SAVE);
            btSaveOptions.setOnAction(e -> {
                SearchParams params = getSearchParams();
                String name = alertService.getTextInputDialogResult(localizeService.getLocalizedResource("search.alert.input.title"),
                        params.getSearchText(),
                        localizeService.getLocalizedResource("search.alert.input.header"),
                        localizeService.getLocalizedResource("search.alert.input.content"),
                        this.stage, null);
                if (commonService.isNotEmpty(name)) {
                    if (optionsRepository.isNameAlreadyExists(name)) {
                        alertService.showError(localizeService.getLocalizedResource("search.alert.error.header"),
                                String.format(localizeService.getLocalizedResource("search.alert.error.message.alreadyExists"), name));
                    } else {
                        SearchOptions options = SearchOptions.builder()
                                .name(name)
                                .params(params)
                                .build();
                        optionsRepository.add(options);
                        controlsProducer.setSearchName(name);
                    }
                }
            });
        }
        return btSaveOptions;
    }

    private Button getRestoreButton() {
        if (btRestoreOptions == null) {
            btRestoreOptions = controlsProducer.getButton(localizeService.getLocalizedResource("search.button.restore"), FontAwesome.Glyph.FOLDER_OPEN);
            btRestoreOptions.setOnAction(e -> {
                List<SearchOptions> options = optionsRepository.getAll();
                List<String> names = new ArrayList<>(options.size());
                options.forEach(s -> names.add(s.getName()));
                String name = alertService.getTextSelectComboDialogResult(localizeService.getLocalizedResource("search.alert.select.title"),
                        localizeService.getLocalizedResource("search.alert.select.header"),
                        localizeService.getLocalizedResource("search.alert.select.content"),
                        names,
                        this.stage);
                if (commonService.isNotEmpty(name)) {
                    SearchParams params = optionsRepository.findByName(name).getParams();
                    controlsProducer.updateHiddenOptionsPane(controlsProducer.getPnHiddenOptions(), params);
                    edSearchText.setText(params.getSearchText());
                    controlsProducer.getSearchFieldsCombobox().getCheckModel().clearChecks();
                    List<String> checkedFields = params.getFields();
                    int[] idx = new int[checkedFields.size()];
                    int i = 0;
                    for (String item : checkedFields) {
                        idx[i++] = controlsProducer.getSearchFieldsCombobox().getItems().indexOf(fieldNameService.getFieldFullName(item));
                    }
                    controlsProducer.getSearchFieldsCombobox().getCheckModel().checkIndices(idx);
                    controlsProducer.setSearchName(name);
                }
            });
        }
        return btRestoreOptions;
    }

    private void proceedSearch() {
        if ((commonService.isNotEmpty(edSearchText.getText()) && rbSimpleSearch.isSelected()) || rbExtSearch.isSelected()) {
            tvSearchResults.getItems().clear();
            SearchParams params = null;
            if (rbExtSearch.isSelected()) {
                params = getSearchParams();
            }
            List<CargoNumber> results;
            if (chbSearchArchive.isSelected()) {
                results = (rbSimpleSearch.isSelected()) ? searchService.searchArchive(edSearchText.getText()) : searchService.searchArchive(params);
            } else {
                results = (rbSimpleSearch.isSelected()) ? searchService.search(edSearchText.getText()) : searchService.search(params);
            }
            if (results.size() > 0) {
                searchHistory.add(edSearchText.getText());
                bindAutoCompletion();
                for (CargoNumber n : results) {
                    tvSearchResults.getItems().add(n);
                }
                controlsProducer.numberColumnAutosize();
                mbPlugins.setDisable(false);
                chbSendAll.setDisable(false);
                mainController.logInfo(String.format(localizeService.getLocalizedResource("search.log.numbersFound"), edSearchText.getText(), results.size()));
            } else {
                mainController.logWarn(String.format(localizeService.getLocalizedResource("search.log.numbersNotFound"),  edSearchText.getText()));
                alertService.showInfo(localizeService.getLocalizedResource("search.alert.info.results.header"),
                        localizeService.getLocalizedResource("search.alert.info.results.content"));
            }
        }
    }

    private void bindAutoCompletion() {
        TextFields.bindAutoCompletion(edSearchText, searchHistory.getAllAsStrings());
    }

    private SearchParams getSearchParams() {
        SearchParams params = controlsProducer.getSearchParams();
        params.setSearchText(edSearchText.getText());
        return params;
    }

    @Override
    public void initLocale() {
        stage.setTitle(localizeService.getLocalizedResource("search.window.title"));
        lbSearchFor.setText(localizeService.getLocalizedResource("search.label.what"));
        edSearchText.setPromptText(localizeService.getLocalizedResource("search.edit.what.prompt"));
        lbSearchOptions.setText(localizeService.getLocalizedResource("search.label.searchOptions"));
        lbSearchResults.setText(localizeService.getLocalizedResource("search.label.results"));
        btSearch.setTooltip(new Tooltip(localizeService.getLocalizedResource("search.button")));
        tvSearchResults.setTooltip(new Tooltip("Use:\nDouble Click - to show number detailed info\nCtrl + Click to select those number in main window"));
        rbSimpleSearch.setText(localizeService.getLocalizedResource("search.button.simpleSearch"));
        rbExtSearch.setText(localizeService.getLocalizedResource("search.button.extSearch"));
        chbSearchArchive.setText(localizeService.getLocalizedResource("search.button.searchArchive"));
    }
}
