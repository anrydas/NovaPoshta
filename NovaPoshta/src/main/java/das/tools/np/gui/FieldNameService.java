package das.tools.np.gui;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.services.CommonService;
import das.tools.np.services.LocalizeResourcesService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Slf4j
public class FieldNameService implements Localized {
    public static final Map<String,String> ALL_FIELDS = new HashMap<>();
    public static final List<String> SEARCHABLE_FIELDS = new ArrayList<>();
    public static final Set<String> GROUPED_FIELD_NAMES = new HashSet<>(2);
    public static final Map<String,String> GROUPED_FIELDS = new HashMap<>();
    private final LocalizeResourcesService localizeService;
    private final CommonService commonService;

    public FieldNameService(LocalizeResourcesService localizeService, CommonService commonService) {
        this.localizeService = localizeService;
        this.commonService = commonService;
    }

    @PostConstruct
    private void postConstruct() {
        initLocale();
        GROUPED_FIELD_NAMES.add("group");
        GROUPED_FIELD_NAMES.add("fullData");
        List<String> list = SEARCHABLE_FIELDS;
        list.add("number");
        list.add("comment");
        list.add("description");
        list.add("recipientFullName");
        list.add("cityRecipient");
        list.add("warehouseRecipient");
        list.add("warehouseRecipientNumber");
        list.add("phoneRecipient");
        list.add("recipientAddress");
        list.add("citySender");
        list.add("phoneSender");
        list.add("warehouseSender");
        list.add("senderAddress");
    }

    @Override
    public void initLocale() {
        for (Field field: CargoNumber.class.getDeclaredFields()) {
            String fieldName = field.getName();
            String localizedName = localizeService.getLocalizedResource(String.format("number.field.%s", fieldName));
            ALL_FIELDS.put(fieldName, localizedName);
            if (isGroupedField(fieldName)) {
                GROUPED_FIELDS.put(fieldName, localizedName);
            }
        }
    }

    public List<String> getAllFieldNames() {
        ArrayList<String> list = new ArrayList<>(ALL_FIELDS.keySet());
        list.trimToSize();
        return list;
    }

    public List<String> getAllFieldFullNames() {
        ArrayList<String> list = new ArrayList<>(ALL_FIELDS.values());
        list.trimToSize();
        return list;
    }

    public List<String> getSearchableFieldNames() {
        return SEARCHABLE_FIELDS;
    }

    public List<String> getSearchableFieldFullNames() {
        ArrayList<String> list = new ArrayList<>(SEARCHABLE_FIELDS.size());
        for (String field: SEARCHABLE_FIELDS) {
            list.add(ALL_FIELDS.get(field));
        }
        list.trimToSize();
        return list;
    }

    public String getFieldFullName(String name) {
        String fullName = ALL_FIELDS.get(name);
        return commonService.isNotEmpty(fullName) ? fullName : name;
    }

    public String getFieldName(String fullName) {
        for (Map.Entry<String,String> entry: ALL_FIELDS.entrySet()) {
            if (entry.getValue().equals(fullName)) return entry.getKey();
        }
        return "";
    }

    public boolean isGroupedField(String name) {
        return GROUPED_FIELD_NAMES.contains(name);
    }
}
