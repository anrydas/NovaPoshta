package das.tools.np.gui.dialog;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ToastComponent {
    private static int TOAST_DELAY_MILLIS = 1500;
    private static int TOAST_FADE_IN_MILLIS = 500;
    private static int TOAST_FADE_OUT_MILLIS = 500;

    public void makeToast(Stage owner, String message, int delay, int fadeIn, int fadeOut) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(message);
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.WHITE);

        StackPane root = new StackPane(text);
        root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.4); -fx-padding: 10px;");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        Timeline timeline = new Timeline();
        KeyFrame fadeInKeyFrame = new KeyFrame(Duration.millis(fadeIn), new KeyValue(stage.getScene().getRoot().opacityProperty(), 1));
        timeline.getKeyFrames().add(fadeInKeyFrame);
        timeline.setOnFinished(event -> {
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    log.error("Error showing toast: ", e);
                }
                Timeline fadeOutTimeline = new Timeline();
                KeyFrame fadeOutKeyFrame = new KeyFrame(Duration.millis(fadeOut), new KeyValue (stage.getScene().getRoot().opacityProperty(), 0));
                fadeOutTimeline.getKeyFrames().add(fadeOutKeyFrame);
                fadeOutTimeline.setOnFinished(finishedEvent -> stage.close());
                fadeOutTimeline.play();
            }).start();
        });
        timeline.play();
    }

    public void makeToast(Stage owner, String message) {
        makeToast(owner, message, TOAST_DELAY_MILLIS, TOAST_FADE_IN_MILLIS, TOAST_FADE_OUT_MILLIS);
    }
}
