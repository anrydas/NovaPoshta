package das.tools.np.services.impl;

import das.tools.np.entity.search.SearchParams;
import das.tools.np.entity.search.SqlCondition;
import das.tools.np.gui.controllers.search.options.SearchCondition;
import das.tools.np.repository.ArchiveRepository;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.repository.GroupRepository;
import das.tools.np.services.SearchQueryProducer;
import das.tools.np.services.SearchService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SearchQueryProducerImpl implements SearchQueryProducer {
    protected static final String PRODUCED_FIELDS_MACRO = "#ALL_PRODUCED_FIELDS#";
    protected static final String CONDITIONS_MACRO = "#CONDITIONS#";
    protected static final String SQL_GET_EXT_RESULTS_TEMPLATE = "select " + PRODUCED_FIELDS_MACRO + "\n" +
            "from " + SearchService.TABLE_NAME + " t\n" +
            "where 1=1 " + CONDITIONS_MACRO;
    protected static final String SQL_GET_EXT_RESULTS_ARCHIVE_TEMPLATE = "select " + PRODUCED_FIELDS_MACRO + "\n" +
            "from " + ArchiveRepository.TABLE_NAME + " t\n" +
            "where 1=1 " + CONDITIONS_MACRO;
    protected static final String GROUP_CONDITION_TEMPLATE = "(select %s from %s where %s = %s) %s '%s'";
    protected static final String ALL_FIELDS_CONDITIONS_TEMPLATE = "(%s\n  %s)";
    protected static final String ALL_GROUPS_CONDITIONS_TEMPLATE = "(%s\n  or %s)";

    @Override
    public String getQuery(SearchParams params) {
        String sql;
        List<String> fields = params.getFields();
        sql = SQL_GET_EXT_RESULTS_TEMPLATE
                .replace(PRODUCED_FIELDS_MACRO, getProducedFields(fields))
                .replace(CONDITIONS_MACRO, getConditions(params));
        return sql;
    }
    @Override
    public String getArchiveQuery(SearchParams params) {
        String sql;
        List<String> fields = params.getFields();
        sql = SQL_GET_EXT_RESULTS_ARCHIVE_TEMPLATE
                .replace(PRODUCED_FIELDS_MACRO, getProducedFields(fields))
                .replace(CONDITIONS_MACRO, getConditions(params));
        return sql;
    }

    private String getProducedFields(List<String> fields) {
        String all_in_one = ",\n" + ((fields.size() > 0) ? "(" + getAllInOneFieldsExpression(fields) + ")" : "''");
        return SearchService.FLD_ID + "," + SearchService.FLD_NUMBER + ",\n" +
                String.format("(coalesce(%s, %s)) as %s,\n",
                        CargoNumberRepository.FLD_COMMENT, CargoNumberRepository.FLD_DESCR, SearchService.FLD_DESCR) +
                String.format("(trim(replace(substr(%s, 1, instr(%s, ',')-1), 'м.', ''))) as %s",
                        CargoNumberRepository.FLD_ADDR_SENDER, CargoNumberRepository.FLD_ADDR_SENDER, SearchService.FLD_CITY_FROM) +
                all_in_one + " as " + SearchService.FLD_ALL_IN_ONE;
    }

    private String getAllInOneFieldsExpression(List<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            sb.append(String.format("coalesce(%s,'')", fields.get(i)));
            if (i < fields.size() -1) {
                sb.append("||' '||");
            }
        }
        return sb.toString();
    }

    private String getConditions(SearchParams params) {
        String allFieldsConditions = String.format(ALL_FIELDS_CONDITIONS_TEMPLATE,
                getSearchableFieldsCondition(params),
                getProducedFieldsCondition(params));
        String allGroupConditions = String.format(ALL_GROUPS_CONDITIONS_TEMPLATE,
                getGroupValueCondition(params),
                getGroupTextCondition(params));
        return "\nand " + allFieldsConditions +
                "\nand " + allGroupConditions +
                "\nand " + params.getNumberType().getSqlCondition(CargoNumberRepository.FLD_NUMBER_TYPE) + " /* Number Type condition */" +
                "\nand " + params.getAppStatus().getSqlCondition(CargoNumberRepository.FLD_APP_STATUS) + " /* App Status condition */" +
                "\nand " + params.getStatus().getSqlCondition(CargoNumberRepository.FLD_STATUS) + " /* Status condition */" +
                "\nand " + params.getCreate().getSqlCondition(CargoNumberRepository.FLD_CREATED) + " /* Create date condition */" +
                "\nand " + params.getSchedule().getSqlCondition(CargoNumberRepository.FLD_DELIVERY_SCHED_DATE) + " /* Scheduled date condition */" +
                "\nand " + params.getWeight().getSqlCondition(CargoNumberRepository.FLD_WEIGHT) + " /* Weight condition */" +
                "\nand " + params.getCost().getSqlCondition(CargoNumberRepository.FLD_COST) + " /* Cost condition */" +
                "\nand " + params.getPrice().getSqlCondition(CargoNumberRepository.FLD_ANNOUNCED_PRICE) + " /* Price condition */" +
                "\nand " + params.getSeats().getSqlCondition(CargoNumberRepository.FLD_SEATS) + " /* Seats condition */";
    }

    private String getSearchableFieldsCondition(SearchParams params) {
        String searchText = params.getSearchText();
        return String.join(" like '%" + searchText + "%' or ", Arrays.asList(SearchService.ALL_SEARCHABLE_FIELDS.split(", "))) +
                " like '%" + searchText + "%'" + " or " + SearchService.FLD_CITY_FROM + " = ''" + " /* Searchable Fields Condition */";
    }

    private String getProducedFieldsCondition(SearchParams params) {
        List<String> fields = params.getFields();
        String searchText = params.getSearchText();
        return (
                (fields.size() > 0) ? "or " +
                        String.join(" like '%" + searchText + "%' or ", fields) + " like '%" + searchText + "%'"
                        : ""
        ) + " /* Produced Fields Condition */";
    }

    private String getGroupValueCondition(SearchParams params) {
        return (
                params.getGroup().isSelected() ? String.format(GROUP_CONDITION_TEMPLATE,
                        GroupRepository.FLD_NAME, GroupRepository.TABLE_NAME,
                        GroupRepository.FLD_ID, CargoNumberRepository.FLD_GROUP,
                        SearchCondition.stringValue(params.getGroup().getCondition()),
                        params.getGroup().getValue()
                ) : SqlCondition.DEFAULT_SQL_CONDITION
        ) + " /* Group condition */";
    }

    private String getGroupTextCondition(SearchParams params) {
        return (
                params.getGroupText().isSelected() ? String.format(GROUP_CONDITION_TEMPLATE,
                        GroupRepository.FLD_NAME, GroupRepository.TABLE_NAME,
                        GroupRepository.FLD_ID, CargoNumberRepository.FLD_GROUP,
                        (!params.getGroupText().getCondition().equals(SearchCondition.CONTAINS)) ?
                                SearchCondition.stringValue(params.getGroupText().getCondition()) : "like",
                        (!params.getGroupText().getCondition().equals(SearchCondition.CONTAINS)) ?
                                params.getGroupText().getValue() : "%"+params.getGroupText().getValue()+"%"
                ) : SqlCondition.DEFAULT_SQL_CONDITION
        ) + " /* Group Text condition */";
    }
}
