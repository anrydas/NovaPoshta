package das.tools.np.services;

import java.util.ResourceBundle;

public interface LocalizeResourcesService {
    ResourceBundle getLocale();

    void initLocale();

    String getLocalizedResource(String key);
}
