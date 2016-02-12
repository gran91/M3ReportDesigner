/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fx.custom;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import org.als.model.Server;

/**
 *
 * @author jeremy.chaut
 */
public class ServerPasswordLabelCell extends TableCell<Server, String> {

    private final Label label;

    public ServerPasswordLabelCell() {
        label = new Label();
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setGraphic(null);
    }

    private String genDotString(int len) {
        String dots = "";

        for (int i = 0; i < len; i++) {
            dots += "\u2022";
        }

        return dots;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            label.setText(genDotString(item.length()));
            setGraphic(label);
        } else {
            setGraphic(null);
        }
    }
}
