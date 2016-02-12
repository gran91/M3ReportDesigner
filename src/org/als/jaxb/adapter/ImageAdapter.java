package org.als.jaxb.adapter;

import javafx.scene.image.Image;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter (for JAXB) to convert between the Image and the path String
 * representation of the image
 *
 * @author Jérémy Chaut
 */
public class ImageAdapter extends XmlAdapter<String, Image> {

    @Override
    public Image unmarshal(String v) throws Exception {
        return new Image(v);
    }

    @Override
    public String marshal(Image v) throws Exception {
        return v.impl_getUrl();
    }
}
