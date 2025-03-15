package das.tools.np.entity.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum Status {
    SELF_CREATED(1), REMOVED(2), NOT_FOUND(3), IN_CITY(4), IN_CITY_1(41),
    SENDING_TO_CITY(5), IN_CITY_ADD(6), IN_WAREHOUSE(7), IN_POST_MATE(8),
    COMPLETED(9),
    MONEY_WILL_SENT(10), MONEY_WAS_SENT(11), COMPLETING(12),
    ON_THE_WAY_TO_RECEIVER(101), SENDER_CANCELED(102), RECEIVER_REJECTED(103),
    ADDRESS_CHANGED(104), STORAGE_STOPPING(105), BACK_DELIVERY(106), DELIVERY_FAILED(111),
    RECEIVER_CHANGE_DATE(112);

    private final int value;
    public static final Map<Status,String> STATUS_NAMES_MAP = new HashMap<>();
    static {
        STATUS_NAMES_MAP.put(Status.SELF_CREATED, "Відправник самостійно створив цю накладну, але ще не надав до відправки");
        STATUS_NAMES_MAP.put(Status.REMOVED, "Видалено");
        STATUS_NAMES_MAP.put(Status.NOT_FOUND, "Номер не знайдено");
        STATUS_NAMES_MAP.put(Status.IN_CITY, "Відправлення у місті ХХXХ");
        STATUS_NAMES_MAP.put(Status.IN_CITY_1,	"Відправлення у місті ХХXХ");
        STATUS_NAMES_MAP.put(Status.SENDING_TO_CITY, "Відправлення прямує до міста YYYY");
        STATUS_NAMES_MAP.put(Status.IN_CITY_ADD, "Відправлення у місті YYYY, орієнтовна доставка до ВІДДІЛЕННЯ-XXX dd-mm. Очікуйте додаткове повідомлення про прибуття");
        STATUS_NAMES_MAP.put(Status.IN_WAREHOUSE, "Прибув на відділення");
        STATUS_NAMES_MAP.put(Status.IN_POST_MATE, "Прибув на відділення (завантажено в Поштомат)");
        STATUS_NAMES_MAP.put(Status.COMPLETED, "Відправлення отримано");
        STATUS_NAMES_MAP.put(Status.MONEY_WILL_SENT,	"Відправлення отримано %DateReceived%. Протягом доби ви одержите SMS-повідомлення про надходження грошового переказу та зможете отримати його в касі відділення «Нова пошта»");
        STATUS_NAMES_MAP.put(Status.MONEY_WAS_SENT,	"Відправлення отримано %DateReceived%. Грошовий переказ видано одержувачу.");
        STATUS_NAMES_MAP.put(Status.COMPLETING,	"Нова Пошта комплектує ваше відправлення");
        STATUS_NAMES_MAP.put(Status.ON_THE_WAY_TO_RECEIVER, "На шляху до одержувача");
        STATUS_NAMES_MAP.put(Status.SENDER_CANCELED, "Відмова від отримання (Відправником створено замовлення на повернення)");
        STATUS_NAMES_MAP.put(Status.RECEIVER_REJECTED, "Відмова одержувача (отримувач відмовився від відправлення)");
        STATUS_NAMES_MAP.put(Status.ADDRESS_CHANGED, "Змінено адресу");
        STATUS_NAMES_MAP.put(Status.STORAGE_STOPPING, "Припинено зберігання");
        STATUS_NAMES_MAP.put(Status.BACK_DELIVERY, "Одержано і створено ЄН зворотньої доставки");
        STATUS_NAMES_MAP.put(Status.DELIVERY_FAILED, "Невдала спроба доставки через відсутність Одержувача на адресі або зв'язку з ним");
        STATUS_NAMES_MAP.put(Status.RECEIVER_CHANGE_DATE, "Дата доставки перенесена Одержувачем");
    }
    Status(int value) {this.value = value;}

    public static Status valueOf(int value) {
        for (Status status : values()) {
            if (status.value == value) return status;
        }
        return null;
    }
    public static Status fromString(String s) {
        for (Map.Entry<Status,String> entry: STATUS_NAMES_MAP.entrySet()) {
            if (entry.getValue().equals(s)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static String stringValue(Status c) {
        return STATUS_NAMES_MAP.get(c);
    }

    public static boolean contains(int intValue) {
        return valueOf(intValue) != null;
    }

    public static boolean containsString(String s) {
        return STATUS_NAMES_MAP.containsValue(s);
    }

    public int getValue() {
        return this.value;
    }

    public static Set<Status> getAllKeys() {
        return STATUS_NAMES_MAP.keySet();
    }

    public static Collection<String> getAllValues() {
        return STATUS_NAMES_MAP.values();
    }
}
