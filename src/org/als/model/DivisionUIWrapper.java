package org.als.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "divisionsUI")
public class DivisionUIWrapper {

    private List<DivisionUI> divisionsUI;

    @XmlElement(name = "divisionUI")
    public List<DivisionUI> getDivisionsUI() {
        return this.divisionsUI;
    }

    public void setDivisionsUI(List<DivisionUI> divisionsUI) {
        this.divisionsUI = divisionsUI;
    }
}
