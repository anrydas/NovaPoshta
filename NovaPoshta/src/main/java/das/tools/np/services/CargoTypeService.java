package das.tools.np.services;

import das.tools.np.entity.db.NumberType;
import das.tools.np.gui.Localized;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CargoTypeService extends Localized {
    Map<NumberType, String> TYPE_ICONS = new HashMap<>();
    Map<NumberType, FontAwesome.Glyph> TYPE_GLYPHS = new HashMap<>();
    Map<NumberType, Color> TYPE_COLORS = new HashMap<>();
    Map<NumberType, String> TYPE_NAMES = new HashMap<>();

    String getTypeName(NumberType type);

    List<NumberType> getTypes();

    NumberType getTypeByName(String name);

    String getTypeImage(NumberType type);

    FontAwesome.Glyph getTypeGlyph(NumberType type);

    org.controlsfx.glyphfont.Glyph getTypeGlyphColored(NumberType type, int size);

    Color getTypeColor(NumberType type);
}
