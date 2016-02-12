package org.als.model;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Division {

    private final StringProperty divi;
    private final StringProperty name;
    private final StringProperty inbl;

    public Division() {
        this(null);
    }

    public Division(String name) {
        if (name == null) {
            this.divi = new SimpleStringProperty("");
        } else {
            this.divi = new SimpleStringProperty(name);
        }
        this.name = new SimpleStringProperty("");
        this.inbl = new SimpleStringProperty("");
    }

    public Division(String divi, String name) {
        this.divi = new SimpleStringProperty(divi);
        this.name = new SimpleStringProperty(name);
        this.inbl = new SimpleStringProperty("");
    }

    public Division(String divi, String name, String inbl) {
        this.divi = new SimpleStringProperty(divi);
        this.name = new SimpleStringProperty(name);
        this.inbl = new SimpleStringProperty(inbl);
    }

    public String getDivi() {
        return (String) this.divi.get();
    }

    public void setDivi(String name) {
        this.divi.set(name);
    }

    public StringProperty getDiviProperty() {
        return this.divi;
    }

    public String getName() {
        return (String) this.name.get();
    }

    public void setName(String ip) {
        this.name.set(ip);
    }

    public StringProperty getNameProperty() {
        return this.name;
    }

    public String getINBL() {
        return (String) this.inbl.get();
    }

    public void setINBL(String login) {
        this.inbl.set(login);
    }

    public StringProperty getINBLProperty() {
        return this.inbl;
    }

    public ArrayList<String> extractData() {
        ArrayList<String> a = new ArrayList();
        a.add(divi.get());
        a.add(name.get());
        a.add(inbl.get());
        return a;
    }

    public void populateData(ArrayList<String> data) {
        if (data != null) {
            if (data.size() == 3) {
                divi.set(data.get(0));
                name.set(data.get(1));
                inbl.set(data.get(2));
            }
        }
    }

    @Override
    public String toString() {
        return name.get();
    }
}
