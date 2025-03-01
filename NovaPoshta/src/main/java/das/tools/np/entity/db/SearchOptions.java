package das.tools.np.entity.db;

import das.tools.np.entity.search.SearchParams;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SearchOptions {
    @Builder.Default
    private long id = 0;
    private String name;
    private SearchParams params;
    @Builder.Default
    private int orderNumber = -1;
    @Builder.Default
    private boolean changed = false;
}
