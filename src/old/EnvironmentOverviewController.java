package old;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.als.model.Environment;
import org.als.model.Server;

public class EnvironmentOverviewController extends DefaultOverviewController {

    @FXML
    private TableColumn<Environment, String> nameColumn;
    @FXML
    private TableColumn<Environment, String> ipColumn;
    @FXML
    private TableColumn<Environment, Integer> portColumn;
    @FXML
    private TableColumn<Environment, String> loginColumn;
    @FXML
    private TableColumn<Environment, String> passwordColumn;
    @FXML
    private TableColumn<Environment, String> pathMOMColumn;
    @FXML
    private TableColumn<Environment, Server> serverColumn;
    @FXML
    private TableColumn<Environment, String> serviceColumn;

    public EnvironmentOverviewController() {
        nameModel = "Environment";
    }

    @FXML
    private void initialize() {
// Initialize the division table with the two columns.
        nameColumn.setCellValueFactory(
                cellData -> cellData.getValue().getNameProperty());
        ipColumn.setCellValueFactory(
                cellData -> cellData.getValue().getIpProperty());
//portColumn.setCellValueFactory(cellData -> cellData.getValue().getPort());
        serverColumn.setCellValueFactory(cellData -> cellData.getValue().getServerProperty());
    }
}
