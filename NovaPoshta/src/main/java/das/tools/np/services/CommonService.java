package das.tools.np.services;

import das.tools.np.entity.db.SimpleNumber;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public interface CommonService {
    boolean isNotEmpty(String s);

    boolean isEmpty(String s);

    Image loadImage(String resource);

    String loadResource(String resource);

    ImageView getImageView(String resource, int size);

    SimpleNumber findNumber(ObservableList<SimpleNumber> list, String number);

    SimpleNumber findNumber(ObservableList<SimpleNumber> list, SimpleNumber simpleNumber);

    String splitStringAsPhonemes(String str, int len);

    double round(double value);
}
