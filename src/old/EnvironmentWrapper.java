 package old;
 
import org.als.model.*;
 import java.util.List;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @XmlRootElement(name="environments")
 public class EnvironmentWrapper
 {
   private List<Environment> environments;
 
   @XmlElement(name="environment")
   public List<Environment> getEnvironments()
   {
     return this.environments;
   }
 
   public void setEnvironments(List<Environment> persons) {
     this.environments = persons;
   }
 }
