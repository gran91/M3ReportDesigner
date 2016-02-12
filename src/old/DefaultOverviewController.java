package old;

import fx.custom.FxUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

import org.controlsfx.dialog.Dialogs;

import org.als.MainApp;
import org.als.model.Model;

public class DefaultOverviewController {

    protected MainApp mainApp;
    protected String nameModel;
    private ClassLoader classLoader;

    @FXML
    protected TableView<Model> dataTable;

    public DefaultOverviewController() {

    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        dataTable.setItems(mainApp.getDataMap().get(nameModel));
    }

    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    protected void handleDelete() {
        int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            dataTable.getItems().remove(selectedIndex);
        } else {
// Nothing selected.
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("main.noselection"));
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new division.
     *
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    @FXML
    protected void handleNew() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Model tempItem = null;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            Class aClass = classLoader.loadClass("org.als.model." + nameModel);
            try {
                tempItem = (Model) aClass.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(DefaultOverviewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DefaultOverviewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (tempItem != null) {
            boolean okClicked = mainApp.showEditDialog(tempItem);
            if (okClicked) {
                mainApp.getDataMap().get(tempItem.getModelName()).add(tempItem);
                dataTable.setItems(mainApp.getDataMap().get(tempItem.getModelName()));
            }
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new division.
     */
    @FXML
    protected void handleCopy() {
        Model selectedItem = dataTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Model tempItem = (Model) selectedItem.clone();
            tempItem.setData(selectedItem.getData());
            boolean okClicked = mainApp.showEditDialog(selectedItem);
        } else {
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("main.noselection"));
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected item model.
     */
    @FXML
    protected void handleEdit() {
        Model selectedItem = dataTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            boolean okClicked = mainApp.showEditDialog(selectedItem);
        } else {
// Nothing selected.
            FxUtil.showAlert(Alert.AlertType.WARNING, mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("main.noselection"),
                    mainApp.getResourceMessage().getString("main.noselection"));
        }
    }
}
