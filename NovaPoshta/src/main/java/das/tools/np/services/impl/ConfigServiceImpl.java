package das.tools.np.services.impl;

import das.tools.np.entity.WindowPosition;
import das.tools.np.entity.db.ExtraPhone;
import das.tools.np.entity.db.Property;
import das.tools.np.gui.enums.WindowType;
import das.tools.np.repository.ExtraPhonesRepository;
import das.tools.np.repository.PropertiesRepository;
import das.tools.np.services.CommonService;
import das.tools.np.services.ConfigService;
import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ConfigServiceImpl implements ConfigService {
    private final PropertiesRepository propertiesRepository;
    private final ExtraPhonesRepository phonesRepository;
    private final ConversionService conversionService;
    private final CommonService commonService;

    static {
        Map<String,String> map = CONFIG_DEFAULT_VALUES;
        map.put(CONFIG_END_POINT_KEY, "https://api.novaposhta.ua/v2.0/json/");
        map.put(CONFIG_PHONE_NUMBER_KEY, "");
        map.put(CONFIG_LANGUAGE_KEY, "en");
        map.put(CONFIG_VIEW_MODE_KEY, "VM_NUMBERS");
        map.put(CONFIG_FILTER_MODE_KEY, "ALL");
        map.put(CONFIG_MAX_SEARCH_HISTORY_KEY, "30");
        map.put(CONFIG_MAX_LOG_RECORDS_KEY, "500");
        map.put(CONFIG_IS_FIRST_LAUNCH, "true");
        map.put(CONFIG_CUSTOM_NUMBERS, "");
        map.put(CONFIG_NUMBERS_DIVIDER_POSITION, "0.36");
        map.put(CONFIG_ENABLE_AUTO_UPDATE, "true");

        map = CONFIG_SUPPORTED_LANGUAGES;
        map.put("en", "English");
        map.put("uk", "Ukrainian");
    }

    @PostConstruct
    public void postConstruct() {
        String position = conversionService.convert(WindowPosition.builder().build(), String.class);
        CONFIG_DEFAULT_VALUES.put(MAIN_WINDOW_POSITION, position);
        CONFIG_DEFAULT_VALUES.put(ARCHIVE_WINDOW_POSITION, position);
        CONFIG_DEFAULT_VALUES.put(CUSTOM_WINDOW_POSITION, position);
        CONFIG_DEFAULT_VALUES.put(SEARCH_WINDOW_POSITION, position);
        CONFIG_DEFAULT_VALUES.put(DETAIL_WINDOW_POSITION, position);
        CONFIG_DEFAULT_VALUES.put(EDIT_PHONES_WINDOW_POSITION, position);
        CONFIG_DEFAULT_VALUES.put(EDIT_SEARCH_OPTIONS_WINDOW_POSITION, position);
        CONFIG_DEFAULT_VALUES.put(EDIT_GROUPS_WINDOW_POSITION, position);
    }

    public ConfigServiceImpl(PropertiesRepository propertiesRepository, ExtraPhonesRepository phonesRepository, ConversionService conversionService, CommonService commonService) {
        this.propertiesRepository = propertiesRepository;
        this.phonesRepository = phonesRepository;
        this.conversionService = conversionService;
        this.commonService = commonService;
    }

    @Override
    public String getDefaultConfigValue(String key) {
        return CONFIG_DEFAULT_VALUES.get(key);
    }

    @Override
    public Properties getCurrentConfig() {
        List<Property> props = propertiesRepository.getAll();
        Property defaultPhone = Property.builder()
                .key(CONFIG_PHONE_NUMBER_KEY)
                .value((phonesRepository.isDefaultExists() ? phonesRepository.getDefault().getPhone() : ""))
                .build();
        props.add(defaultPhone);
        return listToProperties(props);
    }

    @Override
    public Properties saveConfig(Properties props) {
        String phone = props.getProperty(CONFIG_PHONE_NUMBER_KEY);
        props.remove(CONFIG_PHONE_NUMBER_KEY);
        List<Property> list = propertiesRepository.updateAll(props);
        ExtraPhone extraPhone = phonesRepository.setDefault(phone);
        Property defaultPhone = Property.builder()
                .key(CONFIG_PHONE_NUMBER_KEY)
                .value(extraPhone.getPhone())
                .build();
        list.add(defaultPhone);
        return listToProperties(list);
    }

    @Override
    public void saveDefaultPhone(String phone) {
        saveValue(CONFIG_PHONE_NUMBER_KEY, phone);
    }

    @Override
    public Properties saveValue(String key, String value) {
        List<Property> props;
        if (CONFIG_PHONE_NUMBER_KEY.equals(key)) {
            Property defaultPhone = Property.builder()
                    .key(CONFIG_PHONE_NUMBER_KEY)
                    .value(phonesRepository.setDefault(value).getPhone())
                    .build();
            props = new ArrayList<>(1);
            props.add(defaultPhone);
        } else {
            if (propertiesRepository.isPropertyExists(key)) {
                props = propertiesRepository.update(key, value);
            } else {
                props = new ArrayList<>(1);
                props.add(propertiesRepository.add(key, value));
            }
        }
        return listToProperties(props);
    }

    @Override
    public String getConfigValue(String key, String defValue) {
        return getCurrentConfig().getProperty(key, defValue);
    }

    @Override
    public String getConfigValue(String key) {
        return getCurrentConfig().getProperty(key, getDefaultConfigValue(key));
    }

    @Override
    public ObservableList<String> getLanguagesList() {
        String[] langs = ConfigService.CONFIG_SUPPORTED_LANGUAGES.values().toArray(new String[0]);
        Arrays.sort(langs);
        return FXCollections.observableArrayList(langs);
    }

    @Override
    public String getLangName(String code) {
        return CONFIG_SUPPORTED_LANGUAGES.getOrDefault(code, CONFIG_SUPPORTED_LANGUAGES.get(getDefaultConfigValue(CONFIG_LANGUAGE_KEY)));
    }

    @Override
    public String getLangCode(String name) {
        for (Map.Entry<String,String> entry : CONFIG_SUPPORTED_LANGUAGES.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return CONFIG_SUPPORTED_LANGUAGES.get(getDefaultConfigValue(CONFIG_LANGUAGE_KEY));
    }

    @Override
    public String getCurrentLanguageCode() {
        return getCurrentConfig().getProperty(CONFIG_LANGUAGE_KEY, getDefaultConfigValue(CONFIG_LANGUAGE_KEY));
    }

    @Override
    public int getMaxSearchHistoryRecords() {
        String value = getConfigValue(CONFIG_MAX_SEARCH_HISTORY_KEY);
        return Integer.parseInt(value);
    }

    @Override @Cacheable(value = {LOG_RECORDS_CACHE_NAME})
    public int getMaxLogRecords() {
        String value = getConfigValue(CONFIG_MAX_LOG_RECORDS_KEY);
        return Integer.parseInt(value);
    }

    @CacheEvict(value = {LOG_RECORDS_CACHE_NAME}, allEntries = true)
    @Scheduled(fixedDelay = 60 * 60 * 1000,  initialDelay = 60 * 60 * 1000)
    public void emptyMaxLogRecordsCache() {
        log.info("Reset '{}' cache", LOG_RECORDS_CACHE_NAME);
    }

    @Override
    public boolean isFirstLaunch() {
        String value = getConfigValue(CONFIG_IS_FIRST_LAUNCH);
        return Boolean.parseBoolean(value);
    }

    @Override
    public String getCustomNumbers() {
        return getConfigValue(CONFIG_CUSTOM_NUMBERS);
    }

    @Override
    public double getNumbersDividerPosition() {
        return Double.parseDouble(getDefaultConfigValue(CONFIG_NUMBERS_DIVIDER_POSITION));
    }

    @Override
    public boolean isAutoUpdateEnabled() {
        String value = getConfigValue(CONFIG_ENABLE_AUTO_UPDATE, getDefaultConfigValue(CONFIG_ENABLE_AUTO_UPDATE));
        return Boolean.parseBoolean(value);
    }

    @Override
    public WindowPosition getWindowPosition(WindowType type) {
        String configValue;
        switch (type) {
            case MAIN -> configValue = getConfigValue(MAIN_WINDOW_POSITION, getDefaultConfigValue(MAIN_WINDOW_POSITION));
            case ARCHIVE -> configValue = getConfigValue(ARCHIVE_WINDOW_POSITION, getDefaultConfigValue(ARCHIVE_WINDOW_POSITION));
            case CUSTOM_VIEW -> configValue = getConfigValue(CUSTOM_WINDOW_POSITION, getDefaultConfigValue(CUSTOM_WINDOW_POSITION));
            case SEARCH -> configValue = getConfigValue(SEARCH_WINDOW_POSITION, getDefaultConfigValue(SEARCH_WINDOW_POSITION));
            case DETAIL -> configValue = getConfigValue(DETAIL_WINDOW_POSITION, getDefaultConfigValue(DETAIL_WINDOW_POSITION));
            case EDIT_PHONES -> configValue = getConfigValue(EDIT_PHONES_WINDOW_POSITION, getDefaultConfigValue(EDIT_PHONES_WINDOW_POSITION));
            case EDIT_SEARCH_OPTIONS -> configValue = getConfigValue(EDIT_SEARCH_OPTIONS_WINDOW_POSITION, getDefaultConfigValue(EDIT_SEARCH_OPTIONS_WINDOW_POSITION));
            case EDIT_GROUPS -> configValue = getConfigValue(EDIT_GROUPS_WINDOW_POSITION, getDefaultConfigValue(EDIT_GROUPS_WINDOW_POSITION));
            default -> configValue = conversionService.convert(getDefaultWindowPosition(), String.class);
        }
        return conversionService.convert(configValue, WindowPosition.class);
    }

    @Override
    public WindowPosition getDefaultWindowPosition() {
        return WindowPosition.builder().build();
    }

    @Override
    public void saveWindowPosition(Stage window, WindowType type) {
        String key = null;
        switch (type) {
            case MAIN -> key = MAIN_WINDOW_POSITION;
            case ARCHIVE -> key = ARCHIVE_WINDOW_POSITION;
            case CUSTOM_VIEW -> key = CUSTOM_WINDOW_POSITION;
            case SEARCH -> key = SEARCH_WINDOW_POSITION;
            case DETAIL -> key = DETAIL_WINDOW_POSITION;
            case EDIT_PHONES -> key = EDIT_PHONES_WINDOW_POSITION;
            case EDIT_SEARCH_OPTIONS -> key = EDIT_SEARCH_OPTIONS_WINDOW_POSITION;
            case EDIT_GROUPS -> key = EDIT_GROUPS_WINDOW_POSITION;
        }
        if (commonService.isNotEmpty(key)) {
            saveValue(key, conversionService.convert(getWindowPosition(window), String.class));
        }
    }

    @Override
    public void populateWindowPosition(Stage window, WindowType type) {
        WindowPosition position = getWindowPosition(type);
        if (!position.equals(getDefaultWindowPosition())) {
            window.setX(position.getX());
            window.setY(position.getY());
            if (position.getWidth() >= window.getMinWidth()) {
                window.setWidth(position.getWidth());
            }
            if (position.getHeight() >= window.getMinHeight()) {
                window.setHeight(position.getHeight());
            }
        }
    }

    @Override
    public WindowPosition getWindowPosition(Stage window) {
        return WindowPosition.builder()
                .x((int) window.getX())
                .y((int) window.getY())
                .height((int) window.getHeight())
                .width((int) window.getWidth())
                .build();
    }

    private Properties listToProperties(List<Property> list) {
        Properties res = new Properties();
        for (Property p : list) {
            res.setProperty(p.getKey(), p.getValue());
        }
        if (log.isDebugEnabled()) log.debug("got Properties={}", res);
        return res;
    }
}
