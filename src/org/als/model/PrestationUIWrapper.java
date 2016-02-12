package org.als.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "prestationsUI")
public class PrestationUIWrapper {

    private List<PrestationUI> prestationUI;

    @XmlElement(name = "divisionUI")
    public List<PrestationUI> getPrestationsUI() {
        return this.prestationUI;
    }

    public void setPrestationsUI(List<PrestationUI> prestationUI) {
        this.prestationUI = prestationUI;
    }
}
