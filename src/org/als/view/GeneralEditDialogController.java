package org.als.view;

import fx.custom.FxUtil;
import fx.custom.InputConstraints;
import fx.custom.TextFieldValidator;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.als.MainApp;
import org.als.model.General;

/**
 * Dialog to edit details of a general settings.
 *
 * @author Jérémy Chaut
 */
public class GeneralEditDialogController {

    @FXML
    private ComboBox<Font> fontField;
    @FXML
    private ComboBox<Integer> sizeField;
    @FXML
    private ColorPicker colorField;
    @FXML
    private TextField textField;
    @FXML
    private TextField logoL;
    @FXML
    private TextField logoH;
    @FXML
    private TextField timeoutField;
    @FXML
    private Button okButton;

    private Stage dialogStage;
    private General general;
    private boolean okClicked = false;
    private BooleanBinding textBoolean, timeoutBoolean,logoLBoolean,logoHBoolean;

    protected Map<BooleanBinding, String> messages = new LinkedHashMap<>();

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        ObservableList<Font> listFont = FXCollections.observableArrayList();
        Font.getFamilies().stream().forEach((s) -> {
            listFont.addAll(new Font(s, 12));
        });
        fontField.setItems(listFont);
        FxUtil.autoCompleteComboBox(fontField, FxUtil.AutoCompleteMode.STARTS_WITH);
        fontField.setConverter(new StringConverter<Font>() {
            private Object t;

            @Override
            public String toString(Font object) {
                if (object == null) {
                    return "";
                }
                return object.getName();
            }

            @Override
            public Font fromString(String string) {
                return fontField.getSelectionModel().getSelectedItem();
            }
        });

        ObservableList<Integer> listFontSize = FXCollections.observableArrayList();
        for (int i = 5; i <= 25; i++) {
            listFontSize.add(i);
        }
        sizeField.setItems(listFontSize);
        InputConstraints.numbersOnly(timeoutField);
        InputConstraints.numbersOnly(logoL);
        InputConstraints.numbersOnly(logoH);
        textBoolean = TextFieldValidator.emptyTextFieldBinding(textField, MainApp.resourceMessage.getString("message.text"), messages);
        timeoutBoolean = TextFieldValidator.emptyTextFieldBinding(timeoutField, MainApp.resourceMessage.getString("message.timeout"), messages);
        logoLBoolean = TextFieldValidator.emptyTextFieldBinding(logoL, MainApp.resourceMessage.getString("message.logo"), messages);
        logoHBoolean = TextFieldValidator.emptyTextFieldBinding(logoH, MainApp.resourceMessage.getString("message.logo"), messages);
        
        BooleanBinding[] mandotariesBinding = new BooleanBinding[]{textBoolean, timeoutBoolean,logoLBoolean,logoHBoolean};
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
     * @param general
     */
    public void setGeneral(General general) {
        this.general = general;
        fontField.setValue(general.getDefaultFontProperty().get());
        sizeField.setValue(general.getDefaultFontSizeProperty().get());
        colorField.setValue(general.getDefaultFontColorProperty().get());
        textField.setText(general.getDefaultTextProperty().get());
        logoL.setText(""+general.getDefaultLogoLProperty().get());
        logoH.setText(""+general.getDefaultLogoHProperty().get());
        timeoutField.setText("" + general.getTimeoutProperty().getValue());
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
            general.getDefaultFontProperty().setValue(fontField.getSelectionModel().getSelectedItem());
            general.getDefaultFontSizeProperty().setValue(sizeField.getSelectionModel().getSelectedItem());
            general.getDefaultFontColorProperty().setValue(colorField.getValue());
            general.getDefaultTextProperty().setValue(textField.getText());
            general.getDefaultLogoLProperty().setValue(Integer.parseInt(logoL.getText()));
            general.getDefaultLogoHProperty().setValue(Integer.parseInt(logoH.getText()));
            general.getTimeoutProperty().setValue(Long.parseLong(timeoutField.getText()));
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
            return false;
        }
    }
}
