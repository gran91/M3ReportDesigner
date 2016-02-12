package org.als.view;

import fx.custom.FxUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import org.als.MainApp;
import org.als.model.Division;

public class DivisionOverviewController {

    @FXML
    private TableView<Division> divisionTable;
    @FXML
    private TableColumn<Division, String> diviCodeColumn;
    @FXML
    private TableColumn<Division, String> diviNameColumn;
    @FXML
    private TableColumn<Division, String> INBLColumn;

// Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public DivisionOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
// Initialize the division table with the two columns.
        diviCodeColumn.setCellValueFactory(
                cellData -> cellData.getValue().getDiviProperty());
        diviNameColumn.setCellValueFactory(
                cellData -> cellData.getValue().getNameProperty());
        INBLColumn.setCellValueFactory(
                cellData -> cellData.getValue().getINBLProperty());
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

// Add observable list data to the table
        divisionTable.setItems(mainApp.getDivisionData());
    }

    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDelete() {
        int selectedIndex = divisionTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            divisionTable.getItems().remove(selectedIndex);
        } else {
// Nothing selected.
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("division.noselection"),
                    mainApp.getResourceMessage().getString("division.select"));
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new division.
     */
    @FXML
    private void handleNew() {
        Division tempDivision = new Division();
        boolean okClicked = mainApp.getRootController().showDivisionEditDialog(tempDivision);
        if (okClicked) {
            mainApp.getDivisionData().add(tempDivision);
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new division.
     */
    @FXML
    private void handleCopy() {
        Division selectedDivision = divisionTable.getSelectionModel().getSelectedItem();
        if (selectedDivision != null) {
            Division tempDivision = new Division();
            tempDivision.populateData(selectedDivision.extractData());
            boolean okClicked = mainApp.getRootController().showDivisionEditDialog(tempDivision);
            if (okClicked) {
                mainApp.getDivisionData().add(tempDivision);
            }
        } else {
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("division.noselection"),
                    mainApp.getResourceMessage().getString("division.select"));
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected division.
     */
    @FXML
    private void handleEdit() {
        Division selectedDivision = divisionTable.getSelectionModel().getSelectedItem();
        if (selectedDivision != null) {
            boolean okClicked = mainApp.getRootController().showDivisionEditDialog(selectedDivision);
        } else {
// Nothing selected.
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("division.noselection"),
                    mainApp.getResourceMessage().getString("division.select"));
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.isControlDown() && event.getCode().equals(KeyCode.DIGIT1)) {
            handleNew();
        }
        if (event.isControlDown() && event.getCode().equals(KeyCode.DIGIT2)) {
            handleEdit();
        }
        if (event.isControlDown() && event.getCode().equals(KeyCode.DIGIT3)) {
            handleCopy();
        }
        if (event.isControlDown() && event.getCode().equals(KeyCode.DIGIT4)) {
            handleDelete();
        }
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            handleEdit();
        }
    }
}
