package org.als.view;

import fx.custom.FxUtil;
import fx.custom.InputConstraints;
import fx.custom.PasswordFieldSkin;
import fx.custom.TextFieldValidator;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.als.MainApp;
import org.als.model.Environment;
import org.als.model.Server;
import org.als.model.Service;
import org.als.task.ListWindowsService;
import org.controlsfx.dialog.Dialogs;

/**
 * Dialog to edit details of a environment.
 *
 * @author Jérémy Chaut
 */
public class EnvironmentEditDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField MOMPathField;
    @FXML
    private ComboBox<Server> serverField;
    @FXML
    private ComboBox<Service> serviceField;
    @FXML
    private ProgressIndicator progressService;
    @FXML
    private Button okButton;

    private BooleanBinding nameBoolean, hostBoolean, portBoolean, loginBoolean, passwordBoolean, pathBoolean;
    private final BooleanProperty isServer = new SimpleBooleanProperty(false);
    private final BooleanProperty isService = new SimpleBooleanProperty(false);

    protected Map<BooleanBinding, String> messages = new LinkedHashMap<>();

    private ListWindowsService windowsService;
    private Stage dialogStage;
    private Environment environment;
    private ChangeListener changeServerListener;
    private boolean okClicked = false;
    private MainApp mainApp;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        new PasswordFieldSkin(passwordField);
        InputConstraints.upperCaseAndNumbersOnly(loginField, 10);
        nameBoolean = TextFieldValidator.emptyTextFieldBinding(nameField, MainApp.resourceMessage.getString("message.nameEnvironment"), messages);
        hostBoolean = TextFieldValidator.patternTextFieldBinding(hostField, TextFieldValidator.hostnamePattern, MainApp.resourceMessage.getString("message.host"), messages);
        portBoolean = TextFieldValidator.patternTextFieldBinding(portField, TextFieldValidator.allPortNumberPattern, MainApp.resourceMessage.getString("message.port"), messages);
        loginBoolean = TextFieldValidator.emptyTextFieldBinding(loginField, MainApp.resourceMessage.getString("message.login"), messages);
        passwordBoolean = TextFieldValidator.emptyTextFieldBinding(passwordField, MainApp.resourceMessage.getString("message.password"), messages);
        pathBoolean = TextFieldValidator.patternTextFieldBinding(MOMPathField, TextFieldValidator.directoryPathPattern, MainApp.resourceMessage.getString("message.path"), messages);

        serverField.setConverter(new StringConverter<Server>() {

            @Override
            public String toString(Server object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Server fromString(String string) {
                return (serverField.getSelectionModel().getSelectedIndex() == -1) ? null : serverField.getItems().get(serverField.getSelectionModel().getSelectedIndex());
            }
        });
        FxUtil.autoCompleteComboBox(serverField, FxUtil.AutoCompleteMode.STARTS_WITH);
        serverField.valueProperty().addListener((ObservableValue<? extends Server> observable, Server oldValue, Server newValue) -> {
            isServer.set(newValue != null && !newValue.getName().isEmpty());
        });

        serviceField.setConverter(new StringConverter<Service>() {

            @Override
            public String toString(Service object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Service fromString(String string) {
                return (serviceField.getSelectionModel().getSelectedIndex() == -1) ? null : serviceField.getItems().get(serviceField.getSelectionModel().getSelectedIndex());
            }
        });
        FxUtil.autoCompleteComboBox(serviceField, FxUtil.AutoCompleteMode.STARTS_WITH);
        serviceField.valueProperty().addListener((ObservableValue<? extends Service> observable, Service oldValue, Service newValue) -> {
            if (newValue != null) {
                isService.set(!newValue.getName().isEmpty());
            } else {
                isService.set(false);
            }
        });

        BooleanBinding[] mandotariesBinding = new BooleanBinding[]{nameBoolean, hostBoolean, portBoolean, loginBoolean, passwordBoolean, pathBoolean};
        BooleanBinding mandatoryBinding = TextFieldValidator.any(mandotariesBinding);
        okButton.disableProperty().bind((isServer.and(isService).and(mandatoryBinding.not())).not());
    }

    /**
     * Sets the stage of this dialog. a
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the environment to be edited in the dialog.
     *
     * @param environment
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;

        changeServerListener = (ChangeListener<Worker.State>) (ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.err.println(windowsService.getException());
                    break;
                case CANCELLED:
                    break;
                case RUNNING:
                    serviceField.setDisable(true);
                    progressService.setVisible(true);
                    break;
                case SUCCEEDED:
                    this.environment.getServer().getListService().addAll(windowsService.getValue());
                    serviceField.setItems(windowsService.getValue());
                    serviceField.setDisable(false);
                    progressService.setVisible(false);
                    break;
            }
        };
        windowsService = new ListWindowsService(this.environment.getServer());
        windowsService.stateProperty().addListener(changeServerListener);
        nameField.setText(this.environment.getName());
        hostField.setText(this.environment.getIP());
        portField.setText(Integer.toString(this.environment.getPort()));
        loginField.setText(this.environment.getLogin());
        passwordField.setText(this.environment.getPassword());
        MOMPathField.setText(this.environment.getPathMOM());
        serverField.setValue(this.environment.getServer());
        serverField.valueProperty().addListener((ObservableValue<? extends Server> observable, Server oldValue, Server newValue) -> {
            if (newValue != null) {
                environment.setServer(newValue);
            }
        });

        serverField.valueProperty().addListener((ObservableValue<? extends Server> observable, Server oldValue, Server newValue) -> {
            if (newValue != null) {
                windowsService = new ListWindowsService(newValue);
                windowsService.stateProperty().addListener(changeServerListener);
                windowsService.restart();
            }
        });
        serviceField.setItems(this.environment.getServer().getListService());
        serviceField.setValue(this.environment.getService());
        if (this.environment.getServer().getName() == null) {
            serviceField.setDisable(true);
        }

    }

    @FXML
    private void refreshService() {
        windowsService.restart();
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            environment.setName(nameField.getText());
            environment.setIP(hostField.getText());
            environment.setPort(Integer.parseInt(portField.getText()));
            environment.setLogin(loginField.getText());
            environment.setPassword(passwordField.getText());
            environment.setPathMOM(MOMPathField.getText());
            environment.setServer(serverField.getValue());
            environment.setService(serviceField.getValue());
            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("");
        File defaultDirectory = new File("c:/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(dialogStage);
        MOMPathField.setText(selectedDirectory.getPath());
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (errorMessage.length() == 0) {
            return true;
        } else {
// Show the error message.
            Dialogs.create()
                    .title("Invalid Fields")
                    .masthead("Please correct invalid fields")
                    .message(errorMessage)
                    .showError();
            return false;
        }
    }

    public ComboBox<Server> getServerField() {
        return serverField;
    }

    public void setServerField(ComboBox<Server> serverField) {
        this.serverField = serverField;
    }
}
