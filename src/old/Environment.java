package old;

import org.als.model.*;
import java.util.ArrayList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Environment extends Model implements IModel,Cloneable{

    private final StringProperty name;
    private final StringProperty ip;
    private final IntegerProperty port;
    private final StringProperty login;
    private final StringProperty password;
    private final StringProperty pathMOM;
    private final ObjectProperty<Service> server;
    private final StringProperty service;
    
    public Environment() {
        this(null);
        nameModel="Environment";
        i18nID="environment.title";

    }

    public Environment(String name) {
        nameModel="Environment";
        i18nID="environment.title";
        this.name = new SimpleStringProperty(name);
        this.ip = new SimpleStringProperty("");
        this.port = new SimpleIntegerProperty(6800);
        this.login = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.pathMOM = new SimpleStringProperty("");
        this.server = new SimpleObjectProperty(new Service());
        this.service = new SimpleStringProperty("");
    }

    public StringProperty getName() {
        return this.name;
    }

    public StringProperty getIp() {
        return this.ip;
    }

    public IntegerProperty getPort() {
        return this.port;
    }

    public StringProperty getLogin() {
        return this.login;
    }

    public StringProperty getMdp() {
        return this.password;
    }

    public StringProperty getPathMOM() {
        return this.pathMOM;
    }

    public ObjectProperty<Service> getServer() {
        return this.server;
    }

    public StringProperty getService() {
        return this.service;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setIp(String ip) {
        this.ip.set(ip);
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public void setMdp(String mdp) {
        this.password.set(mdp);
    }

    public void setPathMOM(String path) {
        this.pathMOM.set(path);
    }

    public void setServer(Service serv) {
        this.server.set(serv);
    }

    public void setService(String service) {
        this.service.set(service);
    }
    
    @Override
    public ArrayList<?> getData() {
        ArrayList a=new ArrayList();
        a.add(name.get());
        a.add(ip.get());
        a.add(port.get());
        return a;
    }
    
    @Override
    public void setData(ArrayList<?> data){
        if(data!=null){
            if(data.size()==3){
                name.set((String)data.get(0));
                ip.set((String)data.get(1));
                port.set((Integer)data.get(2));
            }
        }
    }
}
