package org.als.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "divisions")
public class DivisionWrapper {

    private List<Division> divisions;

    @XmlElement(name = "division")
    public List<Division> getDivisions() {
        return this.divisions;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
    }
}
