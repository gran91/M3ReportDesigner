package api;

import MvxAPI.MvxSockJ;
import java.util.ArrayList;

/**
 *
 * @author chautj
 */
public class ConnexionAPI {

    private String str = "";
    private String RESULTAT_API = "";
    private MvxSockJ sock;
    private int result;
    private Exception exception;
    private String ip;
    private int port;
    private String user, password;

    public ConnexionAPI(String ip, int port) {
        this.ip = ip;
        this.port = port;
        Connexion();
    }

    private void Connexion() {
        try {
            result = 0;
            sock = new MvxSockJ(ip, port, "MNS150MI", 0, "");
            sock.DEBUG = true;
        } catch (Exception ex) {
            result = -1;
            exception = ex;
        }
    }

    public boolean verifConnection(String log, String pass) {
        try {
            Connexion();
            user = log;
            password = pass;
            result = sock.mvxInit("", log, pass, "MNS150MI");
            if ((str = sock.mvxTrans("GetUserData    " + log)).startsWith("OK")) {
                return true;
            }
        } catch (Exception e) {
            exception = e;
            return false;
        }
        return false;
    }

    public ArrayList listAPI(String log, String pass) {
        try {
            Connexion();
            result = sock.mvxInit("", log, pass, "MRS001MI");
        } catch (Exception ex) {
            exception = ex;
        }
        return getAPI();
    }

    public ArrayList listTransaction(String api, String log, String pass) {
        ArrayList aAPI = new ArrayList();
        ArrayList all = new ArrayList();
        ArrayList aTip = new ArrayList();
        ArrayList aDesc = new ArrayList();
        try {
            Connexion();
            result = sock.mvxInit("", log, pass, "MRS001MI");
            sock.mvxTrans("SetLstMaxRec   0");
            aAPI.add("");
            aDesc.add("");
            aTip.add("");
            while (!(str = sock.mvxTrans("LstTransactions" + api)).startsWith("OK")) {
                if (str.trim().startsWith("NOK")) {
                    return null;
                }
                aAPI.add(str.substring(25, 40).trim());
                aDesc.add(str.substring(40, 100).trim());
                aTip.add("<html><font color=#4286B9><b></b></font>"
                        + str.substring(25, 40).trim() + "<br></b></font>"
                        + str.substring(40, 100).trim() + "<br></b></font></html>");
            }
            all.add(aAPI);
            all.add(aDesc);
            all.add(aTip);
        } catch (Exception ex) {
            exception = ex;
        }
        return all;
    }

    public ArrayList listField(String api, String trns, char type, String log, String pass) {
        ArrayList aField = new ArrayList();
        try {
            Connexion();
            api = String.format("%-10s", api);
            trns = String.format("%-15s", trns);
            result = sock.mvxInit("", log, pass, "MRS001MI");
            sock.mvxTrans("SetLstMaxRec   0");
            while (!(str = sock.mvxTrans("LstFields      " + api + trns + type)).trim().equals("OK")) {
                if (str.trim().startsWith("NOK")) {
                    return null;
                } else {
                    aField.add(str);
                }
            }
        } catch (Exception ex) {
            exception = ex;
        }
        return aField;
    }

    public ArrayList getAPI() {
        ArrayList aAPI = new ArrayList();
        ArrayList all = new ArrayList();
        ArrayList aTip = new ArrayList();
        ArrayList aDesc = new ArrayList();
        try {
            sock.mvxTrans("SetLstMaxRec   0");
            aAPI.add("");
            aDesc.add("");
            aTip.add("");
            while (!(str = sock.mvxTrans("LstPrograms    ")).trim().equals("OK")) {
                if (str.trim().startsWith("NOK")) {
                    return null;
                }
                aAPI.add(str.substring(15, 25).trim());
                aDesc.add(str.substring(55, 115).trim());
                aTip.add("<html><font color=#4286B9><b></b></font>"
                        + str.substring(15, 25).trim() + "<br></b></font>"
                        + str.substring(25, 55).trim() + "<br></b></font>"
                        + str.substring(55, 115).trim() + "<br></b></font></html>");
            }
            all.add(aAPI);
            all.add(aDesc);
            all.add(aTip);

        } catch (Exception ex) {
            exception = ex;
        }
        return all;
    }

    public ArrayList executeAPI(String api, String trans, String in, String log, String pass) {
        ArrayList a = new ArrayList();
        try {
            Connexion();
            result = sock.mvxInit("", log, pass, api);  // Initialisation de la transaction API
            sock.mvxTrans("SetLstMaxRec   0");
            while (!(str = sock.mvxTrans(in)).trim().startsWith("OK") && !a.contains(str)) {
                a.add(str);
                if (str.startsWith("NOK")) {
                    break;
                }
                System.out.println("NB ENREG=" + a.size());
            }
            if (str != null) {
                RESULTAT_API = str;
                if (RESULTAT_API.startsWith("NOK")) {
                    System.out.println("KO:" + str.substring(15).trim());
                }

                if (RESULTAT_API.startsWith("OK")) {
                    if (!a.contains(str) && !str.substring(2).trim().isEmpty()) {
                        a.add(str);
                    }
                    System.out.println("OK:" + str);
                } // end if
            } else {
                System.err.println("Problème sur exécution de l'API MRS001MI");
            }
        } catch (Exception ex) {
            exception = ex;
        }
        return a;
    }

    public static ArrayList parseFieldData(ArrayList a) {
        ArrayList all = new ArrayList();
        ArrayList aName = new ArrayList();
        ArrayList aDesc = new ArrayList();
        ArrayList aBegin = new ArrayList();
        ArrayList aEnd = new ArrayList();
        ArrayList aTaille = new ArrayList();
        ArrayList aType = new ArrayList();
        ArrayList aMandatory = new ArrayList();
        for (int i = 0; i < a.size(); i++) {
            String s = a.get(i).toString();
            aName.add(s.substring(41, 51).trim());
            aDesc.add(s.substring(51, 100).trim());
            aBegin.add(s.substring(124, 127).trim());
            aEnd.add(s.substring(127, 130).trim());
            aTaille.add(s.substring(130, 133).trim());
            aType.add(s.substring(133, 134));
            aMandatory.add(s.substring(134, 135));
        }
        all.add(aName);
        all.add(aDesc);
        all.add(aBegin);
        all.add(aEnd);
        all.add(aTaille);
        all.add(aType);
        all.add(aMandatory);
        return all;
    }

    public String getRESULTAT_API() {
        return RESULTAT_API;
    }

    public int getResult() {
        return result;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Exception getException() {
        return exception;
    }
    
    public String getParameters(){
        return "Host:"+ip+"\n"+
                "Port:"+port+"\n"+
                "Login:"+user+"\n"+
                "Password:"+password+"\n";
                
    }
}
