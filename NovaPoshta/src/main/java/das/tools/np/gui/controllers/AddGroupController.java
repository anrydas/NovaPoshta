package das.tools.np.gui.controllers;

import das.tools.np.entity.db.Group;
import das.tools.np.gui.ApplicationLogService;
import das.tools.np.gui.Localized;
import das.tools.np.gui.NumberTreeViewService;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.services.CommonService;
import das.tools.np.services.GroupService;
import das.tools.np.services.LocalizeResourcesService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("/fxml/AddGroup.fxml")
@Slf4j
public class AddGroupController implements Localized {
    @FXML private ImageView img;
    @FXML private Button btOk;
    @FXML private Button btCancel;
    @FXML private Label lbName;
    @FXML private TextField edName;
    @FXML private AnchorPane root;

    private Stage stage;

    private final LocalizeResourcesService localizeService;
    private final GroupService groupService;
    private final CommonService commonService;
    private final AlertService alertService;
    private final ApplicationLogService logService;
    private final FxWeaver fxWeaver;

    public AddGroupController(LocalizeResourcesService localizeService, GroupService groupService, CommonService commonService, AlertService alertService, ApplicationLogService logService, FxWeaver fxWeaver) {
        this.localizeService = localizeService;
        this.groupService = groupService;
        this.commonService = commonService;
        this.alertService = alertService;
        this.logService = logService;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void initialize() {
        this.stage = new Stage();
        this.stage.setScene(new Scene(root));
        MainController controller = fxWeaver.loadController(MainController.class);
        this.stage.getIcons().add(controller.getWindowIcon());
        btOk.setOnAction(e -> btOkClicked());
        btCancel.setOnAction(e -> stage.close());
        img.setImage(commonService.loadImage(NumberTreeViewService.GROUP_IMAGE));
    }

    public void show() {
        initLocale();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(localizeService.getLocalizedResource("group.add.title"));
        stage.setResizable(false);
        stage.show();
        edName.requestFocus();
    }

    @Override
    public void initLocale() {
        lbName.setText(localizeService.getLocalizedResource("group.add.label.name"));
    }

    private void btOkClicked() {
        String groupName = edName.getText();
        if (commonService.isNotEmpty(groupName)) {
            if (!groupService.isGroupExists(groupName)) {
                groupService.add(Group.builder()
                        .name(groupName)
                        .build());
                logService.populateInfoMessage(String.format(localizeService.getLocalizedResource("group.message.newGroup"), groupName));
                stage.close();
            } else {
                alertService.showError(localizeService.getLocalizedResource("group.alert.exists.title"),
                        String.format(localizeService.getLocalizedResource("group.alert.exists.message"), groupName));
            }
        } else {
            alertService.showError(localizeService.getLocalizedResource("group.alert.EmptyName.title"),
                    localizeService.getLocalizedResource("group.alert.EmptyName.message"));
        }
    }
}
