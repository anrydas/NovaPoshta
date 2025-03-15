package das.tools.np.gui.controllers.edit;

import das.tools.np.entity.db.Group;
import das.tools.np.gui.Localized;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.dialog.ToastComponent;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.gui.menu.ActionService;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.repository.GroupRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Slf4j
public class EditGroupsController implements Localized {
    public static final int ROOT_WIDTH = 400;
    public static final int ROOT_HEIGHT = 300;
    private final FxWeaver fxWeaver;
    private final LocalizeResourcesService localizeService;
    private final GroupRepository groupRepository;
    private final CargoNumberRepository numberRepository;
    private final CommonService commonService;
    private final AlertService alertService;
    private final GlyphFont glyphFont;
    private final ConfigService configService;
    private final EditControlsProducer<Group> editControlsProducer;
    private final ToastComponent toast;
    private ListView<Group> lvGroups;
    private Stage stage;
    private Collection<? extends Action> toolBarActions;

    public EditGroupsController(FxWeaver fxWeaver, LocalizeResourcesService localizeService, GroupRepository groupRepository, CargoNumberRepository numberRepository, CommonService commonService, AlertService alertService, GlyphFont glyphFont, ConfigService configService, EditControlsProducer<Group> editControlsProducer, ToastComponent toast) {
        this.fxWeaver = fxWeaver;
        this.localizeService = localizeService;
        this.groupRepository = groupRepository;
        this.numberRepository = numberRepository;
        this.commonService = commonService;
        this.alertService = alertService;
        this.glyphFont = glyphFont;
        this.configService = configService;
        this.editControlsProducer = editControlsProducer;
        this.toast = toast;
    }

    public void initialize() {
        EditControlsHolder<Group> holder = editControlsProducer.getNewControls(ROOT_WIDTH, ROOT_HEIGHT);
        AnchorPane root = holder.getRoot();
        this.stage = new Stage();
        MainController controller = fxWeaver.loadController(MainController.class);
        stage.getIcons().add(controller.getWindowIcon());
        this.stage.setScene(new Scene(root));
        stage.setOnCloseRequest(e -> {
            configService.saveWindowPosition(stage, WindowType.EDIT_GROUPS);
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
        HBox hb = new HBox(5, lvGroups, editControlsProducer.getToolBar(toolBarActions));
        hb.setAlignment(Pos.TOP_LEFT);
        AnchorPane.setTopAnchor(hb, 5.0);
        AnchorPane.setRightAnchor(hb, 5.0);
        AnchorPane.setLeftAnchor(hb, 5.0);
        AnchorPane.setBottomAnchor(hb, 40.0);
        root.getChildren().addAll(editControlsProducer.getButtonBox(btOk, btCancel), hb);
    }

    private void setupListControl(EditControlsHolder<Group> holder) {
        lvGroups = holder.getListView();
        lvGroups.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item != null ? item.getName() : "");
                    setGraphic(null);
                    if (item != null) {
                        if (item.isChanged()) {
                            setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 16));
                        } else {
                            setFont(Font.font(16));
                        }
                    }
                }
            }
        });
    }

    private Collection<? extends Action> createActions() {
        ActionService.AppAction addNew = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.PLUS).color(Color.DARKBLUE),
                e -> addNew(lvGroups), null);
        ActionService.AppAction editSelected = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.EDIT).color(Color.DARKGREEN),
                e -> editSelected(lvGroups), null);
        ActionService.AppAction removeSelected = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.REMOVE).color(Color.DARKRED),
                e -> removeSelected(lvGroups), null);
        addNew.setStyle(EditAction.ADD.name());
        editSelected.setStyle(EditAction.EDIT.name());
        removeSelected.setStyle(EditAction.REMOVE.name());
        return FXCollections.observableArrayList(addNew, editSelected, ActionUtils.ACTION_SEPARATOR, removeSelected);
    }

    public void show() {
        initLocale();
        initControls();
        configService.populateWindowPosition(stage, WindowType.EDIT_GROUPS);
        stage.show();
        lvGroups.requestFocus();
    }

    private void initControls() {
        lvGroups.getItems().clear();
        lvGroups.getItems().addAll(groupRepository.getAll());
        if (lvGroups.getItems().size() > 0) {
            lvGroups.getSelectionModel().select(0);
        }
    }

    private void saveChanges() {
        Set<Long> ids = lvGroups.getItems().stream().
                map(Group::getId)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());
        for(Group group : groupRepository.getAll()) {
            if (!ids.contains(group.getId())) {
                groupRepository.delete(group);
            }
        }
        for(Group group : lvGroups.getItems()) {
            Group newGroup = Group.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .build();
            if (newGroup.getId() == 0) {
                groupRepository.add(newGroup);
            } else {
                Group oldGroup = groupRepository.findById(group.getId());
                if (!oldGroup.equals(newGroup)) {
                    groupRepository.update(newGroup);
                }
            }
        }
        stage.close();
    }

    private void addNew(ListView<Group> listView) {
        String name = alertService.getTextInputDialogResult(localizeService.getLocalizedResource("edit.groups.dialog.title"),
                "",
                localizeService.getLocalizedResource("edit.groups.dialog.title"),
                localizeService.getLocalizedResource("edit.groups.newGroup"),
                lvGroups.getScene().getWindow(),
                commonService.getImageView("/images/buttons/edit_16.png", 16));
        if (commonService.isEmpty(name)) {
            return;
        }
        if (groupRepository.isGroupExists(name)) {
            alertService.showError(String.format(localizeService.getLocalizedResource("edit.groups.alert.title"), name),
                    localizeService.getLocalizedResource("edit.groups.alert.message"));
            return;
        }
        Group item = Group.builder()
                .name(name)
                .build();
        Group selectedItem = listView.getSelectionModel().getSelectedItem();
        int index = listView.getItems().indexOf(selectedItem) + 1;
        listView.getItems().add(index, item);
        listView.getSelectionModel().select(index);
        if (log.isDebugEnabled()) log.debug("Added item={} to position={}", item, index);
    }

    private void editSelected(ListView<Group> listView) {
        Group item = listView.getSelectionModel().getSelectedItem();
        if (item != null) {
            String name = alertService.getTextInputDialogResult(localizeService.getLocalizedResource("edit.groups.dialog.title"),
                    item.getName(),
                    localizeService.getLocalizedResource("edit.groups.dialog.title"),
                    localizeService.getLocalizedResource("edit.groups.newGroup"),
                    lvGroups.getScene().getWindow(),
                    commonService.getImageView("/images/buttons/edit_16.png", 16));
            if (commonService.isEmpty(name)) {
                return;
            }
            if (!item.getName().equals(name)) {
                int index = listView.getItems().indexOf(item);
                item.setName(name);
                listView.getItems().set(index, item);
                if (log.isDebugEnabled()) log.debug("Changed group name={} to new one={}", item.getName(), name);
            }
        }
    }

    private void removeSelected(ListView<Group> listView) {
        Group item = listView.getSelectionModel().getSelectedItem();
        if (item != null) {
            boolean dialogResult = alertService.showConfirmDialog(localizeService.getLocalizedResource("edit.groups.confirm.title"),
                    String.format(localizeService.getLocalizedResource("edit.groups.confirm.header"), item.getName()),
                    localizeService.getLocalizedResource("edit.groups.confirm.message"));
            int numbersInGroup = numberRepository.numbersInGroup(item.getId());
            if (numbersInGroup > 0) {
                alertService.showError(String.format(localizeService.getLocalizedResource("edit.groups.alert.notEmpty.title"), item.getName()),
                        String.format(localizeService.getLocalizedResource("edit.groups.alert.notEmpty.message"), numbersInGroup));
                return;
            }
            if (dialogResult) {
                listView.getItems().remove(item);
                if (log.isDebugEnabled()) log.debug("Removed item={}", item);
                toast.makeToast((Stage) listView.getScene().getWindow(), localizeService.getLocalizedResource("edit.groups.toast.message.removed"));
            }
        }
    }

    @Override
    public void initLocale() {
        stage.setTitle(localizeService.getLocalizedResource("edit.groups.title"));
        editControlsProducer.updateActionsText(toolBarActions);
    }
}
