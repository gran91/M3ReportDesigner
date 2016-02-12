 package org.als.model;
 
 import java.util.List;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @XmlRootElement(name="servers")
 public class ServerWrapper
 {
   private List<Server> servers;
 
   @XmlElement(name="server")
   public List<Server> getServers()
   {
       return this.servers;
   }
 
   public void setServers(List<Server> servers) {
    this.servers = servers;
   }
 }