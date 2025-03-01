package das.tools.np.services.impl;

import das.tools.np.entity.db.SimpleNumber;
import das.tools.np.services.CommonService;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {
    @Override
    public boolean isNotEmpty(String s) {
        return s != null && !"".equals(s);
    }

    @Override
    public boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    @Override
    public Image loadImage(String resource) {
        return new Image(Objects.requireNonNull(CommonServiceImpl.class.getResourceAsStream(resource)));
    }

    @Override
    public String loadResource(String resource) {
        return Objects.requireNonNull(getClass().getResource(resource)).toExternalForm();
    }

    @Override
    public ImageView getImageView(String resource, int size) {
        Image im = loadImage(resource);
        ImageView iv = new ImageView(im);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        return iv;
    }

    @Override
    public SimpleNumber findNumber(ObservableList<SimpleNumber> list, String number) {
        if (isNotEmpty(number)) {
            for (SimpleNumber sn : list) {
                if (number.equals(sn.getNumber())) return sn;
            }
        }
        return null;
    }

    @Override
    public SimpleNumber findNumber(ObservableList<SimpleNumber> list, SimpleNumber simpleNumber) {
        return findNumber(list, simpleNumber.getNumber());
    }

    @Override
    public String splitStringAsPhonemes(String str, int len) {
        if (str.length() > len) {
            String[] words = str.split("(?=[\\s.])");
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (words.length > i) {
                StringBuilder line = new StringBuilder();
                while (words.length > i && line.length() + words[i].length() < len) {
                    line.append(words[i]);
                    i++;
                }
                line.append("\n");
                sb.append(line);
            }
            return sb.toString();
        } else {
            return str;
        }
    }

    @Override
    public double round(double value) {
        long factor = 100;
        value = value * factor;
        return (double) Math.round(value) / factor;
    }
}
