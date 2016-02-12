package org.als.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "prestations")
public class PrestationWrapper {

    private List<Prestation> prestations;

    @XmlElement(name = "prestation")
    public List<Prestation> getPrestations() {
        return this.prestations;
    }

    public void setPrestations(List<Prestation> prestations) {
        this.prestations = prestations;
    }
}
