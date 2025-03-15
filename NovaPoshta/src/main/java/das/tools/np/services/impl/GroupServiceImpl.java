package das.tools.np.services.impl;

import das.tools.np.entity.db.Group;
import das.tools.np.gui.dialog.AlertService;
import das.tools.np.repository.CargoNumberRepository;
import das.tools.np.repository.GroupRepository;
import das.tools.np.services.GroupService;
import das.tools.np.services.LocalizeResourcesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final CargoNumberRepository numberRepository;
    private final AlertService alertService;
    private final LocalizeResourcesService localizeService;

    public GroupServiceImpl(GroupRepository groupRepository, CargoNumberRepository numberRepository, AlertService alertService, LocalizeResourcesService localizeService) {
        this.groupRepository = groupRepository;
        this.numberRepository = numberRepository;
        this.alertService = alertService;
        this.localizeService = localizeService;
    }

    @Override
    public List<Group> getAll() {
        return groupRepository.getAll();
    }

    @Override
    public Group findById(long id) {
        return groupRepository.findById(id);
    }

    @Override
    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public Group add(Group group) {
        if (!groupRepository.isGroupExists(group.getName())) {
            return groupRepository.add(group);
        } else {
            return groupRepository.findByName(group.getName());
        }
    }

    @Override
    public Group update(Group group) {
        return groupRepository.update(group);
    }

    @Override
    public int delete(Group group) {
        if (numberRepository.numbersInGroup(group.getId()) > 0) {
            log.error("Couldn't delete non empty group '{}'", group.getName());
            return 0;
        }
        return groupRepository.delete(group);
    }

    @Override
    public boolean isGroupExists(String name) {
        return groupRepository.isGroupExists(name);
    }
}
