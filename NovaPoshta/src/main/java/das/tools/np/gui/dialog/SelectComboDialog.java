package das.tools.np.gui.dialog;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.util.List;

public class SelectComboDialog extends Dialog<ButtonType> {
    private final String text;
    private final List<String> items;
    private ComboBox<String> comboBox;

    public SelectComboDialog(String text, String content, List<String> items) {
        this.text = text;
        this.items = items;
        DialogPane root = this.getDialogPane();
        root.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        root.setHeader(getHeaderPane());
        root.setContent(getComboContent(content));
    }

    private Node getHeaderPane() {
        Node textBox = getTextBox();
        Node imageBox = getImageBox();
        HBox headerPane = new HBox(20, textBox, imageBox);
        headerPane.getStyleClass().addAll("header-panel");
        return headerPane;
    }

    private Node getTextBox() {
        Label headerText = new Label(this.text);
        HBox box = new HBox(10, headerText);
        box.setAlignment(Pos.CENTER_LEFT);
        AnchorPane.setTopAnchor(box, 5.0);
        AnchorPane.setLeftAnchor(box, 5.0);
        return box;
    }

    private Node getComboContent(String content) {
        comboBox = new ComboBox<>();
        comboBox.setPrefWidth(150);
        comboBox.getItems().addAll(this.items);
        if (items.size() > 0) comboBox.getSelectionModel().select(0);
        HBox hBox = new HBox(10, new Label(content), comboBox);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    public String getSelectedValue() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    private Node getImageBox() {
        Label headerImage = new Label("",  GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.GEARS).color(Color.BLUE).sizeFactor(3));
        HBox box = new HBox(10, headerImage);
        box.setAlignment(Pos.CENTER_RIGHT);
        AnchorPane.setTopAnchor(box, 5.0);
        AnchorPane.setRightAnchor(box, 5.0);
        return box;
    }
}
