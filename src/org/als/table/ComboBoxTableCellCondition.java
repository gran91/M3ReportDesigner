/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.table;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import org.als.model.Condition;

/**
 *
 * @author Jeremy.CHAUT
 */
public class ComboBoxTableCellCondition extends TableCell<Condition, String> {

    private final ComboBox<String> comboBox;

    public ComboBoxTableCellCondition() {
        comboBox = new ComboBox<>();
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();

            comboBox.setItems(getTableView().getItems().get(getIndex()).getListComparator());
            comboBox.getSelectionModel().select(getItem());

            comboBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!newValue) {
                        commitEdit(comboBox.getSelectionModel().getSelectedItem());
                    }
                }
            });

            setText(null);
            setGraphic(comboBox);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                setText(null);
                setGraphic(comboBox);
            } else {
                setText(getItem());
                setGraphic(null);
            }
        }
    }

}
