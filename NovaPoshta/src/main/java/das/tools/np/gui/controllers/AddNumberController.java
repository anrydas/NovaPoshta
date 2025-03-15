package das.tools.np.gui.controllers;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.Group;
import das.tools.np.entity.db.NumberType;
import das.tools.np.entity.db.SimpleNumber;
import das.tools.np.entity.response.AppResponse;
import das.tools.np.gui.ApplicationLogService;
import das.tools.np.gui.Localized;
import das.tools.np.gui.WindowListService;
import das.tools.np.gui.controllers.edit.EditPhonesController;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.gui.dialog.ToastComponent;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.repository.ExtraPhonesRepository;
import das.tools.np.repository.GroupRepository;
import das.tools.np.repository.PropertiesRepository;
import das.tools.np.services.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component @Scope("prototype")
@FxmlView("/fxml/Add.fxml")
@Slf4j
public class AddNumberController implements Localized {
    public static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");
    @FXML private AnchorPane root;
    @FXML private Label lbNumber;
    @FXML private TextField edNumber;
    @FXML private Label lbType;
    @FXML private ComboBox<NumberType> cbType;
    @FXML private HBox hbType;
    @FXML private CheckBox chbAutoUpdate;
    @FXML private CheckBox chbAddAnother;
    @FXML private Button btOk;
    @FXML private Button btCancel;
    @FXML private Label lbPhone;
    @FXML private ComboBox<String> cbPhone;
    @FXML private Button btAddPhone;
    @FXML private Button btEditPhones;
    @FXML private Label lbComment;
    @FXML private TextField edComment;
    @FXML private Label lbGroup;
    @FXML private ComboBox<Group> cbGroup;

    private final LocalizeResourcesService localizeService;
    private final CargoStatusService statusService;
    private final CargoTypeService typeService;
    private final ApplicationLogService logService;
    private final AlertService alertService;
    private final CargoNumberRepository numberRepository;
    private final CargoNumberService numberService;
    private final ApiService apiService;
    private final ConversionService conversionService;
    private final PropertiesRepository propertiesRepository;
    private final ExtraPhonesRepository phonesRepository;
    private final ToastComponent toast;
    private final FxWeaver fxWeaver;
    private final WindowListService windowService;
    private final GlyphFont glyphFont;
    private final GroupRepository groupRepository;
    private Stage stage;
    private boolean isNumberAdded;
    private MainController mainController;

    public AddNumberController(LocalizeResourcesService localizeService, CargoStatusService statusService, CargoTypeService typeService, ApplicationLogService logService, AlertService alertService, CargoNumberRepository numberRepository, CargoNumberService numberService, ApiService apiService, ConversionService conversionService, PropertiesRepository propertiesRepository, ExtraPhonesRepository phonesRepository, ToastComponent toast, FxWeaver fxWeaver, WindowListService windowService, GlyphFont glyphFont, GroupRepository groupRepository) {
        this.localizeService = localizeService;
        this.statusService = statusService;
        this.typeService = typeService;
        this.logService = logService;
        this.alertService = alertService;
        this.numberRepository = numberRepository;
        this.numberService = numberService;
        this.apiService = apiService;
        this.conversionService = conversionService;
        this.propertiesRepository = propertiesRepository;
        this.phonesRepository = phonesRepository;
        this.toast = toast;
        this.fxWeaver = fxWeaver;
        this.windowService = windowService;
        this.glyphFont = glyphFont;
        this.groupRepository = groupRepository;
    }

    @FXML
    public void initialize() {
        this.stage = new Stage();
        windowService.put(stage);
        this.stage.setScene(new Scene(root));
        mainController = fxWeaver.loadController(MainController.class);
        this.stage.getIcons().add(mainController.getWindowIcon());
        stage.focusedProperty().addListener(e -> actualizePhonesList());
        stage.setOnCloseRequest(e -> {
            windowService.remove(stage);
        });
        btOk.setOnAction(e -> btOkClicked());
        btCancel.setOnAction(e -> stage.close());
        cbType.getItems().addAll(NumberType.UNDEF, NumberType.IN, NumberType.OUT);
        cbType.setConverter(new StringConverter<>() {
            @Override
            public String toString(NumberType numberType) {
                return typeService.getTypeName(numberType);
            }

            @Override
            public NumberType fromString(String s) {
                return typeService.getTypeByName(s);
            }
        });
        cbType.valueProperty().addListener((observableValue, numberType, t1) -> updateImageLabel(t1));
        cbType.getSelectionModel().select(0);
        cbGroup.getItems().addAll(groupRepository.getAll());
        cbGroup.setConverter(new StringConverter<>() {
            @Override
            public String toString(Group group) {
                return group.getName();
            }

            @Override
            public Group fromString(String s) {
                return groupRepository.findByName(s);
            }
        });
        cbGroup.getSelectionModel().select(groupRepository.getDefault());
        btAddPhone.setOnAction(e -> addPhoneToDb());
        btEditPhones.setOnAction(e -> editPhones());
    }

    private void updateImageLabel(NumberType newType) {
        Label lb = new Label("", typeService.getTypeGlyphColored(newType, 24));
        if (hbType.getChildren().get(hbType.getChildren().size()-1) instanceof Label) {
            hbType.getChildren().remove(hbType.getChildren().size()-1);
        };
        hbType.getChildren().add(lb);
    }

    public void setUserData(String data) {
        this.stage.setUserData(data);
    }

    @Override
    public void initLocale() {
        lbNumber.setText(localizeService.getLocalizedResource("add.label.number"));
        lbType.setText(localizeService.getLocalizedResource("add.label.type"));
        lbPhone.setText(localizeService.getLocalizedResource("add.label.phone"));
        chbAutoUpdate.setText(localizeService.getLocalizedResource("add.label.update"));
        chbAddAnother.setText(localizeService.getLocalizedResource("add.label.another"));
        lbGroup.setText(localizeService.getLocalizedResource("add.label.group"));
        lbComment.setText(localizeService.getLocalizedResource("add.label.comment"));
        edComment.setPromptText(localizeService.getLocalizedResource("add.edit.comment.prompt"));
        btAddPhone.setTooltip(new Tooltip(localizeService.getLocalizedResource("add.tooltip.AddPhone")));
        btEditPhones.setTooltip(new Tooltip(localizeService.getLocalizedResource("add.tooltip.EditPhones")));
    }

    private void btOkClicked() {
        if (isDataValid()) {
            saveNumber();
            closeWindow();
        }
    }

    private boolean isDataValid() {
        if (!checkField(NUMBER_PATTERN, edNumber, localizeService.getLocalizedResource("add.alert.message.WrongNumber"))) {
            return false;
        }
        if (!checkField(ExtraPhonesRepository.PHONE_PATTERN, cbPhone, localizeService.getLocalizedResource("add.alert.message.WrongPhone"))) {
            return false;
        }

        return true;
    }

    private boolean checkField(Pattern pattern, Control control, String errorMessage) {
        String text = "";
        if (control instanceof TextField) {
            text = ((TextField) control).getText();
        } else if (control instanceof ComboBox<?>) {
            text = ((ComboBox<?>) control).getValue().toString();
        }
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            showError(errorMessage);
            control.requestFocus();
            return false;
        }
        return true;
    }

    private void saveNumber() {
        // check number existing in DB
        String number = edNumber.getText();
        if (numberRepository.isNumberExists(number)) {
            showError(String.format(localizeService.getLocalizedResource("add.alert.message.NumberAlreadyAdded"), number));
            return;
        }
        // get number data from NP server
        AppResponse response = apiService.getNewNumberResponse(new String[]{number}, cbPhone.getValue());
        if (response.getErrors().length > 0 || response.getWarnings().length > 0) {
            for (String message : response.getErrors()) {
                logService.populateErrorMessage(message);
            }
            for (String message : response.getWarnings()) {
                logService.populateWarnMessage(message);
            }
            return;
        }
        numberService.storeNumbersData(response, cbType.getSelectionModel().getSelectedItem(),
                edComment.getText(), cbPhone.getValue(), chbAutoUpdate.isSelected(), cbGroup.getSelectionModel().getSelectedItem());
        CargoNumber addedNumber = numberRepository.findByNumber(number);
        SimpleNumber simpleNumber = conversionService.convert(addedNumber, SimpleNumber.class);
        // add number into List
        this.mainController.updateNumberLists(simpleNumber);
        this.isNumberAdded = true;
        logService.populateInfoMessage(String.format("Added new number '%s'", number));
    }

    private void closeWindow() {
        if (!chbAddAnother.isSelected() && isNumberAdded) {
            stage.close();
        } else if (isNumberAdded) {
            isNumberAdded = false;
            initControls();
        }
    }

    private void initControls() {
        edNumber.clear();
        actualizePhonesList();
        btAddPhone.setGraphic(glyphFont.create(FontAwesome.Glyph.PLUS).size(15).color(Color.GREEN));
        btEditPhones.setGraphic(glyphFont.create(FontAwesome.Glyph.EDIT).size(14).color(Color.DARKRED));
    }

    private void actualizePhonesList() {
        cbPhone.getItems().clear();
        List<String> allPhones = cbPhone.getItems();
        allPhones.addAll(phonesRepository.getAllNumbers());
        if (cbPhone.getItems().size() > 0) {
            cbPhone.getSelectionModel().select(allPhones.get(0));
        }
    }

    private void addPhoneToDb() {
        if (checkField(ExtraPhonesRepository.PHONE_PATTERN, cbPhone, localizeService.getLocalizedResource("add.alert.message.WrongPhone"))) {
            String phone = cbPhone.getValue();
            String propPhone = propertiesRepository.getPhone();
            if (!phonesRepository.isPhoneAlreadyExists(phone) && !phone.equals(propPhone)) {
                phonesRepository.add(phone);
                toast.makeToast(stage, String.format(localizeService.getLocalizedResource("add.message.PhoneAdded"), phone));
            } else {
                showError(String.format(localizeService.getLocalizedResource("add.message.PhoneExists"), phone));
            }
        }
    }

    private void editPhones() {
        EditPhonesController controller = fxWeaver.loadController(EditPhonesController.class);
        controller.initialize();
        controller.show();
    }

    public void show() {
        initLocale();
        stage.setTitle(localizeService.getLocalizedResource("add.title"));
        stage.setResizable(false);
        initControls();
        stage.show();
        edNumber.requestFocus();
    }

    private void showError(String message) {
        logService.populateErrorMessage(message);
        alertService.showError(localizeService.getLocalizedResource("add.alert.title.error"), message);
    }
}
