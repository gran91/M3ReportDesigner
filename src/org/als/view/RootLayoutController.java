package org.als.view;

import fx.custom.FxUtil;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.als.MainApp;
import org.als.model.Division;
import org.als.model.Environment;
import org.als.model.Server;
import resources.Resource;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 *
 * @author Jérémy Chaut
 */
public class RootLayoutController {

// Reference to the main application
    @FXML
    private RadioMenuItem menuFR;
    @FXML
    private RadioMenuItem menuEN;
    @FXML
    private Menu langmenu;
    private MainApp mainApp;
    private Stage generalStage, divisionStage, environmentStage, serverStage;

    @FXML
    public void initialize() {

    }

/**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        langmenu.getItems().stream().forEach(m -> {
            ((RadioMenuItem) m).setSelected(this.mainApp.prefs.get(MainApp.LANGUAGE, null).equals(m.getId()));
            m.setOnAction(e -> {
                Locale.setDefault(new Locale(m.getId()));
                this.mainApp.prefs.put(MainApp.LANGUAGE, m.getId());
                langmenu.getItems().stream().forEach(m1 -> {
                    ((RadioMenuItem) m).setSelected(m1.getId().equals(this.mainApp.prefs.get(MainApp.LANGUAGE, null)));
                });
                try {
                    mainApp.getPrimaryStage().close();
                } catch (Exception ex) {
                    Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
                }
                new MainApp().start(new Stage());
            });
        });
    }

/**
     * Creates an empty address book.
     */
    @FXML
    private void handleNew() {
        mainApp.clearData();
        mainApp.setRegistryFilePath(null);
    }

/**
     * Opens a FileChooser to let the user select an address book to load.
     */
    @FXML
    private void handleOpen() {
        DirectoryChooser fileChooser = new DirectoryChooser();
// Show save file dialog
        File file = fileChooser.showDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadDataDirectory(file);
        }
    }

/**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    @FXML
    private void handleSave() {
        File directory = mainApp.getDataDirectoryPath();
        if (directory != null) {
            mainApp.saveDataToFile(directory);
        } else {
            handleSaveAs();
        }
    }

/**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {
        DirectoryChooser fileChooser = new DirectoryChooser();
// Show save file dialog
        File file = fileChooser.showDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.saveDataToFile(file);
        }
    }

/**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
        FxUtil.showAlert(Alert.AlertType.INFORMATION, mainApp.getResourceMessage().getString("about.title"), String.format(mainApp.getResourceMessage().getString("about.header"), Resource.VERSION), String.format(mainApp.getResourceMessage().getString("about.text"), Resource.VERSION));
    }

/**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleReport() {
        mainApp.showRunReportDialog();
    }

    public void showDivisionOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/DivisionOverview.fxml"));
            AnchorPane divisionOverview = (AnchorPane) loader.load();

            divisionStage = new Stage();
            divisionStage.setTitle(MainApp.resourceMessage.getString("division.title"));
            divisionStage.initModality(Modality.NONE);
            divisionStage.initOwner(mainApp.getPrimaryStage());
            divisionStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(divisionOverview);
            divisionStage.setScene(scene);

            DivisionOverviewController controller = loader.getController();
            controller.setMainApp(mainApp);

            divisionStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public boolean showGeneralEditDialog() {
        try {
// Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/GeneralEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

// Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.resourceMessage.getString("general.title"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(generalStage);
            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);
            scene.getStylesheets().add(getClass().getResource("/org/als/application.css").toExternalForm());
            dialogStage.setScene(scene);

// Set the division into the controller.
            GeneralEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setGeneral(mainApp.getGeneral());

// Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

/**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param division the division object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showDivisionEditDialog(Division division) {
        try {
// Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/DivisionEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

// Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.resourceMessage.getString("division.title"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(divisionStage);
            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);
            scene.getStylesheets().add(getClass().getResource("/org/als/application.css").toExternalForm());
            dialogStage.setScene(scene);

// Set the division into the controller.
            DivisionEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDivision(division);

// Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showEnvironmentOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/EnvironmentOverview.fxml"));
            AnchorPane environmentOverview = (AnchorPane) loader.load();

            environmentStage = new Stage();
            environmentStage.setTitle(MainApp.resourceMessage.getString("division.title"));
            environmentStage.initModality(Modality.NONE);
            environmentStage.initOwner(mainApp.getPrimaryStage());
            environmentStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(environmentOverview);
            environmentStage.setScene(scene);

            EnvironmentOverviewController controller = loader.getController();
            controller.setMainApp(mainApp);

            environmentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param environment the environment object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showEnvironmentEditDialog(Environment environment) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/EnvironmentEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.resourceMessage.getString("environment.title"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(environmentStage);
            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);
            scene.getStylesheets().add(getClass().getResource("/org/als/application.css").toExternalForm());
            dialogStage.setScene(scene);

            EnvironmentEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.getServerField().setItems(mainApp.getServerData());
            controller.setEnvironment(environment);
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showServerOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/ServerOverview.fxml"));
            AnchorPane serverOverview = (AnchorPane) loader.load();

            serverStage = new Stage();
            serverStage.setTitle(MainApp.resourceMessage.getString("server.title"));
            serverStage.initModality(Modality.NONE);
            serverStage.initOwner(mainApp.getPrimaryStage());
            serverStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(serverOverview);

            serverStage.setScene(scene);
            ServerOverviewController controller = loader.getController();
            controller.setMainApp(mainApp);

            serverStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param server the server object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showServerEditDialog(Server server) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/ServerEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.resourceMessage.getString("server.title"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(serverStage);
            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);
            scene.getStylesheets().add(getClass().getResource("/org/als/application.css").toExternalForm());
            dialogStage.setScene(scene);

            ServerEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setServer(server);
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
