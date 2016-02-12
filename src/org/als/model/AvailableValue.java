/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author Jeremy.CHAUT
 */
public class AvailableValue {

    private final StringProperty parameterCode, availableTitle, availableValue;
    private final ObjectProperty<Color> availablecolor;

    public AvailableValue(String code, String title) {
        this(code, title, "", Color.BLACK);
    }

    public AvailableValue(String code, String title, String val) {
        this(code, title, val, Color.BLACK);
    }

    public AvailableValue(String code, String title, String val, Color c) {
        parameterCode = new SimpleStringProperty(code);
        availableTitle = new SimpleStringProperty(title);
        availableValue = new SimpleStringProperty(val);
        availablecolor = new SimpleObjectProperty<>(c);
    }

    public StringProperty getParameterCode() {
        return parameterCode;
    }

    public StringProperty getAvailableTitle() {
        return availableTitle;
    }

    public StringProperty getAvailableValue() {
        return availableValue;
    }

    public ObjectProperty<Color> getAvailablecolor() {
        return availablecolor;
    }

}
