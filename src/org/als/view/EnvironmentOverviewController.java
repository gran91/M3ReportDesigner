package org.als.view;

import fx.custom.EnvironmentPasswordLabelCell;
import fx.custom.FxUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import org.als.MainApp;
import org.als.model.Environment;
import org.als.model.Server;
import org.als.model.Service;

public class EnvironmentOverviewController {

    @FXML
    private TableView<Environment> environmentTable;
    @FXML
    private TableColumn<Environment, String> nameColumn;
    @FXML
    private TableColumn<Environment, String> ipColumn;
    @FXML
    private TableColumn<Environment, Number> portColumn;
    @FXML
    private TableColumn<Environment, String> loginColumn;
    @FXML
    private TableColumn<Environment, String> passwordColumn;
    @FXML
    private TableColumn<Environment, String> pathMOMColumn;
    @FXML
    private TableColumn<Environment, Server> serverColumn;
    @FXML
    private TableColumn<Environment, Service> serviceColumn;

// Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public EnvironmentOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        ipColumn.setCellValueFactory(cellData -> cellData.getValue().getIpProperty());
        portColumn.setCellValueFactory(cellData -> cellData.getValue().getPortProperty());
        loginColumn.setCellValueFactory(cellData -> cellData.getValue().getLoginProperty());
        passwordColumn.setCellFactory((TableColumn<Environment, String> cell) -> new EnvironmentPasswordLabelCell());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().getPasswordProperty());
        pathMOMColumn.setCellValueFactory(cellData -> cellData.getValue().getPathMOMProperty());
        serverColumn.setCellValueFactory(cellData -> cellData.getValue().getServerProperty());
        serviceColumn.setCellValueFactory(cellData -> cellData.getValue().getServiceProperty());
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        environmentTable.setItems(mainApp.getEnvironmentData());
    }

    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDelete() {
        int selectedIndex = environmentTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            environmentTable.getItems().remove(selectedIndex);
        } else {
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("environment.noselection"),
                    mainApp.getResourceMessage().getString("environment.select"));
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new division.
     */
    @FXML
    private void handleNew() {
        Environment tempEnvironment = new Environment();
        boolean okClicked = mainApp.getRootController().showEnvironmentEditDialog(tempEnvironment);
        if (okClicked) {
            mainApp.getEnvironmentData().add(tempEnvironment);
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new division.
     */
    @FXML
    private void handleCopy() {
        Environment selectedEnvironment = environmentTable.getSelectionModel().getSelectedItem();
        if (selectedEnvironment != null) {
            Environment tempEnvironment = new Environment();
            tempEnvironment.populateData(selectedEnvironment.extractData());
            boolean okClicked = mainApp.getRootController().showEnvironmentEditDialog(tempEnvironment);
            if (okClicked) {
                mainApp.getEnvironmentData().add(tempEnvironment);
            }
        } else {
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("environment.noselection"),
                    mainApp.getResourceMessage().getString("environment.select"));
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected division.
     */
    @FXML
    private void handleEdit() {
        Environment selectedEnvironment = environmentTable.getSelectionModel().getSelectedItem();
        if (selectedEnvironment != null) {
            boolean okClicked = mainApp.getRootController().showEnvironmentEditDialog(selectedEnvironment);
        } else {
// Nothing selected.
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("environment.noselection"),
                    mainApp.getResourceMessage().getString("environment.select"));
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
