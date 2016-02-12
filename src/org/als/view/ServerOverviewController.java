package org.als.view;

import fx.custom.FxUtil;
import fx.custom.ServerPasswordLabelCell;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import org.als.MainApp;
import org.als.model.Server;

public class ServerOverviewController {

    @FXML
    private TableView<Server> serverTable;
    @FXML
    private TableColumn<Server, String> nameColumn;
    @FXML
    private TableColumn<Server, String> ipColumn;
    @FXML
    private TableColumn<Server, String> loginColumn;
    @FXML
    private TableColumn<Server, String> passwordColumn;

// Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public ServerOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        ipColumn.setCellValueFactory(cellData -> cellData.getValue().getIPProperty());
        loginColumn.setCellValueFactory(cellData -> cellData.getValue().getLoginProperty());
        passwordColumn.setCellFactory((TableColumn<Server, String> cell) -> new ServerPasswordLabelCell());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().getPasswordProperty());
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        serverTable.setItems(mainApp.getServerData());
    }

    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDelete() {
        int selectedIndex = serverTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            serverTable.getItems().remove(selectedIndex);
        } else {
// Nothing selected.
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("server.noselection"),
                    mainApp.getResourceMessage().getString("server.select"));
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new server.
     */
    @FXML
    private void handleNew() {
        Server tempServer = new Server();
        boolean okClicked = mainApp.getRootController().showServerEditDialog(tempServer);
        if (okClicked) {
            mainApp.getServerData().add(tempServer);
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new server.
     */
    @FXML
    private void handleCopy() {
        Server selectedServer = serverTable.getSelectionModel().getSelectedItem();
        if (selectedServer != null) {
            Server tempServer = new Server();
            tempServer.populateData(selectedServer.extractData());
            boolean okClicked = mainApp.getRootController().showServerEditDialog(tempServer);
            if (okClicked) {
                mainApp.getServerData().add(tempServer);
            }
        } else {
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("server.noselection"),
                    mainApp.getResourceMessage().getString("server.select"));
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected server.
     */
    @FXML
    private void handleEdit() {
        Server selectedServer = serverTable.getSelectionModel().getSelectedItem();
        if (selectedServer != null) {
            boolean okClicked = mainApp.getRootController().showServerEditDialog(selectedServer);
        } else {
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("server.noselection"),
                    mainApp.getResourceMessage().getString("server.select"));
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
