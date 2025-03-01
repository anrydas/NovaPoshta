package das.tools.np.repository;

import das.tools.np.entity.db.SearchOptions;

import java.util.List;

public interface SearchOptionsRepository {
    String TABLE_NAME = "search_params";
    String FLD_ID = "id";
    String FLD_NAME = "name";
    String FLD_OPTIONS = "options";
    String FLD_ORDER_NUMBER = "order_number";
    String FLD_UPDATED = "updated";
    String FLD_CREATED = "created";
    String ALL_FIELDS =
            FLD_ID + ", " +
            FLD_NAME + ", " +
            FLD_ORDER_NUMBER + ", " +
            FLD_OPTIONS;
    String ALL_FIELDS_ADD =
            FLD_ID + ", " +
            FLD_NAME + ", " +
            FLD_ORDER_NUMBER + ", " +
            FLD_OPTIONS;

    long nextId();

    int nextOrderNumber();

    List<das.tools.np.entity.db.SearchOptions> getAll();

    das.tools.np.entity.db.SearchOptions findById(long id);

    das.tools.np.entity.db.SearchOptions findByName(String name);

    boolean isNameAlreadyExists(String name);

    SearchOptions add(SearchOptions options);

    SearchOptions update(SearchOptions options);

    void remove(SearchOptions options);
}
