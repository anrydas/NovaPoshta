package das.tools.np.controller;

import das.tools.np.gui.RunInThread;
import das.tools.np.gui.controllers.MainController;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateController {
    private final FxWeaver fxWeaver;

    public UpdateController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Async
    @Scheduled(fixedDelay = 600000, initialDelay = 30000)
    public void updateNumbers() {
        MainController controller = fxWeaver.loadController(MainController.class);
        new RunInThread(() -> controller.scheduledNumbersUpdate(false)).run();
    }
}
