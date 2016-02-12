package org.als.model;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Server {

    private final StringProperty name;
    private final StringProperty ip;
    private final StringProperty login;
    private final StringProperty password;
    private final ObservableList<Service> listService;

    public Server() {
        this(null);
    }

    public Server(String name) {
        if (name == null) {
            this.name = new SimpleStringProperty("");
        } else {
            this.name = new SimpleStringProperty(name);
        }
        this.ip = new SimpleStringProperty("");
        this.login = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        listService = FXCollections.observableArrayList();
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

    public String getIP() {
        return (String) this.ip.get();
    }

    public void setIP(String ip) {
        this.ip.set(ip);
    }

    public StringProperty getIPProperty() {
        return this.ip;
    }

    public String getLogin() {
        return (String) this.login.get();
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public StringProperty getLoginProperty() {
        return this.login;
    }

    public String getPassword() {
        return (String) this.password.get();
    }

    public void setPassword(String pass) {
        this.password.set(pass);
    }

    public StringProperty getPasswordProperty() {
        return this.password;
    }

    public ObservableList<Service> getListService() {
        return listService;
    }

    public ArrayList<String> extractData() {
        ArrayList<String> a = new ArrayList();
        a.add(name.get());
        a.add(ip.get());
        a.add(login.get());
        a.add(password.get());
        return a;
    }

    public void populateData(ArrayList<String> data) {
        if (data != null) {
            if (data.size() == 4) {
                name.set(data.get(0));
                ip.set(data.get(1));
                login.set(data.get(2));
                password.set(data.get(3));
            }
        }
    }

    @Override
    public String toString() {
        return name.get();
    }
}
