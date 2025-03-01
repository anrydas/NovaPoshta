package das.tools.np.gui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.stage.Window;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    public void showError(String header, String content) {
        showDialog(Alert.AlertType.ERROR, "Error", header, content);
    }

    public void showError(String title, String header, String content) {
        showDialog(Alert.AlertType.ERROR, title, header, content);
    }

    public void showInfo(String header, String content) {
        showDialog(Alert.AlertType.INFORMATION, "Information", header, content);
    }

    public void showInfo(String title, String header, String content) {
        showDialog(Alert.AlertType.INFORMATION, title, header, content);
    }

    public boolean showConfirmDialog(String title, String header, String content) {
        Optional<ButtonType> buttonType = showDialogAndWait(Alert.AlertType.CONFIRMATION, title, header, content);
        return buttonType.map(type -> type.equals(ButtonType.OK)).orElse(false);
    }

    public boolean showConfirmDialog(String title, String header) {
        Optional<ButtonType> buttonType = showDialogAndWait(Alert.AlertType.CONFIRMATION, title, header, "");
        return buttonType.map(type -> type.equals(ButtonType.OK)).orElse(false);
    }

    private void showDialog(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> showDialogAndWait(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public String getTextInputDialogResult(String title, String value, String header, String content, Window owner, ImageView graphics) {
        TextInputDialog dlg = new TextInputDialog(value);
        dlg.setTitle(title);
        if (!"".equals(header)) dlg.getDialogPane().setHeaderText(header);
        if (!"".equals(content)) dlg.getDialogPane().setContentText(content);
        if (owner != null) dlg.initOwner(owner);
        if (graphics != null) dlg.getDialogPane().setGraphic(graphics);
        dlg.showAndWait();
        return dlg.getResult();
    }

    public String getTextSelectComboDialogResult(String title, String header, String content, List<String> items, Window owner) {
        SelectComboDialog dlg = new SelectComboDialog(header, content, items);
        dlg.setTitle(title);
        if (!"".equals(header)) dlg.getDialogPane().setHeaderText(header);
        if (owner != null) dlg.initOwner(owner);
        dlg.showAndWait();
        if (dlg.getResult() == ButtonType.OK) {
            return dlg.getSelectedValue();
        }
        return "";
    }
}
