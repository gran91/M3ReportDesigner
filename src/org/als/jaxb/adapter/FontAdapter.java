package org.als.jaxb.adapter;

import javafx.scene.text.Font;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter (for JAXB) to convert between the Color and the path String
 * representation of the image
 *
 * @author Jérémy Chaut
 */
public class FontAdapter extends XmlAdapter<String, Font> {

    @Override
    public Font unmarshal(String c) throws Exception {
        return new Font(c, 12);
    }

    @Override
    public String marshal(Font c) throws Exception {
        return c.getName();
    }
}
