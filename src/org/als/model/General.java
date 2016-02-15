package org.als.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.als.jaxb.adapter.ColorAdapter;
import org.als.jaxb.adapter.FontAdapter;

public class General {

    private final ObjectProperty<Font> defaultFont;
    private final ObjectProperty<Color> defaultFontColor;
    private final IntegerProperty defaultFontSize;
    private final StringProperty defaultText;
    private final LongProperty timeout;
    private final IntegerProperty defaultLogoL, defaultLogoH;

    public General() {
        defaultFont = new SimpleObjectProperty<>();
        defaultFontColor = new SimpleObjectProperty<>();
        defaultFontSize = new SimpleIntegerProperty();
        defaultText = new SimpleStringProperty();
        timeout = new SimpleLongProperty();
        defaultLogoL = new SimpleIntegerProperty();
        defaultLogoH = new SimpleIntegerProperty();
    }

    public static General getDefault() {
        General general = new General();
        general.getDefaultFontProperty().set(new Font("Arial", 12));
        general.getDefaultFontColorProperty().set(Color.BLACK);
        general.getDefaultFontSizeProperty().set(12);
        general.getDefaultTextProperty().set("Hello the world!!!");
        general.getTimeoutProperty().set(60000);
        return general;
    }

    public ObjectProperty<Font> getDefaultFontProperty() {
        return defaultFont;
    }

    public ObjectProperty<Color> getDefaultFontColorProperty() {
        return defaultFontColor;
    }

    public IntegerProperty getDefaultFontSizeProperty() {
        return defaultFontSize;
    }
    
     public IntegerProperty getDefaultLogoLProperty() {
        return defaultLogoL;
    }
     
     public IntegerProperty getDefaultLogoHProperty() {
        return defaultLogoH;
    }

    public StringProperty getDefaultTextProperty() {
        return defaultText;
    }

    public LongProperty getTimeoutProperty() {
        return timeout;
    }

    @XmlJavaTypeAdapter(FontAdapter.class)
    public Font getDefaultFont() {
        return defaultFont.get();
    }

    @XmlJavaTypeAdapter(ColorAdapter.class)
    public Color getDefaultFontColor() {
        return defaultFontColor.get();
    }

    public int getDefaultFontSize() {
        return defaultFontSize.get();
    }
    
    public int getDefaultLogoL() {
        return defaultLogoL.get();
    }
    
    public int getDefaultLogoH() {
        return defaultLogoH.get();
    }

    public String getDefaultText() {
        return defaultText.get();
    }

    public long getTimeout() {
        return timeout.get();
    }

    public void setDefaultFont(Font font) {
        defaultFont.set(font);
    }

    public void setDefaultFontColor(Color color) {
        defaultFontColor.set(color);
    }

    public void setDefaultFontSize(int size) {
        defaultFontSize.set(size);
    }
    
    public void setDefaultLogoL(int size) {
        defaultLogoL.set(size);
    }
    
    public void setDefaultLogoH(int size) {
        defaultLogoH.set(size);
    }
    
    public void setDefaultText(String text) {
        defaultText.set(text);
    }

    public void setTimeout(long time) {
        timeout.set(time);
    }
}
