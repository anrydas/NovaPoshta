package das.tools.np.gui.controllers.edit;

import das.tools.np.gui.menu.ActionService;
import das.tools.np.services.CommonService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.ListActionView;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class EditControlsProducer<T> {
    private final GlyphFont glyphFont;
    private final EditActionTextService actionTextService;
    private final CommonService commonService;

    public EditControlsProducer(GlyphFont glyphFont, EditActionTextService actionTextService, CommonService commonService) {
        this.glyphFont = glyphFont;
        this.actionTextService = actionTextService;
        this.commonService = commonService;
    }


    public EditControlsHolder<T> getNewControls(int width, int height) {
        return getControls(width, height);
    }

    private EditControlsHolder<T> getControls(int width, int height) {
        AnchorPane rootPane = getRootPane(width, height);
        Button btOk = getButton("OK");
        btOk.setDefaultButton(true);
        Button btCancel = getButton("Cancel");
        btOk.setCancelButton(true);
        rootPane.getChildren().add(getButtonBox(btOk, btCancel));
        return EditControlsHolder.<T>builder()
                .root(rootPane)
                .btOk(btOk)
                .btCancel(btCancel)
                .listView(getListView(width))
                .build();
    }

    private AnchorPane getRootPane(int width, int height) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);
        return pane;
    }

    public HBox getButtonBox(Button btOk, Button btCancel) {
        HBox hBox = new HBox(20, btOk, btCancel);
        AnchorPane.setBottomAnchor(hBox, 5.0);
        AnchorPane.setLeftAnchor(hBox, 10.0);
        AnchorPane.setRightAnchor(hBox, 10.0);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    public Button getButton(String text) {
        Button bt = new Button(text);
        bt.setPrefHeight(24);
        bt.setPrefWidth(88);
        return bt;
    }

    private ListView<T> getListView(int width) {
        ListView<T> listView = new ListView<>();
        AnchorPane.setTopAnchor(listView, 5.0);
        AnchorPane.setLeftAnchor(listView, 5.0);
        AnchorPane.setRightAnchor(listView, 5.0);
        AnchorPane.setBottomAnchor(listView, 40.0);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setPrefWidth(width * 0.9);
        return listView;
    }
    
    public Node getToolBar(Collection<? extends Action> toolBarActions) {
        ToolBar tb = ActionUtils.createToolBar(toolBarActions, ActionUtils.ActionTextBehavior.HIDE);
        tb.setOrientation(Orientation.VERTICAL);
        tb.borderProperty().setValue(Border.EMPTY);
        tb.getItems().forEach(i -> {
            if (i instanceof Button) {
                ((Button) i).setPrefWidth(30);
                ((Button) i).setPrefHeight(30);
            }
        });
        return tb;
    }

    public ObservableList<Action> getMoveActions() {
        ListActionView.ListAction<T> moveUp = new ListActionView.ListAction<>(glyphFont.create(FontAwesome.Glyph.ANGLE_UP)) {
            @Override
            public void initialize(ListView<T> listView) {
                setEventHandler(e -> moveItemsUp(listView));
            }
        };
        ListActionView.ListAction<T> moveDown = new ListActionView.ListAction<>(glyphFont.create(FontAwesome.Glyph.ANGLE_DOWN)) {
            @Override
            public void initialize(ListView<T> listView) {
                setEventHandler(e -> moveItemsDown(listView));
            }
        };
        moveUp.setStyle(EditAction.MOVE_UP.name());
        moveDown.setStyle(EditAction.MOVE_DOWN.name());
        return FXCollections.observableArrayList(moveUp, moveDown, ActionUtils.ACTION_SEPARATOR);
    }

    public Action getMoveUpAction(ListView<T> listView) {
        ActionService.AppAction action = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.ANGLE_UP),
                e -> moveItemsUp(listView), null);
        action.setStyle(EditAction.MOVE_UP.name());
        return action;
    }

    public Action getMoveDownAction(ListView<T> listView) {
        ActionService.AppAction action = new ActionService.AppAction("", glyphFont.create(FontAwesome.Glyph.ANGLE_DOWN),
                e -> moveItemsDown(listView), null);
        action.setStyle(EditAction.MOVE_DOWN.name());
        return action;
    }

    public void moveItemsUp(ListView<T> listView) {
        log.warn("!!!");
        MultipleSelectionModel<T> selectionModel = listView.getSelectionModel();
        ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
        ObservableList<T> items = listView.getItems();
        for (Integer selectedIndex : selectedIndices) {
            if (selectedIndex > 0) {
                if (selectedIndices.contains(selectedIndex - 1)) continue;
                T item = items.get(selectedIndex);
                T itemToBeReplaced = items.get(selectedIndex - 1);
                items.set(selectedIndex - 1, item);
                items.set(selectedIndex, itemToBeReplaced);
                selectionModel.clearSelection(selectedIndex);
                selectionModel.select(selectedIndex - 1);
            }
        }
    }

    public void moveItemsDown(ListView<T> listView) {
        MultipleSelectionModel<T> selectionModel = listView.getSelectionModel();
        List<Integer> selectedIndices = selectionModel.getSelectedIndices();
        ObservableList<T> items = listView.getItems();
        int lastIndex = items.size() - 1;
        for (int index = selectedIndices.size() - 1; index >= 0; index--) {
            Integer selectedIndex = selectedIndices.get(index);
            if (selectedIndex < lastIndex) {
                if (selectedIndices.contains(selectedIndex + 1)) continue;
                T item = items.get(selectedIndex);
                T itemToBeReplaced = items.get(selectedIndex + 1);
                items.set(selectedIndex + 1, item);
                items.set(selectedIndex, itemToBeReplaced);
                selectionModel.clearSelection(selectedIndex);
                selectionModel.select(selectedIndex + 1);
            }
        }
    }

    public String getLocalizedActionText(Action action) {
        return  actionTextService.getActionText(EditAction.valueOf(action.getStyle()));
    }

    public void updateActionsText(Collection<? extends Action> actions) {
        for (Action action : actions) {
            String actionName = action.getStyle();
            if (commonService.isNotEmpty(actionName)) {
                action.setText(getLocalizedActionText(action));
            }
        }
    }
}
