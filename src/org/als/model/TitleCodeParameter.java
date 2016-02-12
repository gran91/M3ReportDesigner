/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.model;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

/**
 *
 * @author Jeremy.CHAUT
 */
public class TitleCodeParameter {

    private final StringProperty titleCode, titleDescription;
    private final StringProperty titleType;
    private final IntegerProperty tablePadding;
    private final IntegerProperty tableParameterColumnWidth;
    private final ObjectProperty<Color> tableLineColor;
    private final ObservableList<CodeParameter> listParameter;
    private final ObservableList<Condition> listCondition;
    private final ObservableList<AvailableValue> listAvailableValue;

    public TitleCodeParameter(String code) {
        this(code, "","0");
    }

    public TitleCodeParameter(String code, String desc, String type) {
        titleCode = new SimpleStringProperty(code);
        titleDescription = new SimpleStringProperty(desc);
        titleType = new SimpleStringProperty(type);
        tablePadding = new SimpleIntegerProperty();
        tableParameterColumnWidth = new SimpleIntegerProperty();
        tableLineColor = new SimpleObjectProperty<>();
        listParameter = FXCollections.observableArrayList();
        listCondition = FXCollections.observableArrayList();
        listAvailableValue = FXCollections.observableArrayList();
    }

    public static ObservableList<String> listType() {
        ResourceBundle resourceMessage = ResourceBundle.getBundle("resources/language", Locale.getDefault());
        ObservableList<String> listType = FXCollections.observableArrayList();
        for (int i = 0; i < 7; i++) {
            listType.add(resourceMessage.getString("parameter.type" + i));
        }
        return listType;
    }

    public StringProperty getTitleCode() {
        return titleCode;
    }

    public StringProperty getTitleDescription() {
        return titleDescription;
    }

    public StringProperty getTitleType() {
        return titleType;
    }

    public IntegerProperty getTablePadding() {
        return tablePadding;
    }

    public IntegerProperty getTableParameterColumnWidth() {
        return tableParameterColumnWidth;
    }

    public ObjectProperty<Color> getTableLineColor() {
        return tableLineColor;
    }

    public ObservableList<CodeParameter> getListParameter() {
        return listParameter;
    }

    public ObservableList<Condition> getListCondition() {
        return listCondition;
    }

    public ObservableList<AvailableValue> getListAvailableValue() {
        return listAvailableValue;
    }
}
