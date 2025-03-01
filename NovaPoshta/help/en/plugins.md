## Nova Poshta: Tracker and Organizer

### Plugins
Plugins are used to expand the functionality of the application. All plugins must be placed in the **plugins** folder and in its sub-folders. When loading plugins, the application scans the contents of all sub-folders of the folder **plugins** and sorts all folders by name, then the plugins contained directly in the folder **plugins** are added. In all folders, plugins are sorted by file name.

The plugin has an extension **jar** and must have the same name as the plugin's base class.
The plugin's base class must implement the **das.tools.np.entity.plugin.PluginInterface** interface:
```java
public interface PluginInterface {
    String getName();
    String getDescription();
    String getNameUK();
    String getDescriptionUK();
    void doProcess(List<CargoNumber> list);
}
```

The [NP Plugin Template](#ToDo: add link) project was created for convenience. It contains the interface and all necessary classes for work and you need to take it as a basis for creating your plugin.

Also the [NP Demo Plugin](#ToDo: add link) project exists. It contains an example plugin that simply outputs all Numbers passed to it to the console.

At the moment (v.1.*), the plugins work only for outputting information from the application.

#### [to Contents](help.md)

###### _Made by -=:dAs:=-_