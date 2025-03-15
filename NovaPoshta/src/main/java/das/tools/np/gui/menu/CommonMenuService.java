package das.tools.np.gui.menu;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.db.Group;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommonMenuService {
    public MenuItem getMenuItem(String title, EventHandler<ActionEvent> event) {
        return getMenuItem(title, null, event, null);
    }
    public MenuItem getMenuItem(String title, EventHandler<ActionEvent> event, KeyCombination key) {
        return getMenuItem(title, null, event, key);
    }

    public MenuItem getMenuItem(String title, Node graphic, EventHandler<ActionEvent> event, KeyCombination key) {
        MenuItem menuItem = new MenuItem(title);
        if (graphic != null) menuItem.setGraphic(graphic);
        if (event != null) menuItem.setOnAction(event);
        if (key != null) menuItem.setAccelerator(key);
        return menuItem;
    }

    public CheckMenuItem getCheckMenuItem(String title, EventHandler<ActionEvent> event, KeyCombination key) {
        CheckMenuItem menuItem = new CheckMenuItem(title);
        if (event != null) menuItem.setOnAction(event);
        if (key != null) menuItem.setAccelerator(key);
        menuItem.setSelected(false);
        return menuItem;
    }

    public RadioMenuItem getRadioMenuItem(String title, EventHandler<ActionEvent> event, KeyCombination key, boolean checked) {
        RadioMenuItem menuItem = new RadioMenuItem(title);
        if (event != null) menuItem.setOnAction(event);
        if (key != null) menuItem.setAccelerator(key);
        menuItem.setSelected(checked);
        return menuItem;
    }

    public RadioMenuItem getGroupMenuItem(Group group, CargoNumber number, EventHandler<ActionEvent> event) {
        RadioMenuItem menuItem = new RadioMenuItem(group.getName());
        if (event != null) menuItem.setOnAction(event);
        menuItem.setSelected(group.getId() == number.getGroup().getId());
        menuItem.setId(String.valueOf(group.getId()));
        return menuItem;
    }

    public RadioMenuItem getViewMenuItem(String name, boolean selected, EventHandler<ActionEvent> event, KeyCombination key) {
        RadioMenuItem menuItem = new RadioMenuItem(name);
        if (event != null) menuItem.setOnAction(event);
        if (key != null) menuItem.setAccelerator(key);
        menuItem.setSelected(selected);
        return menuItem;
    }

    public CustomMenuItem getTitleCtxMenuItem(String title) {
        Label label = new Label(String.format("    [ %s ]", title));
        label.setStyle("-fx-font-weight: bold");
        CustomMenuItem menuItem = new CustomMenuItem(label);
        menuItem.setHideOnClick(false);
        return menuItem;
    }

    public Menu getMenu(String title, Node graphics, MenuItem... items) {
        Menu menu = new Menu(title);
        menu.getItems().addAll(items);
        menu.setGraphic(graphics);
        return menu;
    }
}
