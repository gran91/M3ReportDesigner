package org.als.model;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class PrestationUI {

    private final ObjectProperty<Prestation> prestation;
    private final BooleanProperty doctor, prescriberInit, prescriber, treatDoctor, technician, technicianTel, technicianAgency, nurse;
    private final IntegerProperty titleShape, titleFontSize, blockShape, logoH, logoL;
    private final ObjectProperty<Color> fontTitleColor, foregroundTitleColor, borderTitleColor;
    private final ObjectProperty<Color> foregroundBlockColor, borderBlockColor;
//private final ObjectProperty<ObservableList<TitleCodeParameter>> listTitleParameter;
    private final ObservableList<TitleCodeParameter> observableListTitleParameter = FXCollections.observableArrayList();
    private final ResourceBundle resourceMessage = ResourceBundle.getBundle("resources/language", Locale.getDefault());
    public static String[] listKeyPrestation = {"TITLE_TYPE", "TITLE_FONT_SIZE", "TITLE_FONT_COLOR", "TITLE_FRAME_COLOR", "TITLE_BORDER_COLOR", "SHAPE_TYPE", "SHAPE_FRAME_COLOR", "SHAPE_BORDER_COLOR", "DOCTOR", "BEGINPRESCRIBER", "PRESCRIBER", "TREATDOCTOR", "TECHNICIAN", "TECHNICIAN_TEL", "TECHNICIAN_AGENCY", "NURSE", "PRESTLOGOL", "PRESTLOGOH"};
    public ObservableList<String> listShape = FXCollections.observableArrayList();
    public static int rectangleCorner = 0;
    public static int roundRectangleCorner = 50;

    public PrestationUI(Prestation prestation) {
        this.prestation = new SimpleObjectProperty<>(prestation);
        listShape.add(resourceMessage.getString("shape.none"));
        listShape.add(resourceMessage.getString("shape.rectangle"));
        listShape.add(resourceMessage.getString("shape.roundedRectangle"));
        doctor = new SimpleBooleanProperty();
        prescriberInit = new SimpleBooleanProperty();
        prescriber = new SimpleBooleanProperty();
        treatDoctor = new SimpleBooleanProperty();
        technician = new SimpleBooleanProperty();
        technicianTel = new SimpleBooleanProperty();
        technicianAgency = new SimpleBooleanProperty();
        nurse = new SimpleBooleanProperty();
        titleShape = new SimpleIntegerProperty();
        titleFontSize = new SimpleIntegerProperty();
        fontTitleColor = new SimpleObjectProperty<>();
        foregroundTitleColor = new SimpleObjectProperty<>();
        borderTitleColor = new SimpleObjectProperty<>();
        /*        listTitleParameter = new SimpleObjectProperty<>();
         listTitleParameter.setValue(observableListTitleParameter);*/
        blockShape = new SimpleIntegerProperty();
        foregroundBlockColor = new SimpleObjectProperty<>();
        borderBlockColor = new SimpleObjectProperty<>();
        logoL = new SimpleIntegerProperty();
        logoH = new SimpleIntegerProperty();
    }

    public ObjectProperty<Prestation> getPrestation() {
        return prestation;
    }

    public BooleanProperty getDoctor() {
        return doctor;
    }

    public BooleanProperty getPrescriberInit() {
        return prescriberInit;
    }

    public BooleanProperty getPrescriber() {
        return prescriber;
    }

    public BooleanProperty getTreatDoctor() {
        return treatDoctor;
    }

    public BooleanProperty getTechnician() {
        return technician;
    }

    public BooleanProperty getTechnicianTel() {
        return technicianTel;
    }

    public BooleanProperty getTechnicianAgency() {
        return technicianAgency;
    }

    public BooleanProperty getNurse() {
        return nurse;
    }

    public IntegerProperty getTitleShape() {
        return titleShape;
    }

    public IntegerProperty getLogoL() {
        return logoL;
    }

    public IntegerProperty getLogoH() {
        return logoH;
    }

    public IntegerProperty getTitleFontSize() {
        return titleFontSize;
    }

    public ObjectProperty<Color> getFontTitleColor() {
        return fontTitleColor;
    }

    public ObjectProperty<Color> getForegroundTitleColor() {
        return foregroundTitleColor;
    }

    public ObjectProperty<Color> getBorderTitleColor() {
        return borderTitleColor;
    }

    public IntegerProperty getBlockShape() {
        return blockShape;
    }

    public ObjectProperty<Color> getForegroundBlockColor() {
        return foregroundBlockColor;
    }

    public ObjectProperty<Color> getBorderBlockColor() {
        return borderBlockColor;
    }

    /*public ObjectProperty<ObservableList<TitleCodeParameter>> getListTitleParameter() {
     return listTitleParameter;
     }

     */
    public ObservableList<TitleCodeParameter> getObservableListTitleParameter() {
        return observableListTitleParameter;
    }

    public void addTitleCodeParameter(String code) {
        addTitleCodeParameter(code, "");
    }

    public void addTitleCodeParameter(String code, String desc) {
        observableListTitleParameter.add(new TitleCodeParameter(code, desc, "0"));
    }

    public void addTitleCodeParameter(String code, String desc, String type) {
        TitleCodeParameter titlecode = new TitleCodeParameter(code, desc, type);
        titlecode.getTitleType().setValue(type);
        observableListTitleParameter.add(titlecode);
    }

}
