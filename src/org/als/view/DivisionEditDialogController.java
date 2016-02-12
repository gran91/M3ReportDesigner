package org.als.view;

import fx.custom.InputConstraints;
import fx.custom.TextFieldValidator;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.als.MainApp;
import org.als.model.Division;

import org.controlsfx.dialog.Dialogs;

/* Dialog to edit details of a division.
 *
 * @author Jérémy 

 Chaut
 */
public class DivisionEditDialogController {

    @FXML
    private TextField diviField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField inblField;
    @FXML
    private Button okButton;

    private Stage dialogStage;
    private Division division;
    private boolean okClicked = false;
    private BooleanBinding diviBoolean, nameBoolean, inblBoolean;

    protected Map<BooleanBinding, String> messages = new LinkedHashMap<>();

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        InputConstraints.upperCaseAndNumbersOnly(diviField, 3);
        diviBoolean = TextFieldValidator.patternTextFieldBinding(diviField, TextFieldValidator.diviPattern, MainApp.resourceMessage.getString("message.divi"), messages);
        nameBoolean = TextFieldValidator.emptyTextFieldBinding(nameField, MainApp.resourceMessage.getString("message.nameDivi"), messages);
        inblBoolean = TextFieldValidator.emptyTextFieldBinding(inblField, MainApp.resourceMessage.getString("message.inbl"), messages);
        BooleanBinding[] mandotariesBinding = new BooleanBinding[]{diviBoolean, nameBoolean, inblBoolean};
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
     * @param division
     */
    public void setDivision(Division division) {
        this.division = division;
        diviField.setText(division.getDivi());
        nameField.setText(division.getName());
        inblField.setText(division.getINBL());
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
            division.setDivi(diviField.getText());
            division.setName(nameField.getText());
            division.setINBL(inblField.getText());
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

        if (diviField.getText() == null || diviField.getText().length() == 0) {
            errorMessage += "No valid first name!\n";
        }
        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "No valid last name!\n";
        }
        if (inblField.getText() == null || inblField.getText().length() == 0) {
            errorMessage += "No valid street!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error 
            Dialogs.create()
                    .title("Invalid Fields")
                    .masthead("Please correct invalid fields")
                    .message(errorMessage)
                    .showError();
            return false;
        }
    }
}
