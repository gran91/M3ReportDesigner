package old;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.als.model.Environment;

import org.controlsfx.dialog.Dialogs;

/**
 * Dialog to edit details of a environment.
 *
 * @author Jérémy Chaut
 */
public class EnvironmentEditDialogController extends DefaultEditDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;

    Environment environment;

    @FXML
    private void initialize() {
    }

    @Override
    public void setUIDataToModel() {
        environment = new Environment();
        environment.setName(nameField.getText());
        environment.setIP(hostField.getText());
        environment.setPort(Integer.parseInt(portField.getText()));
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    @Override
    protected boolean isInputValid() {
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
