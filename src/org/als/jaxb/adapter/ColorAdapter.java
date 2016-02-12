package org.als.jaxb.adapter;

import javafx.scene.paint.Color;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import tools.ColorFontTools;

/**
 * Adapter (for JAXB) to convert between the Color and the path String
 * representation of the image
 *
 * @author Jérémy Chaut
 */
public class ColorAdapter extends XmlAdapter<String, Color> {

    @Override
    public Color unmarshal(String c) throws Exception {
        return ColorFontTools.getColor(c);
    }

    @Override
    public String marshal(Color c) throws Exception {
        return ColorFontTools.getRGB(c);
    }
}
