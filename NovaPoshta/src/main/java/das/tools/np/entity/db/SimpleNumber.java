package das.tools.np.entity.db;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SimpleNumber {
    @Builder.Default
    private long id = -1;
    private String number;
    @Builder.Default
    private CargoStatus appStatus = CargoStatus.NEW;
    @Builder.Default
    private NumberType numberType = NumberType.UNDEF;
    private String description;
    private String comment;
    private int status;
    private Date createDate; //NP
    private Date created; //DB
    private Date updated;
}
