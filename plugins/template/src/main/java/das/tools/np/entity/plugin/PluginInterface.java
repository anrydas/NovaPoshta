package das.tools.np.entity.plugin;

import das.tools.np.entity.db.CargoNumber;

import java.util.List;

public interface PluginInterface {
    String getName();
    String getDescription();
    String getNameUK();
    String getDescriptionUK();
    void doProcess(List<CargoNumber> list);
}
