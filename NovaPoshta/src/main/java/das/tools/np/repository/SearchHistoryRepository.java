package das.tools.np.repository;

import das.tools.np.entity.db.SearchHistory;

import java.util.List;

public interface SearchHistoryRepository {
    String TABLE_NAME = "search_history";
    String FLD_ID = "id";
    String FLD_SEARCH_TEXT = "search_text";
    String FLD_CREATED = "created";
    String ALL_FIELDS =
            FLD_ID + ", " +
            FLD_SEARCH_TEXT + ", " +
            FLD_CREATED;
    String ALL_FIELDS_ADD =
            FLD_ID + ", " +
            FLD_SEARCH_TEXT;

    long nextId();

    SearchHistory findById(long id);

    SearchHistory findByText(String searchText);

    List<SearchHistory> getAll();

    String[] getAllAsStrings();

    boolean isRecordExists(String value);

    SearchHistory add(String searchText);

    void removeOlds();
}
