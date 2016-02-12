package org.als.view;

import api.ConnexionAPI;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.als.model.CodeParameter;
import org.als.model.Condition;
import org.als.model.Division;
import org.als.model.Environment;
import org.als.model.TitleCodeParameter;
import org.als.table.ColorTableCell;
import org.als.table.ComboBoxTableCellCondition;
import resources.Resource;

public class TableConditionUIController {

    @FXML
    private ComboBox<CodeParameter> parameterCode;
    @FXML
    private Label parameterDescription;

    @FXML
    private Button bAdd, bDel;

    @FXML
    private TableView<Condition> conditionTable;
    @FXML
    private TableColumn<Condition, String> codeColumn;
    @FXML
    private TableColumn<Condition, String> descriptionColumn;
    @FXML
    private TableColumn<Condition, String> conditionColumn;
    @FXML
    private TableColumn<Condition, String> conditionValueColumn;
    @FXML
    private TableColumn<Condition, Color> conditionColorColumn;
    @FXML
    private TableColumn<Condition, String> substitutionColumn;

    private TitleCodeParameter titlecodeparameter;
    private Environment environment;
    private Division division;
    private final ObservableList<String> numericConditionList = FXCollections.observableArrayList();
    private final ObservableList<String> alphaConditionList = FXCollections.observableArrayList();
    private final ObservableList<CodeParameter> listParameter = FXCollections.observableArrayList();
    private final ResourceBundle resourceMessage = ResourceBundle.getBundle("resources/language", Locale.getDefault());

/**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public TableConditionUIController() {
    }

/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        parameterCode.setItems(listParameter);
        parameterCode.setConverter(new StringConverter<CodeParameter>() {

            @Override
            public String toString(CodeParameter object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public CodeParameter fromString(String string) {
                return parameterCode.getItems().get(parameterCode.getSelectionModel().getSelectedIndex());
            }
        });
        codeColumn.setCellValueFactory(cellData -> cellData.getValue().getConditionCode());
        codeColumn.setSortType(TableColumn.SortType.ASCENDING);

        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().getConditionDescription());
        conditionColumn.setCellValueFactory(cellData -> cellData.getValue().getConditionType());
        Callback<TableColumn<Condition, String>, TableCell<Condition, String>> cellConditionComparator = new Callback<TableColumn<Condition, String>, TableCell<Condition, String>>() {
            @Override
            public TableCell call(TableColumn p) {
                return new ComboBoxTableCellCondition();
            }
        };
        conditionColumn.setCellFactory(cellConditionComparator);

        conditionValueColumn.setCellValueFactory(cellData -> cellData.getValue().getConditionValue());
        conditionValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        conditionValueColumn.setOnEditCommit((TableColumn.CellEditEvent<Condition, String> t) -> {
            ((Condition) (t.getTableView().getItems().get(t.getTablePosition().getRow()))).getConditionValue().setValue(t.getNewValue());
        });

        conditionColorColumn.setCellValueFactory(cellData -> cellData.getValue().getConditionColor());
        conditionColorColumn.setCellFactory(ColorTableCell::new);
        conditionColorColumn.setOnEditCommit((TableColumn.CellEditEvent<Condition, Color> t) -> {
            ((Condition) (t.getTableView().getItems().get(t.getTablePosition().getRow()))).getConditionColor().setValue(t.getNewValue());
        });

        substitutionColumn.setCellValueFactory(cellData -> cellData.getValue().getSubstitutionValue());
        substitutionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        substitutionColumn.setOnEditCommit((TableColumn.CellEditEvent<Condition, String> t) -> {
            ((Condition) (t.getTableView().getItems().get(t.getTablePosition().getRow()))).getSubstitutionValue().setValue(t.getNewValue());
        });

        conditionTable.getSortOrder().add(codeColumn);

        parameterCode.valueProperty().addListener((ObservableValue<? extends CodeParameter> observable, CodeParameter oldValue, CodeParameter newValue) -> {
            if (newValue != null) {
                bAdd.setDisable(false);
                bDel.setDisable(false);
                parameterDescription.setText(newValue.getParameterDescription().get());
            } else {
                bAdd.setDisable(true);
                bDel.setDisable(true);
            }
        });
    }

    public Callback<TableColumn<Condition, String>, TableCell<Condition, String>> listConditionComparator() {
        return ComboBoxTableCell.forTableColumn(numericConditionList);
    }

    public void setEnvironment(Environment e) {
        environment = e;
    }

    public void setDivision(Division d) {
        division = d;
    }

    public void setTitleCodeParameter(TitleCodeParameter titleCodeParam) {
        titlecodeparameter = titleCodeParam;
        conditionTable.setItems(titleCodeParam.getListCondition());
        listPrestationParameter();

    }

    private void listPrestationParameter() {
        listParameter.clear();
        parameterCode.setDisable(true);
        bAdd.setDisable(true);
        bDel.setDisable(true);
        conditionTable.setDisable(true);
        final Service<Void> listCodeParameterService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String ip = environment.getIP();
                        int port = environment.getPort();
                        String user = environment.getLogin();
                        ConnexionAPI conAPI = new ConnexionAPI(ip, port);
                        if (conAPI.getResult() != -1) {
                            if (conAPI.verifConnection(user, "")) {
                                String in = String.format("%-15s", Resource.TRANS_PARAMCODEJ);
                                in += String.format("%-3s", division.getDivi());
                                in += String.format("%-10s", titlecodeparameter.getTitleCode().getValue());
                                ArrayList aOut = ConnexionAPI.parseFieldData(conAPI.listField(Resource.APINAME, Resource.TRANS_PARAMCODEJ, 'O', user, ""));
                                ArrayList listParamCodeJ = conAPI.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_PARAMCODEJ), in, user, "");
                                Object[][] tableData = new Object[listParamCodeJ.size()][((ArrayList) aOut.get(0)).size()];
                                for (int i = 0; i < listParamCodeJ.size(); i++) {
                                    String code = "";
                                    String desc = "";
                                    String type = "";
                                    for (int j = 5; j < 8; j++) {
                                        try {
                                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(j).toString()) - 1;
                                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(j).toString());
                                            if (j == 5) {
                                                code = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                                            } else if (j == 7) {
                                                desc = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                                            } else if (j == 6) {
                                                type = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                                            }
                                        } catch (NumberFormatException ex) {
                                        } catch (Exception e) {
                                        }
                                    }
                                    listParameter.add(new CodeParameter(code, desc, type));
                                    for (int k = 0; k < conditionTable.getItems().size(); k++) {
                                        Condition c = conditionTable.getItems().get(k);
                                        if (c.getConditionCode().getValue().trim().equals(code.trim())) {
                                            c.getConditionDescription().set(desc);
                                        }
                                    }
                                }
                            } else {
                                throw new RuntimeException(String.format(resourceMessage.getString("api.user"), (Object) new String[]{conAPI.getUser()}));
                            }
                        } else {
                            throw new RuntimeException(String.format(resourceMessage.getString("api.parameter")));
                        }
                        return null;
                    }
                };
            }
        };
        listCodeParameterService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.err.println(listCodeParameterService.getException());
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    parameterCode.setDisable(false);
                    bAdd.setDisable(false);
                    bDel.setDisable(false);
                    conditionTable.setDisable(false);
                    break;
            }
        });
        listCodeParameterService.start();
    }

/**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void delCondition() {
        int selectedIndex = conditionTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            conditionTable.getItems().remove(selectedIndex);
        } else {

        }
    }

/**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new server.
     */
    @FXML
    private void addCondition() {
        if (parameterCode.getValue() != null) {
            Condition c = new Condition(parameterCode.getValue().getParameterCode().get(), parameterCode.getValue().getParameterType().get());
            c.getConditionDescription().set(parameterCode.getValue().getParameterDescription().get());
            conditionTable.getItems().add(c);
        }
    }

/**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new condition.
     */
    @FXML
    private void handleCopy() {
/* Condition selectedCondition = conditionTable.getSelectionModel().getSelectedItem();
         if (selectedCondition != null) {
         Condition tempCondition = new Condition();
         tempCondition.populateData(selectedCondition.extractData());
         boolean okClicked = mainApp.getRootController().showServerEditDialog(tempCondition);
         if (okClicked) {
         mainApp.getServerData().add(tempCondition);
         }
         } else {
         Dialogs.create()
         .title("No Selection")
         .masthead("No Divison Selected")
         .message("Please select a server in the table.")
         .showWarning();
         }*/
    }

/**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected condition.
     */
    @FXML
    private void handleEdit() {
/*Server selectedServer = conditionTable.getSelectionModel().getSelectedItem();
         if (selectedServer != null) {
         boolean okClicked = mainApp.getRootController().showServerEditDialog(selectedServer);
         } else {
/ Nothing selected.
         Dialogs.create()
         .title("No Selection")
         .masthead("No Divison Selected")
         .message("Please select a server in the table.")
         .showWarning();
         }*/
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.isControlDown() && event.getCode().equals(KeyCode.DIGIT1)) {
            addCondition();
        }
        if (event.isControlDown() && event.getCode().equals(KeyCode.DIGIT2)) {
            handleEdit();
        }
        if (event.isControlDown() && event.getCode().equals(KeyCode.DIGIT3)) {
            handleCopy();
        }
        if (event.isControlDown() && event.getCode().equals(KeyCode.DIGIT4)) {
            delCondition();
        }
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            handleEdit();
        }
    }
}
