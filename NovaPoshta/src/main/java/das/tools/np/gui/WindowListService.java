package das.tools.np.gui;

import jakarta.annotation.PostConstruct;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class WindowListService {
    private List<Stage> windows;

    @PostConstruct
    private void init() {
        windows = new LinkedList<>();
    }

    public void put(Stage stage) {
        windows.add(stage);
    }

    public Stage get(Stage stage) {
        if (windows.contains(stage)) {
            return windows.get(windows.indexOf(stage));
        }
        return null;
    }

    public void remove(Stage stage) {
        windows.remove(stage);
    }

    public List<Stage> getWindowsList() {
        return windows;
    }

    public void showAllCascade() {
        int x = 50, y = 50;
        for (Stage stage : windows) {
            stage.setX(x);
            stage.setY(y);
            stage.show();
            x = x + 30;
            y = y + 30;
            stage.requestFocus();
        }
    }

    public void closeAll() {
        for (Stage stage : windows) {
            stage.close();
        }
        windows.clear();
    }
}
