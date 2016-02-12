/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.model;

import java.util.Arrays;
import javafx.beans.property.ObjectProperty;
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
public class Condition {

    private final StringProperty conditionCode, conditionDescription, conditionType, conditionValue, substitutionValue;
    private final ObjectProperty<Color> conditionColor;
    private final ObservableList<String> listComparator;
    private static final String[] alphaCondition = {"=", "!="};
    private static final String[] numCondition = {"=", "!=", "<", "<=", ">", ">=", "<...>"};

    public Condition(CodeParameter codeParameter) {
        this(codeParameter.getParameterCode().getValue(), codeParameter.getParameterDescription().getValue(), "");
    }

    public Condition(String code, String type) {
        this(code, type, "");
    }

    public Condition(String code, String type, String val) {
        conditionCode = new SimpleStringProperty(code.trim());
        conditionDescription = new SimpleStringProperty();
        conditionValue = new SimpleStringProperty(val);
        substitutionValue = new SimpleStringProperty();
        conditionColor = new SimpleObjectProperty<>();
        listComparator = FXCollections.observableArrayList();
        conditionType = new SimpleStringProperty();
        if (type.equals("2") || type.equals("3")) {
            listComparator.addAll(Arrays.asList(numCondition));
        } else {
            listComparator.addAll(Arrays.asList(alphaCondition));
        }
    }

    public Condition(String code, String type, String typeData, String val) {
        conditionCode = new SimpleStringProperty(code.trim());
        conditionDescription = new SimpleStringProperty();
        conditionValue = new SimpleStringProperty(val);
        substitutionValue = new SimpleStringProperty();
        conditionColor = new SimpleObjectProperty<>();
        listComparator = FXCollections.observableArrayList();
        if (type.equals("2") || type.equals("3")) {
            listComparator.addAll(Arrays.asList(numCondition));
        } else {
            listComparator.addAll(Arrays.asList(alphaCondition));    
        }
        conditionType = new SimpleStringProperty();
    }

    public StringProperty getConditionCode() {
        return conditionCode;
    }

    public StringProperty getConditionDescription() {
        return conditionDescription;
    }

    public StringProperty getConditionType() {
        return conditionType;
    }

    public StringProperty getConditionValue() {
        return conditionValue;
    }

    public ObjectProperty<Color> getConditionColor() {
        return conditionColor;
    }

    public StringProperty getSubstitutionValue() {
        return substitutionValue;
    }

    public ObservableList<String> getListComparator() {
        return listComparator;
    }
}
