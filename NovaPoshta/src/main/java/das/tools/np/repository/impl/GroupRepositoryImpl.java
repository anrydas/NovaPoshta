package das.tools.np.repository.impl;

import das.tools.np.entity.db.Group;
import das.tools.np.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
public class GroupRepositoryImpl implements GroupRepository {
    private static final String SQL_NEW_ID = "select coalesce(max(seq), 0) + 1 from sqlite_sequence WHERE name = '" + TABLE_NAME + "'";
    private static final String SQL_GET_ALL = "select " + ALL_FIELDS + " from " + TABLE_NAME + " order by " + FLD_NAME;
    private static final String SQL_GET_BY_ID = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_GET_BY_NAME = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_NAME + " = ?";
    private static final String SQL_ADD = "insert into " + TABLE_NAME + "(" + ALL_FIELDS + ")" + " values (?,?)";
    private static final String SQL_UPDATE = "update " + TABLE_NAME + " set " + FLD_NAME + " = ? " + "where " + FLD_ID + " = ?";
    private static final String SQL_DELETE = "delete from " + TABLE_NAME + " where " + FLD_ID + " = ?";
    private static final String SQL_COUNT_IN_GROUP = "select count(1) as cnt from numbers where " + FLD_ID + " = ?";
    private static final String SQL_GROUPS_COUNT = "select count(1) as cnt from " + TABLE_NAME + " where " + FLD_NAME + " = ?";
    private static final String SQL_GET_DEFAULT = "select " + ALL_FIELDS + " from " + TABLE_NAME + " where " + FLD_ID + " = 1";

    private final JdbcTemplate jdbcTemplate;

    public GroupRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long nextId() {
        Long id = jdbcTemplate.queryForObject(SQL_NEW_ID, Long.class);
        if (id == null) {
            throw new RuntimeException("Couldn't get new ID");
        }
        if (log.isDebugEnabled()) log.debug("got new id={}", id);
        return id;
    }

    @Override
    public List<Group> getAll() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_ALL);
        List<Group> groups = jdbcTemplate.query(SQL_GET_ALL, new GroupRowMapper<Group>());
        if (log.isDebugEnabled()) log.debug("got groups={}", groups);
        return groups;
    }

    @Override
    public Group findById(long id) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, id={}", SQL_GET_BY_ID, id);
        Group group = jdbcTemplate.queryForObject(SQL_GET_BY_ID, new GroupRowMapper<Group>(), id);
        if (log.isDebugEnabled()) log.debug("got group={}", group);
        return group;
    }

    @Override
    public Group getDefault() {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}", SQL_GET_DEFAULT);
        Group group = jdbcTemplate.queryForObject(SQL_GET_DEFAULT, new GroupRowMapper<Group>());
        if (log.isDebugEnabled()) log.debug("got default group={}", group);
        return group;
    }

    @Override
    public Group findByName(String name) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, name={}", SQL_GET_BY_NAME, name);
        Group group = jdbcTemplate.queryForObject(SQL_GET_BY_NAME, new GroupRowMapper<Group>(), name);
        if (log.isDebugEnabled()) log.debug("got group={}", group);
        return group;
    }

    @Override
    public Group add(Group group) {
        long id = nextId();
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, group={}, id={}", SQL_ADD, group, id);
        jdbcTemplate.update(SQL_ADD,
                id,
                group.getName()
        );
        return findById(id);
    }

    @Override
    public Group update(Group group) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, group={}", SQL_UPDATE, group);
        jdbcTemplate.update(SQL_UPDATE,
                group.getName(),
                group.getId()
        );
        return findById(group.getId());
    }

    @Override
    public int delete(Group group) {
        if (getMembersCountInGroup(group) > 0) {
            log.error("Couldn't delete non empty group {}", group);
            return 0;
        }
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, group={}", SQL_DELETE, group);
        int rows = jdbcTemplate.update(SQL_DELETE, group.getId());
        if (log.isDebugEnabled()) log.debug("removed {} rows", rows);
        return rows;
    }

    @Override
    public boolean isGroupExists(String name) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={}, id={}", SQL_GROUPS_COUNT, name);
        Integer res = jdbcTemplate.queryForObject(SQL_GROUPS_COUNT, Integer.class, name);
        return (res != null ? res : 0) > 0;
    }

    private int getMembersCountInGroup(Group group) {
        if (log.isDebugEnabled()) log.debug("Executing SQL={} for group={}", SQL_COUNT_IN_GROUP, group);
        Integer res = jdbcTemplate.queryForObject(SQL_COUNT_IN_GROUP, Integer.class, group.getId());
        return (res != null ? res : 0);
    }

    private static final class GroupRowMapper<T> implements RowMapper<Group> {

        @Override
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Group.builder()
                    .id(rs.getLong(FLD_ID))
                    .name(rs.getString(FLD_NAME))
                    .build();
        }
    }
}
