package das.tools.np.gui.controllers;

import das.tools.np.gui.Localized;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.repository.ExtraPhonesRepository;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import das.tools.np.services.LocalizeResourcesService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.regex.Pattern;

@Component
@FxmlView("/fxml/Config.fxml")
@Slf4j
public class ConfigController implements Localized {
    protected static final int SEARCH_HISTORY_MIN_VALUE = 10;
    protected static final int SEARCH_HISTORY_MAX_VALUE = 500;
    protected static final int LOG_RECORDS_MIN_VALUE = 10;
    protected static final int LOG_RECORDS_MAX_VALUE = 5000;
    public static final Pattern API_URL_PATTERN = Pattern.compile("^https://api\\.novaposhta\\.ua/[-a-zA-Z0-9.]+/json/$");
    private final CommonService commonService;
    @FXML private AnchorPane root;
    @FXML private Button btOk;
    @FXML private Button btCancel;
    @FXML private Label lbUrl;
    @FXML private TextField edUrl;
    @FXML private Label lbPhone;
    @FXML private TextField edPhone;
    @FXML private Label lbLanguage;
    @FXML private ComboBox<String> cbLanguage;
    @FXML private Label lbMaxRecords;
    @FXML private Spinner<Integer> spMaxHistoryRecords;
    @FXML private Label lbMaxLogRecords;
    @FXML private Spinner<Integer> spMaxLogRecords;
    @FXML private CheckBox chbAutoUpdate;

    private Stage stage;
    private boolean isConfigChanged;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private final ConfigService configService;
    private final AlertService alertService;
    private final LocalizeResourcesService localizeService;
    private final ExtraPhonesRepository phonesRepository;
    private final FxWeaver fxWeaver;

    public ConfigController(ConfigService configService, AlertService alertService, LocalizeResourcesService localizeService, CommonService commonService, ExtraPhonesRepository phonesRepository, FxWeaver fxWeaver) {
        this.configService = configService;
        this.alertService = alertService;
        this.localizeService = localizeService;
        this.commonService = commonService;
        this.phonesRepository = phonesRepository;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void initialize() {
        isConfigChanged = false;
        this.stage = new Stage();
        this.stage.setScene(new Scene(root));
        MainController controller = fxWeaver.loadController(MainController.class);
        this.stage.getIcons().add(controller.getWindowIcon());
        btOk.setOnAction(e -> saveConfig());
        btCancel.setOnAction(e -> this.stage.close());
        edUrl.setText(configService.getConfigValue(ConfigService.CONFIG_END_POINT_KEY));
        edPhone.setText(configService.getConfigValue(ConfigService.CONFIG_PHONE_NUMBER_KEY));
        cbLanguage.setItems(configService.getLanguagesList());
        String lang = configService.getLangName(configService.getConfigValue(ConfigService.CONFIG_LANGUAGE_KEY));
        cbLanguage.getSelectionModel().select(lang);
        chbAutoUpdate.setSelected(configService.isAutoUpdateEnabled());
        spMaxHistoryRecords.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(SEARCH_HISTORY_MIN_VALUE, SEARCH_HISTORY_MAX_VALUE,
                configService.getMaxSearchHistoryRecords(), 10));
        spMaxHistoryRecords.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            spinnerOnValueChange(spMaxHistoryRecords, oldValue, newValue, SEARCH_HISTORY_MIN_VALUE, SEARCH_HISTORY_MAX_VALUE);
        });
        spMaxLogRecords.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LOG_RECORDS_MIN_VALUE, LOG_RECORDS_MAX_VALUE,
                configService.getMaxLogRecords(), 100));
        spMaxLogRecords.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            spinnerOnValueChange(spMaxLogRecords, oldValue, newValue, LOG_RECORDS_MIN_VALUE, LOG_RECORDS_MAX_VALUE);
        });
        validationSupport.registerValidator(edUrl, Validator.createRegexValidator(localizeService.getLocalizedResource("config.alert.message.wrongApiUrl"),
                API_URL_PATTERN, Severity.ERROR));
        validationSupport.registerValidator(edPhone, Validator.createRegexValidator(localizeService.getLocalizedResource("config.alert.message.wrongPhone"),
                ExtraPhonesRepository.PHONE_PATTERN, Severity.ERROR));
    }

    private void spinnerOnValueChange(Spinner<Integer> spinner, Integer oldValue, Integer newValue, Integer min, Integer max) {
        if (newValue == null) {
            spinner.getValueFactory().setValue(oldValue);
        } else if (newValue < min) {
            spinner.getValueFactory().setValue(min);
        } else if (newValue > max) {
            spinner.getValueFactory().setValue(max);
        }
    }

    @Override
    public void initLocale() {
        stage.setTitle(localizeService.getLocalizedResource("config.title"));
        lbUrl.setText(localizeService.getLocalizedResource("config.label.url"));
        lbPhone.setText(localizeService.getLocalizedResource("config.label.phone"));
        lbLanguage.setText(localizeService.getLocalizedResource("config.label.language"));
        lbMaxRecords.setText(localizeService.getLocalizedResource("config.label.maxHistory"));
        Tooltip tooltip = new Tooltip(String.format(
                localizeService.getLocalizedResource("config.tooltip.maxHistory"), SEARCH_HISTORY_MIN_VALUE, SEARCH_HISTORY_MAX_VALUE));
        lbMaxRecords.setTooltip(tooltip);
        spMaxHistoryRecords.setTooltip(tooltip);
        lbMaxLogRecords.setText(localizeService.getLocalizedResource("config.label.maxLogRecords"));
        chbAutoUpdate.setText(localizeService.getLocalizedResource("config.label.autoUpdate"));
    }

    public void show() {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        initLocale();
        stage.showAndWait();
    }

    public boolean isConfigChanged() {
        return isConfigChanged;
    }

    private void saveConfig() {
        if (isFieldsValid()) {
            Properties oldProps = configService.getCurrentConfig();
            Properties props = getUpdatedProperties();
            this.isConfigChanged = !oldProps.equals(props);
            if (isConfigChanged) {
                configService.saveConfig(props);
                localizeService.initLocale();
                stage.close();
            }
        }
    }

    private Properties getUpdatedProperties() {
        Properties props = new Properties();
        props.setProperty(ConfigService.CONFIG_END_POINT_KEY, edUrl.getText());
        props.setProperty(ConfigService.CONFIG_PHONE_NUMBER_KEY, edPhone.getText());
        props.setProperty(ConfigService.CONFIG_LANGUAGE_KEY, configService.getLangCode(cbLanguage.getValue()));
        props.setProperty(ConfigService.CONFIG_ENABLE_AUTO_UPDATE, String.valueOf(chbAutoUpdate.isSelected()));
        props.setProperty(ConfigService.CONFIG_MAX_SEARCH_HISTORY_KEY, String.valueOf(spMaxHistoryRecords.getValue()));
        if (log.isDebugEnabled()) log.debug("saved config={}", props);
        return props;
    }

    private boolean isFieldsValid() {
        if (!API_URL_PATTERN.matcher(edUrl.getText()).matches()) {
            edUrl.requestFocus();
            showError(localizeService.getLocalizedResource("config.alert.message.wrongApiUrl"));
            return false;
        }
        if (!phonesRepository.isPhoneValid(edPhone.getText())) {
            edPhone.requestFocus();
            showError(localizeService.getLocalizedResource("config.alert.message.wrongPhone"));
            return false;
        }
        return true;
    }

    private void showError(String message) {
        alertService.showError(localizeService.getLocalizedResource("config.alert.title"), message, "");
    }
}
