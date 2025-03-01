package das.tools.np.entity.db;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SearchHistory {
    @Builder.Default
    private long id = 0;
    private String searchText;
}
