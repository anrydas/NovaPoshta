package das.tools.np.repository;

import das.tools.np.entity.db.Property;

import java.util.List;
import java.util.Properties;

public interface PropertiesRepository {
    String TABLE_NAME = "properties";
    String FLD_ID = "id";
    String FLD_KEY = "prop_key";
    String FLD_VALUE = "prop_value";
    String ALL_FIELDS = FLD_ID + ", " +
            FLD_KEY + ", " +
            FLD_VALUE;

    long nextId();

    List<Property> getAll();

    Property findById(long id);

    String getPhone();

    List<Property> update(String key, String value);

    Property add(String key, String value);

    boolean isPropertyExists(String key);

    List<Property> updateAll(Properties props);
}
