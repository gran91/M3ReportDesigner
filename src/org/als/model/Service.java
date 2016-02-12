package org.als.model;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Service {

    private final StringProperty name;
    private final StringProperty windowsName;

    public Service() {
        this(null, null);
    }

    public Service(String window, String name) {
        this.windowsName = new SimpleStringProperty((window==null)?"":window);
        this.name = new SimpleStringProperty((name==null)?"":name);
    }

    public String getName() {
        return (String) this.name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty getNameProperty() {
        return this.name;
    }

    public String getWindowsName() {
        return (String) this.windowsName.get();
    }

    public void setWindowsName(String ip) {
        this.windowsName.set(ip);
    }

    public StringProperty getWindowsNameProperty() {
        return this.windowsName;
    }

    public ArrayList<String> extractData() {
        ArrayList<String> a = new ArrayList();
        a.add(windowsName.get());
        a.add(name.get());
        return a;
    }

    public void populateData(ArrayList<String> data) {
        if (data != null) {
            if (data.size() == 2) {
                windowsName.set(data.get(0));
                name.set(data.get(1));
            }
        }
    }

    @Override
    public String toString() {
        return name.get();
    }
}
