package das.tools.np.services;

import das.tools.np.entity.WindowPosition;
import das.tools.np.gui.enums.WindowType;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public interface ConfigService {
    Map<String,String> CONFIG_DEFAULT_VALUES = new HashMap<>();
    Map<String,String> CONFIG_SUPPORTED_LANGUAGES = new HashMap<>(2);
    String LOG_RECORDS_CACHE_NAME = "logRecords";

    String CONFIG_END_POINT_KEY = "end_point";
    String CONFIG_API_KEY_KEY = "api_key";
    String CONFIG_PHONE_NUMBER_KEY = "phone_number";
    String CONFIG_LANGUAGE_KEY = "language";
    String CONFIG_VIEW_MODE_KEY = "view_mode";
    String CONFIG_FILTER_MODE_KEY = "filtering_mode";
    String CONFIG_MAX_SEARCH_HISTORY_KEY = "max_search_history";
    String CONFIG_MAX_LOG_RECORDS_KEY = "max_log_records";
    String CONFIG_IS_FIRST_LAUNCH = "is_first_launch";
    String CONFIG_CUSTOM_NUMBERS = "custom_numbers";
    String CONFIG_NUMBERS_DIVIDER_POSITION = "numbers_divider_position"; // Doesn't save
    String CONFIG_ENABLE_AUTO_UPDATE = "enable_auto_update";
    String MAIN_WINDOW_POSITION = "main_window_position";
    String ARCHIVE_WINDOW_POSITION = "archive_window_position";
    String CUSTOM_WINDOW_POSITION = "custom_window_position";
    String SEARCH_WINDOW_POSITION = "search_window_position";
    String DETAIL_WINDOW_POSITION = "detail_window_position";
    String EDIT_PHONES_WINDOW_POSITION = "edit_phones_window_position";
    String EDIT_SEARCH_OPTIONS_WINDOW_POSITION = "edit_search_options_window_position";
    String EDIT_GROUPS_WINDOW_POSITION = "edit_groups_window_position";

    String getDefaultConfigValue(String key);

    Properties getCurrentConfig();

    Properties saveConfig(Properties props);

    void saveDefaultPhone(String phone);

    Properties saveValue(String key, String value);

    String getConfigValue(String key, String defValue);

    String getConfigValue(String key);

    ObservableList<String> getLanguagesList();

    String getLangName(String code);

    String getLangCode(String name);

    String getCurrentLanguageCode();

    int getMaxSearchHistoryRecords();

    int getMaxLogRecords();

    boolean isFirstLaunch();

    String getCustomNumbers();

    double getNumbersDividerPosition();

    boolean isAutoUpdateEnabled();

    WindowPosition getWindowPosition(WindowType windowType);

    WindowPosition getDefaultWindowPosition();

    void saveWindowPosition(Stage window, WindowType type);

    void populateWindowPosition(Stage window, WindowType type);

    WindowPosition getWindowPosition(Stage window);
}
