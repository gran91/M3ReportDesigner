package org.als.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.als.MainApp;
import org.als.model.Environment;
import org.als.model.Server;

/**
 *
 * @author Jeremy.CHAUT
 */
public class ManageWindowsService extends Service<Boolean> {

    protected ArrayList<String> sclaunch = new ArrayList<>();
    private final Server server;
    private final org.als.model.Service service;
    protected String errCmd = "";
    protected String outCmd = "";

    public ManageWindowsService(Environment env){
        this(env.getServer(), env.getService());
    }
    public ManageWindowsService(Server server, org.als.model.Service service) {
        this.server = server;
        this.service = service;
        sclaunch.add("cmd.exe");
        sclaunch.add("/C");
        sclaunch.add("sc");
    }

    @Override
    protected Task<Boolean> createTask() {

        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                updateMessage(String.format(MainApp.resourceMessage.getString("message.stopservice"),service.getName()));
                ArrayList<String> paramsc = new ArrayList();
                paramsc.addAll(sclaunch);
                paramsc.add("\\\\" + server.getIP());
                paramsc.add("stop");
                paramsc.add(service.getWindowsName());
                ThreadExec t = new ThreadExec(paramsc.toArray(new String[paramsc.size()]));
                t.run();
                waitThread(t);
                if (!errCmd.isEmpty()) {
                    System.err.println(errCmd);
                    return false;
                }

                updateMessage(String.format(MainApp.resourceMessage.getString("message.startservice"),service.getName()));
                paramsc = new ArrayList();
                paramsc.addAll(sclaunch);
                paramsc.add("\\\\" + server.getIP());
                paramsc.add("start");
                paramsc.add(service.getWindowsName());
                t = new ThreadExec(paramsc.toArray(new String[paramsc.size()]));
                t.run();
                waitThread(t);
                if (!errCmd.isEmpty()) {
                    System.err.println(errCmd);
                    return false;
                }
                updateMessage(String.format(MainApp.resourceMessage.getString("message.startsuccess"),service.getName()));
                return true;
            }
        };
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

    public org.als.model.Service getService() {
        return service;
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
