package das.tools.np.services.impl;

import das.tools.np.services.ConfigService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Slf4j
public class LocalizeResourcesService implements das.tools.np.services.LocalizeResourcesService {
    private final ConfigService configService;
    private final ResourceBundle.Control utf8Control;
    private ResourceBundle storedLocale;

    public LocalizeResourcesService(ConfigService configService, ResourceBundle.Control utf8Control) {
        this.configService = configService;
        this.utf8Control = utf8Control;
    }

    @Override
    public ResourceBundle getLocale() {
        return storedLocale;
    }

    @Override @PostConstruct
    public void initLocale() {
        String lang = configService.getConfigValue(ConfigService.CONFIG_LANGUAGE_KEY);
        Locale locale = new Locale(lang);
        if (log.isDebugEnabled()) log.debug("Current Language have been set to {}", lang);
        this.storedLocale = ResourceBundle.getBundle("languages.lang", locale, utf8Control);
    }

    @Override
    public String getLocalizedResource(String key) {
        return storedLocale.getString(key);
    }
}
