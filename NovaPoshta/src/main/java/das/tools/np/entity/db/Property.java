package das.tools.np.entity.db;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Property {
    private String key;
    private String value;
}
