package das.tools.np.gui.controllers;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.gui.NumberInfoViewService;
import das.tools.np.gui.WindowListService;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.services.ConfigService;
import das.tools.np.services.LocalizeResourcesService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component @Scope("prototype")
@FxmlView("/fxml/Detail.fxml")
@Slf4j
public class DetailedInfoController {
    private static final int MIN_WIDTH = 300;
    private static final int MIN_HEIGHT = 400;
    @FXML private AnchorPane root;
    @FXML private Button btOk;
    @FXML private AnchorPane pnInfo;
    @FXML private Label lbNumber;

    private Stage stage;

    private final NumberInfoViewService infoService;
    private final LocalizeResourcesService localizeService;
    private final WindowListService windowService;
    private final ConfigService configService;
    private final FxWeaver fxWeaver;

    public DetailedInfoController(NumberInfoViewService infoService, LocalizeResourcesService localizeService, WindowListService windowService, ConfigService configService, FxWeaver fxWeaver) {
        this.infoService = infoService;
        this.localizeService = localizeService;
        this.windowService = windowService;
        this.configService = configService;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void initialize() {
        this.stage = new Stage();
        this.stage.setScene(new Scene(root));
        MainController controller = fxWeaver.loadController(MainController.class);
        this.stage.getIcons().add(controller.getWindowIcon());
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setOnCloseRequest(e -> {
            windowService.remove(stage);
            configService.saveWindowPosition(stage, WindowType.DETAIL);
        });
        btOk.setOnAction(e -> {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            stage.close();
        });
    }

    public void show(CargoNumber number) {
        stage.setTitle(String.format(localizeService.getLocalizedResource("detail.title"), number.getNumber()));
        lbNumber.setText(number.getNumber());
        infoService.showInfo(pnInfo, number, true);
        windowService.put(stage);
        configService.populateWindowPosition(stage, WindowType.DETAIL);
        stage.show();
    }
}
