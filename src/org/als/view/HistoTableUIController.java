package org.als.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.als.model.TitleCodeParameter;

import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Jérémy Chaut
 */
public class HistoTableUIController {

    @FXML
    private Label labelCode;
    @FXML
    private TextField tableSpacing;
    @FXML
    private TextField tableWidth;
    @FXML
    private ColorPicker tableGridColor;

    private Stage dialogStage;
    private TitleCodeParameter titleCodeParameter;
    StringConverter<? extends Number> converter = new IntegerStringConverter();
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
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
     * @param titleCodeParam
     */
    public void setTitleCodeParameter(TitleCodeParameter titleCodeParam) {
        this.titleCodeParameter = titleCodeParam;
        labelCode.setText(titleCodeParameter.getTitleCode().getValue().trim() + ": " + titleCodeParameter.getTitleDescription().getValue().trim());
        tableSpacing.setText(titleCodeParameter.getTablePadding().getValue().toString());
        tableWidth.setText(titleCodeParameter.getTableParameterColumnWidth().getValue().toString());
        tableGridColor.setValue(titleCodeParameter.getTableLineColor().getValue());

        Bindings.bindBidirectional(tableSpacing.textProperty(), titleCodeParameter.getTablePadding(), (StringConverter<Number>) converter);
        Bindings.bindBidirectional(tableWidth.textProperty(), titleCodeParameter.getTableParameterColumnWidth(), (StringConverter<Number>) converter);
        tableGridColor.valueProperty().bindBidirectional(titleCodeParameter.getTableLineColor());
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
            titleCodeParameter.getTablePadding().setValue(new Integer(tableSpacing.getText()));
            titleCodeParameter.getTableParameterColumnWidth().setValue(new Integer(tableWidth.getText()));
            titleCodeParameter.getTableLineColor().setValue(tableGridColor.getValue());
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

        /*if (diviField.getText() == null || diviField.getText().length() == 0) {
         errorMessage += "No valid first name!\n"; 
         }
         if (nameField.getText() == null || nameField.getText().length() == 0) {
         errorMessage += "No valid last name!\n"; 
         }
         if (inblField.getText() == null || inblField.getText().length() == 0) {
         errorMessage += "No valid street!\n"; 
         }
         */
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
