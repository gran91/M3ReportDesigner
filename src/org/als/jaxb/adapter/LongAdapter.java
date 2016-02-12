package org.als.jaxb.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter (for JAXB) to convert between the Color and the path String
 * representation of the image
 *
 * @author Jérémy Chaut
 */
public class LongAdapter extends XmlAdapter<String, Long> {

    @Override
    public Long unmarshal(String c) throws Exception {
        return new Long(c);
    }

    @Override
    public String marshal(Long c) throws Exception {
        return c.toString();
    }
}
