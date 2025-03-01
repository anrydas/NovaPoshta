package das.tools.np.gui.controllers.windows;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.gui.Localized;
import das.tools.np.gui.controllers.search.SearchControlsProduceService;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class CustomNumbersListController implements Localized {
    private Stage stage;
    private final AlertService alertService;
    private final LocalizeResourcesService localizeService;
    private final WindowControlsProducer windowControlsProducer;
    private final SearchControlsProduceService controlsProducer;
    private final CargoNumberRepository numberRepository;
    private final ConfigService configService;
    private final CommonService commonService;
    private Label lbTitle;
    private TableView<CargoNumber> dataTable;

    public CustomNumbersListController(LocalizeResourcesService localizeService, WindowControlsProducer windowControlsProducer, AlertService alertService, SearchControlsProduceService controlsProducer, CargoNumberRepository numberRepository, ConfigService configService, CommonService commonService) {
        this.localizeService = localizeService;
        this.windowControlsProducer = windowControlsProducer;
        this.alertService = alertService;
        this.controlsProducer = controlsProducer;
        this.numberRepository = numberRepository;
        this.configService = configService;
        this.commonService = commonService;
    }

    public void init() {
        WindowControlsHolder holder = windowControlsProducer.getNewControls();
        if (this.stage == null) {
            stage = holder.getStage();
            stage.setOnCloseRequest(e -> {
                configService.saveWindowPosition(stage, WindowType.CUSTOM_VIEW);
            });
        }
        initControls(holder);
    }

    public void show() {
        initLocale();
        if (stage.isShowing()) {
            stage.requestFocus();
        } else {
            configService.populateWindowPosition(stage, WindowType.CUSTOM_VIEW);
            stage.show();
        }
    }

    public void putNumberIntoTable(CargoNumber number) {
        if (!isNumberInList(number.getNumber(), dataTable.getItems())) {
            dataTable.getItems().add(number);
        }
    }

    private boolean isNumberInList(String number, List<CargoNumber> list) {
        for (CargoNumber n : list) {
            if (n.getNumber().equals(number)) {
                return true;
            }
        }
        return false;
    }

    public void removeNumberFromList(String number) {
        dataTable.getItems().removeIf(n -> n.getNumber().equals(number));
    }

    private void initControls(WindowControlsHolder holder) {
        lbTitle = holder.getTitleLabel();
        dataTable = holder.getDataTable();
        dataTable.setContextMenu(
                getContextMenu(dataTable,
                        e -> removeItemMenuHandler(), e -> clearItemsMenuHandler())
        );
        dataTable.setOnMouseClicked(e -> controlsProducer.tableViewItemOnClick(e, dataTable, false));
        loadStoredNumbers();
    }

    private void loadStoredNumbers() {
        String numbers = configService.getCustomNumbers();
        if (commonService.isNotEmpty(numbers)) {
            for (String s : numbers.split(",")) {
                if (numberRepository.isNumberExists(s)) {
                    CargoNumber number = numberRepository.findByNumber(s);
                    if (number != null) {
                        dataTable.getItems().add(number);
                    }
                } else {
                    log.warn(String.format("The '%s' number from Custom view doesn't found in DB. May be it was deleted or moved to Archive.", s));
                }
            }
        }
    }


    @Override
    public void initLocale() {
        stage.setTitle(localizeService.getLocalizedResource("cnl.title"));
        lbTitle.setText(localizeService.getLocalizedResource("cnl.label"));
    }

    public String getSeparatedNumbers() {
        StringBuilder sb = new StringBuilder();
        dataTable.getItems().forEach(s -> sb.append(s.getNumber()).append(","));
        return sb.toString();
    }

    private void removeItemMenuHandler() {
        ObservableList<CargoNumber> selectedItems = dataTable.getSelectionModel().getSelectedItems();
        if (isDeletionConfirmed(selectedItems.size())) {
            for (CargoNumber number : selectedItems) {
                dataTable.getItems().remove(number);
            }
        }
    }

    private void clearItemsMenuHandler() {
        if (isDeletionConfirmed(dataTable.getItems().size())) {
            dataTable.getItems().clear();
            stage.hide();
        }
    }

    private boolean isDeletionConfirmed(int amount) {
        return alertService.showConfirmDialog(
                localizeService.getLocalizedResource("lw.alert.title"),
                String.format(localizeService.getLocalizedResource("cnl.alert.message"), amount)
        );
    }

    private ContextMenu getContextMenu(TableView<CargoNumber> dataTable, Consumer<ActionEvent> removeConsumer, Consumer<ActionEvent> clearConsumer) {
        List<Action> actions = windowControlsProducer.getActions(removeConsumer, clearConsumer);
        Action removeAction = actions.get(0);
        MenuItem miRemove = ActionUtils.createMenuItem(removeAction);
        MenuItem miClear = ActionUtils.createMenuItem(actions.get(1));
        ContextMenu menu = new ContextMenu(miRemove, miClear);
        menu.setOnShowing(e -> {
            removeAction.setDisabled(dataTable.getItems().size() == 0 || dataTable.getSelectionModel().getSelectedItems().size() == 0);
        });
        return menu;
    }
}
