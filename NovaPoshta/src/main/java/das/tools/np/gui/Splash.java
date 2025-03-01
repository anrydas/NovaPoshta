package das.tools.np.gui;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;

public class Splash {
    public void show() {
        new RunInThread(() -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            AnchorPane root = getRootPane();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.show();
            FadeTransition fadeIn = getFadeTransition(root, 0, 1, 3);
            FadeTransition fadeOut = getFadeTransition(root, 1, 0, 5);
            fadeIn.setOnFinished((e) -> {
                fadeOut.play();
            });
            fadeOut.setOnFinished((e) -> {
                stage.close();
            });
            fadeIn.play();
        }).run();
    }

    private AnchorPane getRootPane() {
        AnchorPane root = new AnchorPane();
        root.getChildren().add(new ImageView(new Image(
                Objects.requireNonNull(Splash.class.getResourceAsStream("/images/np_app_icon.png")))
        ));
        return root;
    }

    private FadeTransition getFadeTransition(Node node, int from, int to, int duration) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(duration), node);
        fadeIn.setFromValue(from);
        fadeIn.setToValue(to);
        fadeIn.setCycleCount(1);
        return fadeIn;
    }
}
