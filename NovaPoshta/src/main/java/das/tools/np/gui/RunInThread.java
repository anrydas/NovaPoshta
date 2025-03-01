package das.tools.np.gui;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunInThread implements Runnable {
    private final Thread thread;
    private static int counter = 0;

    public RunInThread(Runnable runnable) {
        this.thread = new Thread(() -> Platform.runLater(runnable), "RunInThread#" + counter++);
        this.thread.setDaemon(true);
        if (log.isDebugEnabled()) log.debug("Created thread {}", this.thread.getName());
    }

    @Override
    public void run() {
        this.thread.start();
        if (log.isDebugEnabled()) log.debug("Started thread {}", this.thread.getName());
    }

}
