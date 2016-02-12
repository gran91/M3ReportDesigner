package org.als;

import fx.custom.FxUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import org.als.model.Division;
import org.als.model.DivisionWrapper;

import org.als.model.*;
import old.DefaultEditDialogController;
import old.DefaultOverviewController;
import org.als.view.MainReportController;
import org.als.view.ProgressDialogController;
import org.als.view.RootLayoutController;
import org.als.view.RunReportDialogController;
import resources.Resource;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Locale locale;
    private Properties configProp;
    public static final String configFileName = "config";
    public static ResourceBundle resourceMessage;
    public static String LANGUAGE = "lang";
    public Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
    RootLayoutController rootController;
    private HashMap<String, ObservableList> dataMap;
    private final ObservableList<Division> divisionData = FXCollections.observableArrayList();
    private final ObservableList<Server> serverData = FXCollections.observableArrayList();
    private final ObservableList<Environment> environmentData = FXCollections.observableArrayList();
    private final ObservableList<General> generalData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public MainApp() {
        if (prefs.get(LANGUAGE, null) == null) {
            prefs.put(LANGUAGE, Locale.getDefault().toString());
        } else {
            Locale.setDefault(new Locale(prefs.get(LANGUAGE, null).split("_")[0], prefs.get(LANGUAGE, null).split("_")[1]));
        }
        locale = Locale.getDefault();
        Locale.setDefault(locale);
        resourceMessage = ResourceBundle.getBundle("resources/language", locale);
        createDataMap();
        Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);
    }

    public void clearData() {
        divisionData.clear();
        environmentData.clear();
        serverData.clear();
    }

    private void createDataMap() {
        dataMap = new HashMap();
        dataMap.put("Environment", environmentData);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(Resource.TITLE);

// Set the application icon
        this.primaryStage.getIcons().add(Resource.LOGO_ICON);

        initRootLayout();
        showMainReport();
    }

    /**
     * Initializes the root layout and tries to load the last opened person
     * file.
     */
    public void initRootLayout() {
        try {
// Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", locale));
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

// Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);

// Give the controller access to the main app.
            rootController = loader.getController();
            rootController.setMainApp(this);
            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                    System.out.println("Width: " + newSceneWidth);
                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                    System.out.println("Height: " + newSceneHeight);
                }
            });
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

// Try to load last opened person file.
        File file = getDataDirectoryPath();
        if (file != null) {
            loadDataDirectory(file);
        }
    }

    public void showRunReportDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", locale));
            loader.setLocation(MainApp.class.getResource("view/RunReportDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(resourceMessage.getString("m3.runreport"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            dialogStage.setScene(scene);

            RunReportDialogController controller = loader.getController();
            controller.setMainApp(this);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProgressDialogController showProgressDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", locale));
            loader.setLocation(MainApp.class.getResource("view/ProgressDialog.fxml"));
            StackPane page = (StackPane) loader.load();

            Stage dialogStage = new Stage(StageStyle.UNDECORATED);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ProgressDialogController controller = loader.getController();
            controller.setStage(dialogStage);
            return controller;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showMainReport() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", locale));
            loader.setLocation(MainApp.class.getResource("view/MainReport.fxml"));
            rootLayout.setCenter(loader.load());
            MainReportController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initMainReport() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", locale));
            loader.setLocation(MainApp.class.getResource("view/MainReport.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.show();
            MainReportController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showModelOverview(String mod) {
        try {
// Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", locale));
            loader.setLocation(MainApp.class.getResource("view/" + mod + "Overview.fxml"));
            AnchorPane modelOverview = (AnchorPane) loader.load();

// Set person overview into the center of root layout.
            rootLayout.setCenter(modelOverview);

// Give the controller access to the main app.
            DefaultOverviewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param model the model object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showEditDialog(Model model) {
        try {
// Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", locale));
            loader.setLocation(MainApp.class.getResource("view/" + model.getModelName() + "EditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

// Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle(model.getI18NName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

// Set the model into the controller.
            DefaultEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setModel(model);

// Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens a dialog to view all entities of model.
     *
     * @param model the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public FXMLLoader createModelOverviewDialog(String model) {
        try {
// Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", locale));
            loader.setLocation(MainApp.class.getResource("view/" + model + "Overview.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

// Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle(model);
            dialogStage.initModality(Modality.NONE);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

// Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getDataDirectoryPath() {
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setRegistryFilePath(File file) {
        if (file != null) {
            prefs.put("filePath", file.getPath());
            primaryStage.setTitle(Resource.TITLE + " " + Resource.VERSION + " - " + file.getName()
            );
        } else {
            prefs.remove("filePath");
            primaryStage.setTitle(Resource.TITLE + " " + Resource.VERSION);
        }
    }

    /**
     * Loads model data from the specified directory.
     *
     * @param directory
     */
    public void loadDataDirectory(File directory) {
        JAXBContext context;
        Unmarshaller um;
        try {
            if (directory.exists() && directory.isDirectory()) {
                File[] listFile = directory.listFiles();
                for (File f : listFile) {
                    if (f.getName().startsWith("division")) {
                        context = JAXBContext.newInstance(DivisionWrapper.class);
                        um = context.createUnmarshaller();
                        DivisionWrapper wrapper = (DivisionWrapper) um.unmarshal(f);
                        divisionData.clear();
                        divisionData.addAll(wrapper.getDivisions());
                    } else if (f.getName().startsWith("server")) {
                        context = JAXBContext.newInstance(ServerWrapper.class);
                        um = context.createUnmarshaller();
                        ServerWrapper wrapper = (ServerWrapper) um.unmarshal(f);
                        serverData.clear();
                        serverData.addAll(wrapper.getServers());
                    } else if (f.getName().startsWith("environment")) {
                        context = JAXBContext.newInstance(EnvironmentWrapper.class);
                        um = context.createUnmarshaller();
                        EnvironmentWrapper wrapper = (EnvironmentWrapper) um.unmarshal(f);
                        environmentData.clear();
                        environmentData.addAll(wrapper.getEnvironments());
                    } else if (f.getName().startsWith("general")) {
                        context = JAXBContext.newInstance(GeneralWrapper.class);
                        um = context.createUnmarshaller();
                        GeneralWrapper wrapper = (GeneralWrapper) um.unmarshal(f);
                        generalData.clear();
                        generalData.addAll(wrapper.getGenerals());
                    }
                }
            }
// Save the file path to the registry.
            setRegistryFilePath(directory);
        } catch (Exception e) { // catches ANY exception
            FxUtil.showAlert(Alert.AlertType.ERROR, resourceMessage.getString("main.error"),
                    resourceMessage.getString("error.file"),
                    String.format(resourceMessage.getString("error.loaddirectory"), directory.getPath()));
        }
    }

    /**
     * Saves the current person data to the specified file.
     *
     * @param file
     */
    public void saveDataToFile(File file) {
        try {
            saveGeneral(file);
            saveDivision(file);
            saveServer(file);
            saveEnvironment(file);
            setRegistryFilePath(file);
        } catch (Exception e) { // catches ANY exception
            FxUtil.showAlert(Alert.AlertType.ERROR, resourceMessage.getString("main.error"),
                    resourceMessage.getString("error.file"),
                    String.format(resourceMessage.getString("error.savefile"), file.getPath()));
        }
    }

    private void saveGeneral(File file) throws PropertyException, JAXBException {
        if (generalData.size() > 0) {
            JAXBContext context = JAXBContext.newInstance(GeneralWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            GeneralWrapper wrapper = new GeneralWrapper();
            wrapper.setGenerals(generalData);
            File fileData = new File(file.getAbsoluteFile() + System.getProperty("file.separator") + "general.xml");
            m.marshal(wrapper, fileData);
        }
    }

    private void saveDivision(File file) throws PropertyException, JAXBException {
        if (divisionData.size() > 0) {
            JAXBContext context = JAXBContext.newInstance(DivisionWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            DivisionWrapper wrapper = new DivisionWrapper();
            wrapper.setDivisions(divisionData);
            File fileData = new File(file.getAbsoluteFile() + System.getProperty("file.separator") + "division.xml");
            m.marshal(wrapper, fileData);
        }
    }

    private void saveServer(File file) throws PropertyException, JAXBException {
        if (serverData.size() > 0) {
            JAXBContext context = JAXBContext.newInstance(ServerWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ServerWrapper wrapper = new ServerWrapper();
            wrapper.setServers(serverData);
            File fileData = new File(file.getAbsoluteFile() + System.getProperty("file.separator") + "server.xml");
            m.marshal(wrapper, fileData);
        }
    }

    private void saveEnvironment(File file) throws PropertyException, JAXBException {
        if (environmentData.size() > 0) {
            JAXBContext context = JAXBContext.newInstance(EnvironmentWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            EnvironmentWrapper wrapper = new EnvironmentWrapper();
            wrapper.setEnvironments(environmentData);
            File fileData = new File(file.getAbsoluteFile() + System.getProperty("file.separator") + "environment.xml");
            m.marshal(wrapper, fileData);
        }
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<Division> getDivisionData() {
        return divisionData;
    }

    public ObservableList<Environment> getEnvironmentData() {
        return environmentData;
    }

    public ObservableList<Server> getServerData() {
        return serverData;
    }

    public General getGeneral() {
        if (generalData.isEmpty()) {
            generalData.add(General.getDefault());
        }
        return generalData.get(0);
    }

    public ResourceBundle getResourceMessage() {
        return resourceMessage;
    }

    public HashMap<String, ObservableList> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<String, ObservableList> dataMap) {
        this.dataMap = dataMap;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(BorderPane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public RootLayoutController getRootController() {
        return rootController;
    }

    public void setRootController(RootLayoutController rootController) {
        this.rootController = rootController;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
