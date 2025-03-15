package das.tools.np.entity.search;

public interface SqlCondition {
    String DEFAULT_SQL_CONDITION = "1=1";
    String getSqlCondition(String fieldName);
}
