package org.als.model;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Prestation {

    private final StringProperty code;
    private final StringProperty name;

    public Prestation() {
        this(null);
    }

    public Prestation(String name) {
        this.code = new SimpleStringProperty(name);
        this.name = new SimpleStringProperty("");
    }

    public Prestation(String divi, String name) {
        this.code = new SimpleStringProperty(divi);
        this.name = new SimpleStringProperty(name);
    }


    public String getCode() {
        return (String) this.code.get();
    }

    public void setCode(String name) {
        this.code.set(name);
    }

    public StringProperty getCodeProperty() {
        return this.code;
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

    public ArrayList<String> extractData() {
        ArrayList<String> a=new ArrayList();
        a.add(code.get());
        a.add(name.get());
        return a;
    }
    
    public void populateData(ArrayList<String> data){
        if(data!=null){
            if(data.size()==3){
                code.set(data.get(0));
                name.set(data.get(1));
            }
        }
    }
    
    @Override
    public String toString() {
        return code.get();
    }
}
