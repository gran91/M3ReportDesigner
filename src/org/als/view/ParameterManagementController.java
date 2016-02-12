/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import org.als.MainApp;
import org.als.model.Division;
import org.als.model.Environment;
import org.als.model.TitleCodeParameter;

/**
 * FXML Controller class
 *
 * @author Jérémy Chaut
 */
public class ParameterManagementController {

    @FXML
    private HistoTableUIController histoTableController;
    @FXML
    private TableAvailableValueUIController valueTableController;
    @FXML
    private TableConditionUIController conditionTableController;

    @FXML
    private TitledPane histoPane, valuePane, conditionPane;

    private Environment environment;
    private Division division;
    private TitleCodeParameter titleCodeParameter;
    private MainApp mainApp;
    private Stage dialogStage;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
// TODO
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
     * @param titleCodeParam
     */
    public void setTitleCodeParameter(TitleCodeParameter titleCodeParam) {
        this.titleCodeParameter = titleCodeParam;
        ObservableList<String> type = TitleCodeParameter.listType();
        int n = type.indexOf(titleCodeParameter.getTitleType().getValue());
        if (n != 2 && n != 3) {
            histoPane.setVisible(false);
            valuePane.setVisible(false);
        }
        histoTableController.setTitleCodeParameter(titleCodeParam);
        valueTableController.setTitleCodeParameter(titleCodeParam);
        conditionTableController.setTitleCodeParameter(titleCodeParam);
    }

    public void setEnvironment(Environment e) {
        environment = e;
        conditionTableController.setEnvironment(e);
    }

    public void setDivision(Division d) {
        division = d;
        conditionTableController.setDivision(d);
    }

}
