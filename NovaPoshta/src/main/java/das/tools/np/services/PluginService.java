package das.tools.np.services;


import das.tools.np.entity.db.CargoNumber;
import das.tools.np.entity.plugin.PluginInfo;

import java.util.List;

public interface PluginService {
    void loadPlugins();

    List<PluginInfo> getAllPlugins();

    void launchPlugin(List<CargoNumber> numbers, String fileName);
}
