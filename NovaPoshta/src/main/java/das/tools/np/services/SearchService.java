package das.tools.np.services;

import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.search.SearchParams;

import java.util.List;

public interface SearchService {
    String TABLE_NAME = "numbers";
    String FLD_ID = "id";
    String FLD_NUMBER = "number";
    String FLD_DESCR = "descr";
    String FLD_CITY_FROM = "city_from";
    String FLD_ALL_IN_ONE = "all_in_one";
    String ALL_FIELDS =
            FLD_ID + ", " +
            FLD_NUMBER + ", " +
            FLD_DESCR + ", " +
            FLD_CITY_FROM;
    String SIMPLE_ALL_FIELDS =
            FLD_ID + ", " +
            FLD_NUMBER + ", " +
            "null as " + FLD_DESCR + ", " +
            "null as " + FLD_CITY_FROM + ", " +
            "null as " + FLD_ALL_IN_ONE;
    String ALL_SEARCHABLE_FIELDS =
            FLD_DESCR + ", " +
            FLD_CITY_FROM;

    List<CargoNumber> search(String simpleValue);

    List<CargoNumber> searchArchive(String simpleValue);

    List<CargoNumber> search(SearchParams params);

    List<CargoNumber> searchArchive(SearchParams params);
}
