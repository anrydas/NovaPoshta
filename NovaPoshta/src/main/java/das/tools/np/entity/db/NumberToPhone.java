package das.tools.np.entity.db;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NumberToPhone {
    private long id;
    private long numberId;
    private long phoneId;
    private String number;
    private String phone;
}
