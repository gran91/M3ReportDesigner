package org.als.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import org.als.model.AvailableValue;

import org.als.model.TitleCodeParameter;
import org.als.table.ColorTableCell;

public class TableAvailableValueUIController {

    @FXML
    private TableView<AvailableValue> titlevalueTable;
    @FXML
    private TableColumn<AvailableValue, String> titleColumn;
    @FXML
    private TableColumn<AvailableValue, String> valueColumn;
    @FXML
    private TableColumn<AvailableValue, Color> colorColumn;

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public TableAvailableValueUIController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().getAvailableTitle());

        valueColumn.setCellValueFactory(cellData -> cellData.getValue().getAvailableValue());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit((TableColumn.CellEditEvent<AvailableValue, String> t) -> {
            ((AvailableValue) (t.getTableView().getItems().get(t.getTablePosition().getRow()))).getAvailableValue().setValue(t.getNewValue());
        });

        colorColumn.setCellValueFactory(cellData -> cellData.getValue().getAvailablecolor());
        colorColumn.setCellFactory(ColorTableCell::new);
        colorColumn.setOnEditCommit((TableColumn.CellEditEvent<AvailableValue, Color> t) -> {
            ((AvailableValue) (t.getTableView().getItems().get(t.getTablePosition().getRow()))).getAvailablecolor().setValue(t.getNewValue());
        });

        titlevalueTable.getSortOrder().add(titleColumn);
    }

    public void setTitleCodeParameter(TitleCodeParameter titleCodeParam) {
        titlevalueTable.setItems(titleCodeParam.getListAvailableValue());
    }
}
