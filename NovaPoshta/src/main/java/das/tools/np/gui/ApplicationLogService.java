package das.tools.np.gui;

import das.tools.np.entity.response.AppResponse;
import das.tools.np.gui.dialog.ToastComponent;
import das.tools.np.gui.enums.LogMessageType;
import das.tools.np.services.ConfigService;
import das.tools.np.services.impl.LocalizeResourcesService;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ApplicationLogService {
    public static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String LOG_MESSAGE_PATTERN = "[%s]: %s\n";
    private final ConfigService configService;
    private final LocalizeResourcesService localizeService;
    private final ToastComponent toast;
    private TextFlow logControl;

    public ApplicationLogService(ConfigService configService, LocalizeResourcesService localizeService, ToastComponent toast) {
        this.configService = configService;
        this.localizeService = localizeService;
        this.toast = toast;
    }

    public void initLogControl(TextFlow log) {
        this.logControl = log;
    }

    public void populateLogMessage(AppResponse response, TextFlow log) {
        if (response.getErrors() != null) {
            addMessages(log, response.getErrors(), "error-text");
        } else if (response.getWarnings() != null) {
            addMessages(log, response.getWarnings(), "warn-text");
        } else if (response.getInfo() != null) {
            addMessages(log, response.getWarnings(), "info-text");
        }
    }

    public void populateLogMessage(LogMessageType type, String message, TextFlow log) {
        if (type == LogMessageType.ERROR) {
            addMessage(log, message, "error-text");
        } else if (type == LogMessageType.WARN) {
            addMessage(log, message, "warn-text");
        } else if (type == LogMessageType.INFO) {
            addMessage(log, message, "info-text");
        }
    }

    public void populateInfoMessage(String message, TextFlow log) {
        populateLogMessage(LogMessageType.INFO, message, log);
    }

    public void populateErrorMessage(String message, TextFlow log) {
        populateLogMessage(LogMessageType.ERROR, message, log);
    }

    public void populateWarnMessage(String message, TextFlow log) {
        populateLogMessage(LogMessageType.WARN, message, log);
    }

    public void populateInfoMessage(String message) {
        if (logControl != null) {
            populateLogMessage(LogMessageType.INFO, message, logControl);
        }
    }

    public void populateErrorMessage(String message) {
        if (logControl != null) {
            populateLogMessage(LogMessageType.ERROR, message, logControl);
        }
    }

    public void populateWarnMessage(String message) {
        if (logControl != null) {
            populateLogMessage(LogMessageType.WARN, message, logControl);
        }
    }

    public void clearLog() {
        if (logControl != null) {
            logControl.getChildren().clear();
        }
    }

    public void copyLogToClip() {
        if (logControl != null && logControl.getChildren().size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Node node : logControl.getChildren()) {
                if (node instanceof Text) {
                    sb.append(((Text) node).getText());
                }
            }
            String value = sb.toString();
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(value), null);
            toast.makeToast((Stage) logControl.getScene().getWindow(),
                    localizeService.getLocalizedResource("info.button.message.copy"));
        }
    }

    private void copyToClip() {

    }

    private void addMessages(TextFlow log, String[] messages, String style) {
        for (String message : messages) {
            Text text = new Text(String.format(LOG_MESSAGE_PATTERN, LOG_DATE_FORMAT.format(new Date()), message));
            text.setId(style);
            putMessage(log, text);
        }
    }

    private void addMessage(TextFlow log, String message, String style) {
        Text text = new Text(String.format(LOG_MESSAGE_PATTERN, LOG_DATE_FORMAT.format(new Date()), message));
        text.setId(style);
        putMessage(log, text);
    }

    private void putMessage(TextFlow log, Text text) {
        if (log.getChildren().size() > configService.getMaxLogRecords()) {
            log.getChildren().remove(log.getChildren().size() - 1);
        }
        log.getChildren().add(0, text);
    }
}
