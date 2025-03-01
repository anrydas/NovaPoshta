package das.tools.np.entity.plugin;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PluginInfo {
    private String absolutePath;
    private String pluginSubdirectory;
    private String name;
    private String description;
    private String className;
}
