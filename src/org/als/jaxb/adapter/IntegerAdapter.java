package org.als.jaxb.adapter;


import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter (for JAXB) to convert between the Color and the path String
 * representation of the image
 *
 * @author Jérémy Chaut
 */
public class IntegerAdapter extends XmlAdapter<String, Integer> {

    @Override
    public Integer unmarshal(String c) throws Exception {
        return new Integer(c);
    }

    @Override
    public String marshal(Integer c) throws Exception {
        return c.toString();
    }
}
