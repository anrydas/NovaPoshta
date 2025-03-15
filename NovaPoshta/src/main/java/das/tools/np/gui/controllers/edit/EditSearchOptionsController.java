package das.tools.np.gui.controllers.edit;

import das.tools.np.entity.db.SearchOptions;
import das.tools.np.entity.search.SearchParams;
import das.tools.np.gui.Localized;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.controllers.search.options.SearchOptionsControls;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.dialog.ToastComponent;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.gui.menu.ActionService;
import das.tools.np.repository.SearchOptionsRepository;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component @Scope("prototype")
@Slf4j
public class EditSearchOptionsController implements Localized {
    public static final int ROOT_WIDTH = 770;
    public static final int ROOT_HEIGHT = 500;
    public static final int CAPTION_LABEL_WIDTH = 90;
    public static final int CAPTION_TEXT_FIELD_WIDTH = 200;
    private final FxWeaver fxWeaver;
    private final LocalizeResourcesService localizeService;
    private final SearchOptionsRepository optionsRepository;
    private final AlertService alertService;
    private final GlyphFont glyphFont;
    private final EditControlsProducer<SearchOptions> editControlsProducer;
    private final SearchOptionsControls optionsControls;
    private final ToastComponent toast;
    private final CommonService commonService;
    private final ConfigService configService;
    private ListView<SearchOptions> lvOptions;
    private Stage stage;
    private Pane optionsPane;
    private TextField edSearchName;
    private TextField edSearchText;
    private Collection<? extends Action> toolBarActions;

    public EditSearchOptionsController(FxWeaver fxWeaver, LocalizeResourcesService localizeService, SearchOptionsRepository optionsRepository, AlertService alertService, GlyphFont glyphFont, EditControlsProducer<SearchOptions> editControlsProducer, SearchOptionsControls optionsControls, ToastComponent toast, CommonService commonService, ConfigService configService) {
        this.fxWeaver = fxWeaver;
        this.localizeService = localizeService;
        this.optionsRepository = optionsRepository;
        this.alertService = alertService;
        this.glyphFont = glyphFont;
        this.editControlsProducer = editControlsProducer;
        this.optionsControls = optionsControls;
        this.toast = toast;
        this.commonService = commonService;
        this.configService = configService;
    }

    public void initialize() {
        EditControlsHolder<SearchOptions> holder = editControlsProducer.getNewControls(ROOT_WIDTH, ROOT_HEIGHT);
        AnchorPane root = holder.getRoot();
        this.stage = new Stage();
        MainController controller = fxWeaver.loadController(MainController.class);
        stage.getIcons().add(controller.getWindowIcon());
        this.stage.setScene(new Scene(root));
        stage.setOnCloseRequest(e -> {
            configService.saveWindowPosition(stage, WindowType.EDIT_SEARCH_OPTIONS);
        });
        Button btOk = holder.getBtOk();
        btOk.setOnAction(e -> saveChanges());
        Button btCancel = holder.getBtCancel();
        btCancel.setOnAction(e -> {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            stage.close();
        });
        initListControl(holder);
        optionsPane = new Pane();
        optionsPane.setPrefWidth(ROOT_WIDTH * 0.5);
        toolBarActions = createActions();
        HBox hb = new HBox(5, lvOptions, editControlsProducer.getToolBar(toolBarActions), optionsPane);
        hb.setAlignment(Pos.TOP_LEFT);
        AnchorPane.setTopAnchor(hb, 5.0);
        AnchorPane.setRightAnchor(hb, 5.0);
        AnchorPane.setLeftAnchor(hb, 5.0);
        AnchorPane.setBottomAnchor(hb, 40.0);
        root.getChildren().addAll(editControlsProducer.getButtonBox(btOk, btCancel), hb);
    }

    private void initListControl(EditControlsHolder<SearchOptions> holder) {
        lvOptions = holder.getListView();
        lvOptions.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(SearchOptions item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item != null ? item.getName() : "");
                    setGraphic(null);
                    if (item != null) {
                        Font defaultFont = Font.getDefault();
                        if (item.isChanged()) {
                            setFont(Font.font(defaultFont.getName(), FontWeight.BOLD, defaultFont.getSize()));
                        } else {
                            setFont(Font.font(defaultFont.getName(), FontWeight.NORMAL, defaultFont.getSize()));
                        }
                    }
                }
            }
        });
        lvOptions.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                optionsPane.getChildren().clear();
                Node node = optionsControls.getOptionsBox(newValue.getParams());
                optionsPane.getChildren().add(getCaptionBox(newValue, node));
            }
        });
        lvOptions.setPrefWidth(ROOT_WIDTH * 0.33);
    }

    private Node getCaptionBox(SearchOptions options, Node node) {
        VBox vb = new VBox(getEditNameBox(options.getName()),
                getEditTextBox(options.getParams().getSearchText()),
                node);
        vb.setAlignment(Pos.TOP_CENTER);
        vb.setPrefWidth(ROOT_WIDTH * 0.59);
        return vb;
    }

    private HBox getEditNameBox(String value) {
        Label lb = getLabel(localizeService.getLocalizedResource("edit.options.label.name"));
        lb.setPrefWidth(150);
        edSearchName = getField(value);
        HBox hbName = new HBox(5, lb, edSearchName);
        hbName.setAlignment(Pos.TOP_CENTER);
        return hbName;
    }

    private HBox getEditTextBox(String value) {
        Label lb = getLabel(localizeService.getLocalizedResource("edit.options.label.text"));
        lb.setPrefWidth(150);
        edSearchText = getField(value);
        HBox hbName = new HBox(5, lb, edSearchText);
        hbName.setAlignment(Pos.TOP_CENTER);
        return hbName;
    }

    private Label getLabel(String text) {
        Label lb = new Label(text);
        lb.setPrefWidth(CAPTION_LABEL_WIDTH);
        lb.setAlignment(Pos.CENTER_RIGHT);
        return lb;
    }

    private TextField getField(String text) {
        TextField ed = new TextField(text);
        ed.setPrefWidth(CAPTION_TEXT_FIELD_WIDTH);
        return ed;
    }

    private Collection<? extends Action> createActions() {
        Action moveUp = editControlsProducer.getMoveUpAction(lvOptions);
        Action moveDown = editControlsProducer.getMoveDownAction(lvOptions);
        ActionService.AppAction addNew = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.PLUS).color(Color.DARKBLUE),
                e -> addNew(lvOptions), null);
        ActionService.AppAction applyChanges = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.CHECK).color(Color.DARKGREEN),
                e -> applyChanges(lvOptions), null);
        ActionService.AppAction removeSelected = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.REMOVE).color(Color.DARKRED),
                e -> removeSelected(lvOptions), null);
        addNew.setStyle(EditAction.ADD.name());
        applyChanges.setStyle(EditAction.APPLY.name());
        removeSelected.setStyle(EditAction.REMOVE.name());
        return FXCollections.observableArrayList(moveUp, moveDown, ActionUtils.ACTION_SEPARATOR, addNew, applyChanges, ActionUtils.ACTION_SEPARATOR, removeSelected);
    }

    public void show() {
        initLocale();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        initControls();
        configService.populateWindowPosition(stage, WindowType.EDIT_SEARCH_OPTIONS);
        stage.show();
        lvOptions.requestFocus();
    }

    private void initControls() {
        lvOptions.getItems().clear();
        lvOptions.getItems().addAll(optionsRepository.getAll());
        if (lvOptions.getItems().size() > 0) {
            lvOptions.getSelectionModel().select(0);
        }
    }

    @Override
    public void initLocale() {
        stage.setTitle(localizeService.getLocalizedResource("edit.options.title"));
        editControlsProducer.updateActionsText(toolBarActions);
    }

    private void saveChanges() {
        Set<Long> optionsIds = lvOptions.getItems().stream().
                map(SearchOptions::getId)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());
        for (SearchOptions options: optionsRepository.getAll()) {
            if (!optionsIds.contains(options.getId())) {
                optionsRepository.remove(options);
            }
        }
        for (SearchOptions options : lvOptions.getItems()) {
            SearchOptions newOptions = SearchOptions.builder()
                    .id(options.getId())
                    .name(options.getName())
                    .params(options.getParams())
                    .orderNumber(lvOptions.getItems().indexOf(options) + 1)
                    .build();
            if (newOptions.getId() == 0) {
                optionsRepository.add(newOptions);
            } else {
                SearchOptions oldOptions = optionsRepository.findById(options.getId());
                if (!oldOptions.equals(newOptions)) {
                    optionsRepository.update(newOptions);
                }
            }
        }
        stage.close();
    }

    private void addNew(ListView<SearchOptions> listView) {
        SearchOptions item = SearchOptions.builder()
                .name(localizeService.getLocalizedResource("edit.options.name.newOption"))
                .params(optionsControls.getEmptyParams())
                .changed(true)
                .build();
        SearchOptions selectedItem = listView.getSelectionModel().getSelectedItem();
        int index = listView.getItems().indexOf(selectedItem) + 1;
        listView.getItems().add(index, item);
        listView.getSelectionModel().select(index);
        if (log.isDebugEnabled()) log.debug("Added item={} to position={}", item, index);
    }

    private void applyChanges(ListView<SearchOptions> listView) {
        String searchNameText = commonService.isNotEmpty(edSearchName.getText()) ? edSearchName.getText() : "";
        if (commonService.isEmpty(searchNameText)) {
            alertService.showError(localizeService.getLocalizedResource("edit.options.alert.title"),
                    localizeService.getLocalizedResource("edit.options.alert.message"));
            return;
        }
        SearchOptions item = listView.getSelectionModel().getSelectedItem();
        if (item != null) {
            SearchParams params = optionsControls.getSearchParams();
            String searchText = edSearchText.getText();
            boolean isChanged = !item.getName().equals(searchNameText) || (
                    commonService.isEmpty(item.getParams().getSearchText()) && commonService.isEmpty(item.getParams().getSearchText()) ||
                    commonService.isNotEmpty(item.getParams().getSearchText()) && !item.getParams().getSearchText().equals(searchText)
            );
            if (isChanged) {
                item.setName(searchNameText);
                params.setSearchText(searchText);
                item.setParams(params);
                item.setChanged(true);
                toast.makeToast((Stage) listView.getScene().getWindow(), localizeService.getLocalizedResource("edit.options.toast.message.applied"));
                listView.refresh();
            } else {
                toast.makeToast((Stage) listView.getScene().getWindow(), localizeService.getLocalizedResource("edit.options.toast.message.noChanges"));
            }
        }
    }

    private void removeSelected(ListView<SearchOptions> listView) {
        SearchOptions item = listView.getSelectionModel().getSelectedItem();
        if (item != null) {
            boolean dialogResult = alertService.showConfirmDialog(localizeService.getLocalizedResource("edit.options.confirm.title"),
                    String.format(localizeService.getLocalizedResource("edit.options.confirm.header"), item.getName()),
                    localizeService.getLocalizedResource("edit.options.confirm.message"));
            if (dialogResult) {
                listView.getItems().remove(item);
                if (log.isDebugEnabled()) log.debug("Removed item={}", item);
                toast.makeToast((Stage) listView.getScene().getWindow(), String.format(localizeService.getLocalizedResource("edit.options.toast.message.removed"), item.getName()));
            }
        }
    }
}