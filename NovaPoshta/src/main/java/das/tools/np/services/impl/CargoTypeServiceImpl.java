package das.tools.np.services.impl;

import das.tools.np.entity.db.NumberType;
import das.tools.np.gui.Localized;
import das.tools.np.services.CargoTypeService;
import das.tools.np.services.LocalizeResourcesService;
import jakarta.annotation.PostConstruct;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CargoTypeServiceImpl implements CargoTypeService, Localized {
    private final LocalizeResourcesService localizeService;
    private final GlyphFont glyphFont;

    static {
        TYPE_ICONS.put(NumberType.IN, "/images/number/types/in.png");
        TYPE_ICONS.put(NumberType.OUT, "/images/number/types/out.png");
        TYPE_ICONS.put(NumberType.UNDEF, "/images/number/types/undef.png");

        TYPE_GLYPHS.put(NumberType.IN, FontAwesome.Glyph.SIGN_IN);
        TYPE_GLYPHS.put(NumberType.OUT, FontAwesome.Glyph.SIGN_OUT);
        TYPE_GLYPHS.put(NumberType.UNDEF, FontAwesome.Glyph.QUESTION_CIRCLE);

        TYPE_COLORS.put(NumberType.IN, Color.GREEN);
        TYPE_COLORS.put(NumberType.OUT, Color.DARKRED);
        TYPE_COLORS.put(NumberType.UNDEF, Color.BLUE);
    }

    public CargoTypeServiceImpl(LocalizeResourcesService localizeService, GlyphFont glyphFont) {
        this.localizeService = localizeService;
        this.glyphFont = glyphFont;
    }

    @Override @PostConstruct
    public void initLocale() {
        TYPE_NAMES.put(NumberType.IN, localizeService.getLocalizedResource("number.type.in"));
        TYPE_NAMES.put(NumberType.OUT, localizeService.getLocalizedResource("number.type.out"));
        TYPE_NAMES.put(NumberType.UNDEF, localizeService.getLocalizedResource("number.type.undef"));
    }

    @Override
    public String getTypeName(NumberType type) {
        if (TYPE_NAMES.containsKey(type)) {
            return TYPE_NAMES.get(type);
        } else {
            throw new RuntimeException("Wrong cargo type: " + type.name());
        }
    }

    @Override
    public List<NumberType> getTypes() {
        return new ArrayList<>(TYPE_NAMES.keySet());
    }

    @Override
    public NumberType getTypeByName(String name) {
        if (TYPE_NAMES.containsValue(name)) {
            for (Map.Entry<NumberType,String> entry : TYPE_NAMES.entrySet()) {
                if (entry.getValue().equals(name)) {
                    return entry.getKey();
                }
            }
        } else {
            throw new RuntimeException("Wrong cargo type name: " + name);
        }
        return null;
    }

    @Override
    public String getTypeImage(NumberType type) {
        if (TYPE_ICONS.containsKey(type)) {
            return TYPE_ICONS.get(type);
        } else {
            throw new RuntimeException("Wrong cargo type: " + type.name());
        }
    }

    @Override
    public FontAwesome.Glyph getTypeGlyph(NumberType type) {
        if (TYPE_GLYPHS.containsKey(type)) {
            return TYPE_GLYPHS.get(type);
        } else {
            throw new RuntimeException("Wrong cargo type: " + type.name());
        }
    }

    @Override
    public org.controlsfx.glyphfont.Glyph getTypeGlyphColored(NumberType type, int size) {
        if (TYPE_GLYPHS.containsKey(type) && TYPE_COLORS.containsKey(type)) {
            return glyphFont.create(TYPE_GLYPHS.get(type)).color(TYPE_COLORS.get(type)).size(size);
        } else {
            throw new RuntimeException("Wrong cargo type: " + type.name());
        }
    }

    @Override
    public Color getTypeColor(NumberType type) {
        if (TYPE_COLORS.containsKey(type)) {
            return TYPE_COLORS.get(type);
        } else {
            throw new RuntimeException("Wrong cargo type: " + type.name());
        }
    }
}
