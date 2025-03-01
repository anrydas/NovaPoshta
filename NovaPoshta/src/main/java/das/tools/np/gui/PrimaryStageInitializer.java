package das.tools.np.gui;

import das.tools.np.entity.StageReadyEvent;
import das.tools.np.gui.controllers.MainController;
import das.tools.np.gui.controllers.MainControllerImpl;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;
    private final MainController mainController;
    private final CommonService commonService;
    private final ConfigService configService;

    public PrimaryStageInitializer(FxWeaver fxWeaver, MainController mainController, CommonService commonService, ConfigService configService) {
        this.fxWeaver = fxWeaver;
        this.mainController = mainController;
        this.commonService = commonService;
        this.configService = configService;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.stage;
        Scene scene = new Scene(fxWeaver.loadView(MainControllerImpl.class));
        stage.setScene(scene);
        stage.setTitle(MainController.APPLICATION_TITLE);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.setIconified(true);
            }
        });
        stage.getIcons().add(commonService.loadImage("/images/np_app_icon.png"));
        stage.setOnShowing(e -> mainController.onShowingStage());
        stage.setOnCloseRequest(mainController::onClosingStage);
        configService.populateWindowPosition(stage, WindowType.MAIN);
        stage.show();
    }
}
