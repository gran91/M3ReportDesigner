package org.als.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

/**
 *
 * @author Jérémy Chaut
 */
public class ProgressDialogController {

    @FXML
    private Label label;
    @FXML
    private ProgressIndicator progress;

    private Stage stage;

/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

    }

    public void hide(){
        stage.hide();
    }
    
    public void close(){
        stage.close();
    }
    
    public void show() {
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public ProgressIndicator getProgress() {
        return progress;
    }

    public void setProgress(ProgressIndicator progress) {
        this.progress = progress;
    }
}
