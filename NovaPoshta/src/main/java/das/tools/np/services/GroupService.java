package das.tools.np.services;

import das.tools.np.entity.db.Group;

import java.util.List;

public interface GroupService {
    List<Group> getAll();

    Group findById(long id);

    Group findByName(String name);

    Group add(Group group);

    Group update(Group group);

    int delete(Group group);

    boolean isGroupExists(String name);
}
