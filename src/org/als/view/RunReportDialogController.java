package org.als.view;

import api.ConnexionAPI;
import fx.custom.DigitalClock;
import fx.custom.FxUtil;
import fx.custom.InputConstraints;
import fx.custom.TextFieldValidator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.CANCELLED;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.als.MainApp;
import org.als.model.Division;
import org.als.model.Environment;
import resources.Resource;

/**
 *
 * @author Jérémy Chaut
 */
public class RunReportDialogController {

    @FXML
    private ComboBox<Environment> environmentField;
    @FXML
    private ComboBox<Division> divisionField;
    @FXML
    private TextField ornoField;
    @FXML
    private TextField jobnField;
    @FXML
    private Button buttonOK;
    @FXML
    private ProgressIndicator progressReport;
    @FXML
    private Label message;
    @FXML
    private Button bStop;

    private final BooleanProperty isDisableOK = new SimpleBooleanProperty(true);
    private final BooleanProperty isEnvironment = new SimpleBooleanProperty(false);
    private final BooleanProperty isDivision = new SimpleBooleanProperty(false);
    private BooleanBinding ornoBoolean, jobnBoolean;

    private ConnexionAPI conAPI;
    private final ResourceBundle resourceMessage = ResourceBundle.getBundle("resources/language", Locale.getDefault());
    protected Map<BooleanBinding, String> messages = new LinkedHashMap<>();
    private long timeout = 60000;
    private String stringTime = "";
    private File fpath;
    private final DigitalClock clock = new DigitalClock(DigitalClock.CHRONOMETER);
    private final StringProperty messageGen = new SimpleStringProperty();
    private Service<String> runReportService;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        InputConstraints.numbersOnly(ornoField, 10);
        InputConstraints.numbersOnly(jobnField, 3);
        ornoBoolean = TextFieldValidator.patternTextFieldBinding(ornoField, TextFieldValidator.ornoPattern, MainApp.resourceMessage.getString("message.orno"), messages);
        jobnBoolean = TextFieldValidator.patternTextFieldBinding(jobnField, TextFieldValidator.jobnPattern, MainApp.resourceMessage.getString("message.jobn"), messages);
        environmentField.setConverter(new StringConverter<Environment>() {

            @Override
            public String toString(Environment object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Environment fromString(String string) {
                return environmentField.getItems().get(environmentField.getSelectionModel().getSelectedIndex());
            }
        });
        FxUtil.autoCompleteComboBox(environmentField, FxUtil.AutoCompleteMode.STARTS_WITH);
        environmentField.valueProperty().addListener((ObservableValue<? extends Environment> observable, Environment oldValue, Environment newValue) -> {
            isEnvironment.set(newValue != null);
        });

        divisionField.setConverter(new StringConverter<Division>() {

            @Override
            public String toString(Division object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Division fromString(String string) {
                return divisionField.getItems().get(divisionField.getSelectionModel().getSelectedIndex());
            }
        });
        FxUtil.autoCompleteComboBox(divisionField, FxUtil.AutoCompleteMode.STARTS_WITH);
        divisionField.valueProperty().addListener((ObservableValue<? extends Division> observable, Division oldValue, Division newValue) -> {
            isDivision.set(newValue != null);
        });

        BooleanBinding[] mandotariesBinding = new BooleanBinding[]{ornoBoolean, jobnBoolean};
        BooleanBinding mandatoryBinding = TextFieldValidator.any(mandotariesBinding);
        isDisableOK.bind((isDivision.and(isEnvironment).and(mandatoryBinding.not())).not());
        buttonOK.disableProperty().bind(isDisableOK);
    }

    /**
     * Sets the division to be edited in the dialog.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        environmentField.setItems(mainApp.getEnvironmentData());
        divisionField.setItems(mainApp.getDivisionData());
        timeout = mainApp.getGeneral().getTimeout();
        stringTime = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeout),
                TimeUnit.MILLISECONDS.toMinutes(timeout) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeout)),
                TimeUnit.MILLISECONDS.toSeconds(timeout) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeout)));
        messageGen.bind(Bindings.concat(MainApp.resourceMessage.getString("message.generating")).concat(" ").concat(clock.getTimeText()).concat(" Timeout:" + stringTime));
        message.textProperty().bind(messageGen);
    }

    @FXML
    private void runReport() {
        progressReport.setVisible(true);
        message.setVisible(true);
        bStop.setVisible(true);

        runReportService = new Service<String>() {

            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        String path = "";
                        String ip = environmentField.getValue().getIP();
                        int port = environmentField.getValue().getPort();
                        String user = environmentField.getValue().getLogin();
                        conAPI = new ConnexionAPI(ip, port);
                        if (conAPI.getResult() != -1) {
                            if (conAPI.verifConnection(user, "")) {
                                String in = String.format("%-15s", Resource.TRANS_CR);
                                in += String.format("%-3s", "");
                                in += String.format("%-10s", ornoField.getText());
                                in += String.format("%-3s", jobnField.getText());

                                long beginDate = System.currentTimeMillis();
                                ArrayList aOut = ConnexionAPI.parseFieldData(conAPI.listField(Resource.APINAME, Resource.TRANS_CR, 'O', user, ""));
                                ArrayList listCodeJ = conAPI.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_CR), in, user, "");
                                clock.start();
                                Object listCodeJ1 = listCodeJ.get(0);
                                if (conAPI.getRESULTAT_API().startsWith("NOK")) {
                                    throw new RuntimeException(conAPI.getRESULTAT_API());
                                } else {
                                    for (int j = 0; j < ((ArrayList) aOut.get(0)).size(); j++) {
                                        try {
                                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(j).toString()) - 1;
                                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(j).toString());
                                            path = listCodeJ1.toString().substring(begin, end);

                                        } catch (NumberFormatException ex) {
                                            throw new RuntimeException(ex.getLocalizedMessage());
                                        } catch (Exception e) {
                                            throw new RuntimeException(e.getLocalizedMessage());
                                        }
                                    }
                                    updateMessage(messageGen.getValue());
                                    fpath = new File(path);
                                    while (beginDate > fpath.lastModified()) {
                                        if ((beginDate + timeout) < System.currentTimeMillis()) {
                                            throw new RuntimeException("Timeout");
                                        }
                                        fpath = new File(path);
                                    }
                                }
                            } else {
                                throw new RuntimeException(String.format(resourceMessage.getString("api.user"), conAPI.getUser()));
                            }
                        } else {
                            throw new RuntimeException(String.format(resourceMessage.getString("api.parameter"), conAPI.getParameters()));
                        }
                        return path;
                    }
                };
            }
        };
        runReportService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    if (runReportService.getException().getMessage().equals("Timeout")) {
                        FxUtil.showAlert(Alert.AlertType.WARNING, "Timeout", "Timeout", String.format(MainApp.resourceMessage.getString("message.reportgenerate"), new String[]{fpath.getAbsolutePath().trim(), stringTime}));
                    } else if (runReportService.getException().getMessage().startsWith("NOK")) {
                        FxUtil.showAlert(Alert.AlertType.ERROR, resourceMessage.getString("api.title"), resourceMessage.getString("api.header"), runReportService.getException().getMessage().substring(15));
                    } else {
                        FxUtil.showAlert(Alert.AlertType.ERROR, resourceMessage.getString("api.title"), resourceMessage.getString("api.header"), runReportService.getException().getMessage(), conAPI.getException());
                    }
                    progressReport.setVisible(false);
                    message.setVisible(false);
                    bStop.setVisible(false);
                    clock.stop();
                    break;
                case CANCELLED:
                    progressReport.setVisible(false);
                    message.setVisible(false);
                    bStop.setVisible(false);
                    clock.stop();
                    break;
                case SUCCEEDED:
                    File f = new File(runReportService.getValue());
                    if (f.exists()) {
                        java.lang.Process p;
                        try {
                            p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + f.getAbsolutePath());
                            p.waitFor();
                        } catch (IOException | InterruptedException ex) {

                        }
                    } else {
                        System.out.println("File is not exists");
                    }
                    progressReport.setVisible(false);
                    message.setVisible(false);
                    bStop.setVisible(false);
                    clock.stop();
                    break;
            }
        });
        runReportService.restart();
    }

    @FXML
    private void stop() {
        if (runReportService != null) {
            if (runReportService.isRunning()) {
                runReportService.cancel();
            }
        }
    }
}
