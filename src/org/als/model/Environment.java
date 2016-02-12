package org.als.model;

import java.util.ArrayList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Environment {

    private final StringProperty name;
    private final StringProperty ip;
    private final IntegerProperty port;
    private final StringProperty login;
    private final StringProperty password;
    private final StringProperty pathMOM;
    private final ObjectProperty<Server> server;
    private final ObjectProperty<Service> service;

    public Environment() {
        this(null);

    }

    public Environment(String name) {
        if (name == null) {
            this.name = new SimpleStringProperty("");
        } else {
            this.name = new SimpleStringProperty(name);
        }
        this.ip = new SimpleStringProperty("");
        this.port = new SimpleIntegerProperty(6800);
        this.login = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.pathMOM = new SimpleStringProperty("");
        this.server = new SimpleObjectProperty(new Server());
        this.service = new SimpleObjectProperty(new Service());
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty getNameProperty() {
        return this.name;
    }

    public String getIP() {
        return ip.get();
    }

    public void setIP(String ip) {
        this.ip.set(ip);
    }

    public StringProperty getIpProperty() {
        return this.ip;
    }

    public int getPort() {
        return port.get();
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public IntegerProperty getPortProperty() {
        return this.port;
    }

    public String getLogin() {
        return login.get();
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public StringProperty getLoginProperty() {
        return this.login;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty getPasswordProperty() {
        return this.password;
    }

    public String getPathMOM() {
        return pathMOM.get();
    }

    public void setPathMOM(String pathMOM) {
        this.pathMOM.set(pathMOM);
    }

    public StringProperty getPathMOMProperty() {
        return this.pathMOM;
    }

    public Server getServer() {
        return server.get();
    }

    public void setServer(Server server) {
        this.server.set(server);
    }

    public ObjectProperty<Server> getServerProperty() {
        return this.server;
    }

    public Service getService() {
        return service.get();
    }

    public void setService(Service service) {
        this.service.set(service);
    }

    public ObjectProperty<Service> getServiceProperty() {
        return this.service;
    }

    public ArrayList<?> extractData() {
        ArrayList a = new ArrayList();
        a.add(name.get());
        a.add(ip.get());
        a.add(port.get());
        return a;
    }

    public void populateData(ArrayList<?> data) {
        if (data != null) {
            if (data.size() == 3) {
                name.set((String) data.get(0));
                ip.set((String) data.get(1));
                port.set((Integer) data.get(2));
            }
        }
    }

    @Override
    public String toString() {
        return name.get();
    }

}
