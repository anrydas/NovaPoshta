package das.tools.np.entity.db;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Group {
    private long id;
    private String name;
}
