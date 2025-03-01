package das.tools.np.entity.db;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SearchResult {
    private long id;
    private String number;
    private String descr;
    private String cityFrom;
    private String all;
}
