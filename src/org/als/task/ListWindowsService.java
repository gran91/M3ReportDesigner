package org.als.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.als.model.Server;
import static org.als.task.WindowsService.waitThread;

/**
 *
 * @author Jeremy.CHAUT
 */
public class ListWindowsService extends Service<ObservableList<org.als.model.Service>> {

    private final Server server;
    private final ObservableList<org.als.model.Service> listService;
    protected String errCmd = "";
    protected String outCmd = "";
    private static final String DISPLAYNAME = "getdisplayname";
    private static final String KEYNAME = "getkeyname";

    public ListWindowsService(Server server) {
        this.server = server;
        listService = FXCollections.observableArrayList();
    }

    @Override
    protected Task<ObservableList<org.als.model.Service>> createTask() {

        return new Task<ObservableList<org.als.model.Service>>() {
            @Override
            protected ObservableList<org.als.model.Service> call() throws Exception {
                ArrayList<String> paramconnect = new ArrayList();
                paramconnect.add("cmd.exe");
                paramconnect.add("/C");
                paramconnect.add("net");
                paramconnect.add("use");
                paramconnect.add("\\\\" + server.getIP());
                paramconnect.add("/USER:" + server.getLogin());
                paramconnect.add(server.getPassword());

                ThreadExec t = new ThreadExec(paramconnect.toArray(new String[paramconnect.size()]));
                t.run();
                waitThread(t);
                if (!errCmd.isEmpty()) {
                    System.err.println(errCmd);
                    System.out.println(outCmd);
                }

                listService.clear();
                listService(server, "StreamServe[0-9]^");
                listService(server, "MOM_");
//                ArrayList<String> paramsc = new ArrayList();
//                paramsc.add("cmd.exe");
//                paramsc.add("/C");
//                paramsc.add("sc");
//                paramsc.add("\\\\" + server.getIP());
//                paramsc.add("query");
//                paramsc.add("|");
//                paramsc.add("findstr");
//                paramsc.add("/r");
//                paramsc.add("StreamServe[0-9]^");
//                ThreadExec t1 = new ThreadExec(paramsc.toArray(new String[paramsc.size()]));
//                t1.run();
//                waitThread(t1);
//
//                if (!outCmd.isEmpty()) {
//                    String[] tabline = outCmd.split("\n");
//                    for (String tabline1 : tabline) {
//                        String[] tabService = tabline1.split(" ");
//                        ArrayList<String> par = new ArrayList();
//                        par.add("cmd.exe");
//                        par.add("/C");
//                        par.add("sc");
//                        par.add("\\\\" + server.getIP());
//                        par.add("getdisplayname");
//                        par.add(tabService[1]);
//                        par.add("|");
//                        par.add("findstr");
//                        par.add("/r");
//                        par.add("^Name = ");
//                        ThreadExec t2 = new ThreadExec(par.toArray(new String[par.size()]));
//                        t2.run();
//                        waitThread(t2);
//                        if (!outCmd.isEmpty()) {
//                            listService.add(new org.als.model.Service(tabService[1], outCmd.split("=")[1].trim()));
//                        }
//                    }
//                }
                return listService;
            }
        };
    }

    private void listService(Server server, String service) {
        ArrayList<String> paramsc = new ArrayList();
        paramsc.add("cmd.exe");
        paramsc.add("/C");
        paramsc.add("sc");
        paramsc.add("\\\\" + server.getIP());
        paramsc.add("query");
        paramsc.add("|");
        paramsc.add("findstr");
        paramsc.add("/r");
        paramsc.add(service);
        ThreadExec t1 = new ThreadExec(paramsc.toArray(new String[paramsc.size()]));
        t1.run();
        waitThread(t1);

        if (!outCmd.isEmpty()) {
            String[] tabline = outCmd.split("\n");
            for (String tabline1 : tabline) {
                String[] tabService = tabline1.split(" ");
                if (tabService[0].startsWith("DISPLAY")) {
                    BindingServiceDisplay(server, KEYNAME, tabService[1]);
                } else {
//                    ArrayList<String> par = new ArrayList();
//                    par.add("cmd.exe");
//                    par.add("/C");
//                    par.add("sc");
//                    par.add("\\\\" + server.getIP());
//                    par.add("getdisplayname");
//                    par.add(tabService[1]);
//                    par.add("|");
//                    par.add("findstr");
//                    par.add("/r");
//                    par.add("^Name = ");
//                    ThreadExec t2 = new ThreadExec(par.toArray(new String[par.size()]));
//                    t2.run();
//                    waitThread(t2);
//                    if (!outCmd.isEmpty()) {
//                        listService.add(new Service(outCmd.split("=")[1].trim(), tabService[1]));
//                    }
                    BindingServiceDisplay(server, DISPLAYNAME,  tabService[1]);
                }
            }
        }
    }

    private void BindingServiceDisplay(Server server, String type, String value) {
        ArrayList<String> par = new ArrayList();
        par.add("cmd.exe");
        par.add("/C");
        par.add("sc");
        par.add("\\\\" + server.getIP());
        par.add(type);
        par.add(value);
        par.add("|");
        par.add("findstr");
        par.add("/r");
        par.add("^Name = ");
        ThreadExec t2 = new ThreadExec(par.toArray(new String[par.size()]));
        t2.run();
        waitThread(t2);
        if (!outCmd.isEmpty()) {
            if (type.equals(DISPLAYNAME)) {
                listService.add(new org.als.model.Service(value, outCmd.split("=")[1].trim()));
            } else {
                listService.add(new org.als.model.Service(outCmd.split("=")[1].trim(), value));
            }
        }
    }

    public String getErrCmd() {
        return errCmd;
    }

    public String getOutCmd() {
        return outCmd;
    }

    public Server getServer() {
        return server;
    }

    public ObservableList<org.als.model.Service> getListService() {
        return listService;
    }

    public class ThreadExec implements Runnable {

        private Process process;
        private final String[] cmd;
        private boolean isActive = false;

        public ThreadExec(String[] p) {
            cmd = p;
        }

        private BufferedReader getOutputReader() {
            return new BufferedReader(new InputStreamReader(process.getInputStream()));
        }

        private BufferedReader getErrorReader() {
            return new BufferedReader(new InputStreamReader(process.getErrorStream()));
        }

        private void getError() throws IOException {
            String line = "";
            errCmd = "";
            BufferedReader error = getErrorReader();
            try {
                while ((line = error.readLine()) != null) {
                    errCmd += line + "\n";
                }
            } finally {
                error.close();
            }
        }

        private void getOutput() throws IOException {
            String line = "";
            outCmd = "";
            BufferedReader out = getOutputReader();
            try {
                while ((line = out.readLine()) != null) {
                    outCmd += line + "\n";
                }
            } finally {
                out.close();
            }
        }

        @Override
        public void run() {
            try {
                isActive = true;
                process = Runtime.getRuntime().exec(cmd);
                getError();
                getOutput();
                process.waitFor();
                isActive = false;
            } catch (IOException ex) {

            } catch (InterruptedException ex) {

            }
        }

        public boolean isActive() {
            return isActive;
        }

    }

    public static void waitThread(ThreadExec t) {
        while (t.isActive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {

            }
        }
    }

}
