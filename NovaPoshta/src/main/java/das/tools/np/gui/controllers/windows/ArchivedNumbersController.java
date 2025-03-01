package das.tools.np.gui.controllers.windows;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.gui.Localized;
import das.tools.np.gui.controllers.DetailedInfoController;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.gui.menu.ActionService;
import das.tools.np.repository.ArchiveRepository;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.services.ArchiveNumberService;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class ArchivedNumbersController implements Localized {
    private final CommonService commonService;
    private Stage stage;
    private final FxWeaver fxWeaver;
    private final LocalizeResourcesService localizeService;
    private final WindowControlsProducer windowControlsProducer;
    private final AlertService alertService;
    private final ArchiveRepository archiveRepository;
    private final ArchiveNumberService archiveService;
    private final ActionService actionService;
    private final ConfigService configService;
    private final CargoNumberRepository numberRepository;
    private Label lbTitle;
    private TableView<CargoNumber> dataTable;

    public ArchivedNumbersController(LocalizeResourcesService localizeService, WindowControlsProducer windowControlsProducer, AlertService alertService, ArchiveRepository archiveRepository, FxWeaver fxWeaver, ActionService actionService, CommonService commonService, ArchiveNumberService archiveService, ConfigService configService, CargoNumberRepository numberRepository) {
        this.localizeService = localizeService;
        this.windowControlsProducer = windowControlsProducer;
        this.alertService = alertService;
        this.archiveRepository = archiveRepository;
        this.fxWeaver = fxWeaver;
        this.actionService = actionService;
        this.commonService = commonService;
        this.archiveService = archiveService;
        this.configService = configService;
        this.numberRepository = numberRepository;
    }

    public void init() {
        if (this.stage == null) {
            WindowControlsHolder holder = windowControlsProducer.getNewControls();
            stage = holder.getStage();
            stage.setOnCloseRequest(e -> {
                configService.saveWindowPosition(stage, WindowType.ARCHIVE);
            });
            initControls(holder);
            initLocale();
            dataTable.getItems().addAll(archiveRepository.findAll());
        }
    }

    public void show(String number) {
        if (stage.isShowing()) {
            stage.requestFocus();
        } else {
            configService.populateWindowPosition(stage, WindowType.ARCHIVE);
            stage.show();
        }
        if (commonService.isNotEmpty(number)) {
            for (CargoNumber n : dataTable.getItems()) {
                if (n.getNumber().equals(number)) {
                    dataTable.getSelectionModel().select(n);
                    break;
                }
            }
        }
    }

    public void moveNumberToArchive(String number) {
        archiveService.moveNumberToArchive(number);
        CargoNumber cargoNumber = archiveRepository.findByNumber(number);
        if (cargoNumber != null) {
            putNumberIntoTable(cargoNumber);
        } else {
            log.error("Couldn't find number '{}' in archive after moving it to archive", number);
        }
    }

    public void restoreNumber(String number) {
        archiveService.restoreNumber(number);
        CargoNumber cargoNumber = numberRepository.findByNumber(number);
        if (cargoNumber != null) {
            removeNumberFromTable(cargoNumber);
        } else {
            log.error("Couldn't find number '{}' after restoring", number);
        }
    }

    public void putNumberIntoTable(CargoNumber number) {
        dataTable.getItems().add(number);
    }

    public void removeNumberFromList(CargoNumber number) {
        archiveRepository.delete(number);
        dataTable.getItems().remove(number);
    }

    public void removeNumberFromTable(CargoNumber number) {
        dataTable.getItems().removeIf(n -> n.getNumber().equals(number.getNumber()));
    }

    public String getSelectedNumber() {
        CargoNumber number = dataTable.getSelectionModel().getSelectedItem();
        if (number != null) {
            return number.getNumber();
        }
        return "";
    }

    private void initControls(WindowControlsHolder holder) {
        lbTitle = holder.getTitleLabel();
        dataTable = holder.getDataTable();
        ContextMenu contextMenu = getContextMenu(dataTable,
                e -> removeItemMenuHandler(), e -> clearItemsMenuHandler());
        dataTable.setContextMenu(contextMenu);
        dataTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                showInfoWindow();
            }
        });
    }

    private void showInfoWindow() {
        DetailedInfoController controller = fxWeaver.loadController(DetailedInfoController.class);
        CargoNumber number = dataTable.getSelectionModel().getSelectedItem();
        if (number != null) {
            controller.show(archiveRepository.findByNumber(number.getNumber()));
        }
    }

    @Override
    public void initLocale() {
        stage.setTitle(localizeService.getLocalizedResource("anl.title"));
        lbTitle.setText(localizeService.getLocalizedResource("anl.label"));
    }

    private void removeItemMenuHandler() {
        ObservableList<CargoNumber> selectedItems = dataTable.getSelectionModel().getSelectedItems();
        boolean result = isDeletionConfirmed(selectedItems.size());
        if (result) {
            for (CargoNumber number : selectedItems) {
                archiveRepository.delete(number);
                dataTable.getItems().remove(number);
                fxWeaver.loadController(MainController.class).logError(
                        String.format(localizeService.getLocalizedResource("anl.log.numberRemoved"), number.getNumber())
                );
            }
        }
    }

    private void clearItemsMenuHandler() {
        ObservableList<CargoNumber> items = dataTable.getItems();
        if (isDeletionConfirmed(items.size())) {
            for (CargoNumber number : items) {
                archiveRepository.delete(number);
            }
            items.clear();
            fxWeaver.loadController(MainController.class).logError(
                    localizeService.getLocalizedResource("anl.log.numbersCleared")
            );
            stage.hide();
        }
    }

    private boolean isDeletionConfirmed(int amount) {
        return alertService.showConfirmDialog(
                localizeService.getLocalizedResource("lw.alert.title"),
                String.format(localizeService.getLocalizedResource("anl.alert.message"), amount),
                localizeService.getLocalizedResource("anl.alert.content"));
    }

    private ContextMenu getContextMenu(TableView<CargoNumber> dataTable, Consumer<ActionEvent> removeConsumer, Consumer<ActionEvent> clearConsumer) {
        List<Action> actions = windowControlsProducer.getActions(removeConsumer, clearConsumer);
        Action restoreAction = actionService.getRestoreAction();
        MenuItem miRestore = ActionUtils.createMenuItem(restoreAction);
        MenuItem miRemove = ActionUtils.createMenuItem(actions.get(0));
        MenuItem miClear = ActionUtils.createMenuItem(actions.get(1));
        ContextMenu menu = new ContextMenu(miRestore, new SeparatorMenuItem(), miRemove, miClear);
        menu.setOnShowing(e -> {
            for (Action action : actions) {
                action.setDisabled(dataTable.getItems().size() == 0 || dataTable.getSelectionModel().getSelectedItems().size() == 0);
            }
        });
        return menu;
    }
}
