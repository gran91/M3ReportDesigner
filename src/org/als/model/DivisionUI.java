package org.als.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.als.MainApp;

public class DivisionUI {

    private final StringProperty divi;
    private final ObjectProperty<Font> font;
    private final StringProperty formatFont;
    private final ObjectProperty<Color> fontColor;
    private final ObjectProperty<Color> fontColorAlertnative;
    private final IntegerProperty fontSize;
    private final IntegerProperty logoL,logoH;
    public static String[] listDivisionKey = {"FONT", "FONTSIZE", "COLOR", "ALTERCOLOR","LOGOL","LOGOH"};

    public DivisionUI(String divi,MainApp mainApp) {
        if (divi == null) {
            this.divi = new SimpleStringProperty("");
        } else {
            this.divi = new SimpleStringProperty(divi);
        }
        fontSize = new SimpleIntegerProperty(mainApp.getGeneral().getDefaultFontSize());
        formatFont = new SimpleStringProperty(mainApp.getGeneral().getDefaultFont().getName());
        fontColor = new SimpleObjectProperty<>(mainApp.getGeneral().getDefaultFontColor());
        fontColorAlertnative = new SimpleObjectProperty<>(Color.BLACK);
        font = new SimpleObjectProperty<>(new Font(mainApp.getGeneral().getDefaultFont().getName(), fontSize.get()));
        font.addListener((ObservableValue<? extends Font> observable, Font oldValue, Font newValue) -> {
            formatFont.set(newValue.getName().replaceAll(" ", "_"));
        });
        logoL = new SimpleIntegerProperty(mainApp.getGeneral().getDefaultLogoL());
        logoH = new SimpleIntegerProperty(mainApp.getGeneral().getDefaultLogoH());
    }

    public StringProperty getDivi() {
        return divi;
    }

    public ObjectProperty<Font> getFont() {
        return font;
    }

    public StringProperty getFormatFont() {
        return formatFont;
    }

    public ObjectProperty<Color> getFontColor() {
        return fontColor;
    }

    public ObjectProperty<Color> getFontColorAlertnative() {
        return fontColorAlertnative;
    }

    public IntegerProperty getFontSize() {
        return fontSize;
    }

}
