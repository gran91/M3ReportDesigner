package org.als.view;

import fx.custom.InputConstraints;
import fx.custom.PasswordFieldSkin;
import fx.custom.TextFieldValidator;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.als.MainApp;
import org.als.model.Server;

import org.controlsfx.dialog.Dialogs;

/**
 * Dialog to edit details of a server.
 *
 * @author Jérémy Chaut
 */
public class ServerEditDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField ipField;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button okButton;

    private BooleanBinding nameBoolean, ipBoolean, loginBoolean, passwordBoolean;
    protected Map<BooleanBinding, String> messages = new LinkedHashMap<>();

    private Stage dialogStage;
    private Server server;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        new PasswordFieldSkin(passwordField);
        nameBoolean = TextFieldValidator.emptyTextFieldBinding(nameField, MainApp.resourceMessage.getString("message.nameServer"), messages);
        ipBoolean = TextFieldValidator.emptyTextFieldBinding(ipField, MainApp.resourceMessage.getString("message.ip"), messages);
        loginBoolean = TextFieldValidator.emptyTextFieldBinding(loginField, MainApp.resourceMessage.getString("message.login"), messages);
        passwordBoolean = TextFieldValidator.emptyTextFieldBinding(passwordField, MainApp.resourceMessage.getString("message.password"), messages);
        BooleanBinding[] mandotariesBinding = new BooleanBinding[]{nameBoolean, ipBoolean, loginBoolean, passwordBoolean};
        BooleanBinding mandatoryBinding = TextFieldValidator.any(mandotariesBinding);
        okButton.disableProperty().bind(mandatoryBinding);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the division to be edited in the dialog.
     *
     * @param server
     */
    public void setServer(Server server) {
        this.server = server;
        nameField.setText(server.getName());
        ipField.setText(server.getIP());
        loginField.setText(server.getLogin());
        passwordField.setText(server.getPassword());
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
            server.setName(nameField.getText());
            server.setIP(ipField.getText());
            server.setLogin(loginField.getText());
            server.setPassword(passwordField.getText());
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
}
