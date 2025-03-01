package das.tools.np.entity.db;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ExtraPhone {
    @Builder.Default
    private long id = 0;
    private String phone;
    @Builder.Default
    private int orderNumber = -1;
}
