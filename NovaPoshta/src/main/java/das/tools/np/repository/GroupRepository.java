package das.tools.np.repository;

import das.tools.np.entity.db.Group;

import java.util.List;

public interface GroupRepository {
    String TABLE_NAME = "groups";
    String FLD_ID = "id";
    String FLD_NAME = "name";
    String ALL_FIELDS =
            FLD_ID + ", " +
            FLD_NAME;

    long nextId();

    List<Group> getAll();

    Group findById(long id);

    Group getDefault();

    Group findByName(String name);

    Group add(Group group);

    Group update(Group group);

    int delete(Group group);

    boolean isGroupExists(String name);
}
