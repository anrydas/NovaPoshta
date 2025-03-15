package das.tools.np.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class WindowPosition {
    @Builder.Default
    private int x = 0;
    @Builder.Default
    private int y = 0;
    @Builder.Default
    private int width = -1;
    @Builder.Default
    private int height = -1;
}
