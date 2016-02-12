package org.als.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "generals")
public class GeneralWrapper {

    private List<General> generals;

    @XmlElement(name = "general")
    public List<General> getGenerals() {
        return this.generals;
    }

    public void setGenerals(List<General> generals) {
        this.generals = generals;
    }
}
