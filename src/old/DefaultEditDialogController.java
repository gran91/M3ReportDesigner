package old;

import fx.custom.FxUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import static org.als.MainApp.resourceMessage;
import org.als.model.Model;

import org.controlsfx.dialog.Dialogs;

/**
 * Dialog to edit details of a division.
 *
 * @author Jérémy Chaut
 */
public class DefaultEditDialogController {

    private Stage dialogStage;
    protected Model model;
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
     * Sets the model to be edited in the dialog.
     *
     * @param mod
     */
    public void setModel(Model mod) {
        this.model = mod;
        this.model.setData(mod.getData());
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
    protected void handleOk() {
        if (isInputValid()) {
            setUIDataToModel();
            okClicked = true;
            dialogStage.close();
        }
    }

    public void setUIDataToModel() {

    }

/**
     * Called when the user clicks cancel.
     */
    @FXML
    protected void handleCancel() {
        dialogStage.close();
    }

/**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    protected boolean isInputValid() {
        String errorMessage = "";

        if (errorMessage.length() == 0) {
            return true;
        } else {
            FxUtil.showAlert(Alert.AlertType.ERROR, resourceMessage.getString("main.invalidField"),
                    resourceMessage.getString("error.list"), errorMessage);
            return false;
        }
    }
}
