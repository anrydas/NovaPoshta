package das.tools.np.gui.controllers.edit;

import das.tools.np.entity.db.ExtraPhone;
import das.tools.np.gui.Localized;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.gui.menu.ActionService;
import das.tools.np.repository.ExtraPhonesRepository;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

@Component @Scope("prototype")
@Slf4j
public class EditPhonesController implements Localized {
    public static final int ROOT_WIDTH = 400;
    public static final int ROOT_HEIGHT = 300;
    private final FxWeaver fxWeaver;
    private final LocalizeResourcesService localizeService;
    private final ExtraPhonesRepository phonesRepository;
    private final CommonService commonService;
    private final AlertService alertService;
    private final GlyphFont glyphFont;
    private final EditControlsProducer<ExtraPhone> editControlsProducer;
    private final ConfigService configService;
    private ListView<ExtraPhone> lvPhones;
    private Stage stage;
    private Collection<? extends Action> toolBarActions;

    public EditPhonesController(FxWeaver fxWeaver, LocalizeResourcesService localizeService, ExtraPhonesRepository phonesRepository, CommonService commonService, AlertService alertService, GlyphFont glyphFont, EditControlsProducer<ExtraPhone> editControlsProducer, ConfigService configService) {
        this.fxWeaver = fxWeaver;
        this.localizeService = localizeService;
        this.phonesRepository = phonesRepository;
        this.commonService = commonService;
        this.alertService = alertService;
        this.glyphFont = glyphFont;
        this.editControlsProducer = editControlsProducer;
        this.configService = configService;
    }

    public void initialize() {
        EditControlsHolder<ExtraPhone> holder = editControlsProducer.getNewControls(ROOT_WIDTH, ROOT_HEIGHT);
        AnchorPane root = holder.getRoot();
        this.stage = new Stage();
        MainController controller = fxWeaver.loadController(MainController.class);
        stage.getIcons().add(controller.getWindowIcon());
        this.stage.setScene(new Scene(root));
        stage.setOnCloseRequest(e -> {
            configService.saveWindowPosition(stage, WindowType.EDIT_PHONES);
        });
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        Button btOk = holder.getBtOk();
        btOk.setOnAction(e -> saveChanges());
        Button btCancel = holder.getBtCancel();
        btCancel.setOnAction(e -> {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            stage.close();
        });
        setupListControl(holder);
        toolBarActions = createActions();
        HBox hb = new HBox(5, lvPhones, editControlsProducer.getToolBar(toolBarActions));
        hb.setAlignment(Pos.TOP_LEFT);
        AnchorPane.setTopAnchor(hb, 5.0);
        AnchorPane.setRightAnchor(hb, 5.0);
        AnchorPane.setLeftAnchor(hb, 5.0);
        AnchorPane.setBottomAnchor(hb, 40.0);
        root.getChildren().addAll(editControlsProducer.getButtonBox(btOk, btCancel), hb);
    }

    private void setupListControl(EditControlsHolder<ExtraPhone> holder) {
        lvPhones = holder.getListView();
        lvPhones.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ExtraPhone item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item != null ? item.getPhone() : "");
                    setGraphic(null);
                    if (item != null && item.getOrderNumber() == 0) {
                        setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 16));
                    } else {
                        setFont(Font.font(16));
                    }
                }
            }
        });
    }

    private Collection<? extends Action> createActions() {
        Action moveUp = editControlsProducer.getMoveUpAction(lvPhones);
        Action moveDown = editControlsProducer.getMoveDownAction(lvPhones);
        ActionService.AppAction addNew = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.PLUS).color(Color.DARKBLUE),
                e -> addNew(lvPhones), null);
        ActionService.AppAction editSelected = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.EDIT).color(Color.DARKGREEN),
                e -> editSelected(lvPhones), null);
        ActionService.AppAction removeSelected = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.REMOVE).color(Color.DARKRED),
                e -> removeSelected(lvPhones), null);
        addNew.setStyle(EditAction.ADD.name());
        editSelected.setStyle(EditAction.EDIT.name());
        removeSelected.setStyle(EditAction.REMOVE.name());
        return FXCollections.observableArrayList(moveUp, moveDown, ActionUtils.ACTION_SEPARATOR, addNew, editSelected, ActionUtils.ACTION_SEPARATOR, removeSelected);
    }

    public void show() {
        initLocale();
        initControls();
        configService.populateWindowPosition(stage, WindowType.EDIT_PHONES);
        stage.show();
        lvPhones.requestFocus();
    }

    private void initControls() {
        lvPhones.getItems().clear();
        lvPhones.getItems().addAll(phonesRepository.getAll());
        if (lvPhones.getItems().size() > 0) {
            lvPhones.getSelectionModel().select(0);
        }
    }

    private void saveChanges() {
        for (ExtraPhone phone: phonesRepository.getAll()) {
            if (!lvPhones.getItems().contains(phone)) {
                phonesRepository.remove(phone);
            }
        }
        for (ExtraPhone phone : lvPhones.getItems()) {
            if (phone.getId() == 0 && phone.getOrderNumber() == -1) {
                phonesRepository.add(phone);
            } else {
                ExtraPhone oldPhone = phonesRepository.findById(phone.getId());
                ExtraPhone newPhone = ExtraPhone.builder()
                        .id(phone.getId())
                        .phone(phone.getPhone())
                        .orderNumber(lvPhones.getItems().indexOf(phone))
                        .build();
                if (!oldPhone.equals(newPhone)) {
                    phonesRepository.update(newPhone);
                }
            }
        }
        ExtraPhone defaultPhone = lvPhones.getItems().get(0);
        if (defaultPhone != null) {
            ExtraPhone storedDefaultPhone = phonesRepository.getDefault();
            if (!defaultPhone.getPhone().equals(storedDefaultPhone.getPhone())) {
                configService.saveDefaultPhone(defaultPhone.getPhone());
            }
        }
        stage.close();
    }

    private void addNew(ListView<ExtraPhone> listView) {
        String editedPhone = alertService.getTextInputDialogResult(localizeService.getLocalizedResource("edit.phones.dialog.title"),
                "",
                localizeService.getLocalizedResource("edit.phones.dialog.title"),
                localizeService.getLocalizedResource("edit.phones.newPhone"),
                lvPhones.getScene().getWindow(),
                commonService.getImageView("/images/buttons/edit_16.png", 16));
        if (commonService.isEmpty(editedPhone)) {
            return;
        }
        if (!phonesRepository.isPhoneValid(editedPhone)) {
            alertService.showError(String.format(localizeService.getLocalizedResource("edit.phones.alert.title"), editedPhone),
                    localizeService.getLocalizedResource("edit.phones.alert.message"));
            return;
        }
        ExtraPhone item = ExtraPhone.builder()
                .phone(editedPhone)
                .build();
        ExtraPhone selectedItem = listView.getSelectionModel().getSelectedItem();
        int index = listView.getItems().indexOf(selectedItem) + 1;
        listView.getItems().add(index, item);
        if (log.isDebugEnabled()) log.debug("Added editedPhone={} to position={}", item, index);
    }

    private void editSelected(ListView<ExtraPhone> listView) {
        ExtraPhone item = listView.getSelectionModel().getSelectedItem();
        if (item != null) {
            String editedPhone = alertService.getTextInputDialogResult(localizeService.getLocalizedResource("edit.phones.dialog.title"),
                    item.getPhone(),
                    localizeService.getLocalizedResource("edit.phones.dialog.title"),
                    localizeService.getLocalizedResource("edit.phones.newPhone"),
                    lvPhones.getScene().getWindow(),
                    commonService.getImageView("/images/buttons/edit_16.png", 16));
            if (commonService.isEmpty(editedPhone)) {
                return;
            }
            if (!phonesRepository.isPhoneValid(editedPhone)) {
                alertService.showError(String.format(localizeService.getLocalizedResource("edit.phones.alert.title"), editedPhone),
                        localizeService.getLocalizedResource("edit.phones.alert.message"));
                return;
            }
            if (!item.getPhone().equals(editedPhone)) {
                int index = listView.getItems().indexOf(item);
                item.setPhone(editedPhone);
                listView.getItems().set(index, item);
                if (log.isDebugEnabled()) log.debug("Changed phone={} to new one={}", item, editedPhone);
            }
        }
    }

    private void removeSelected(ListView<ExtraPhone> listView) {
        ExtraPhone item = listView.getSelectionModel().getSelectedItem();
        if (item != null) {
            boolean dialogResult = alertService.showConfirmDialog(localizeService.getLocalizedResource("edit.phones.confirm.title"),
                    String.format(localizeService.getLocalizedResource("edit.phones.confirm.header"), item.getPhone()),
                    localizeService.getLocalizedResource("edit.phones.confirm.message"));
            if (phonesRepository.isUsedInNumber(item)) {
                alertService.showError(String.format(localizeService.getLocalizedResource("edit.phones.alert.phoneUsed.title"), item.getPhone()),
                        localizeService.getLocalizedResource("edit.phones.alert.phoneUsed.message"));
                return;
            }
            if (dialogResult) {
                listView.getItems().remove(item);
            }
        }
    }

    @Override
    public void initLocale() {
        stage.setTitle(localizeService.getLocalizedResource("edit.phones.title"));
        editControlsProducer.updateActionsText(toolBarActions);
    }
}
