/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import javafx.scene.paint.Color;

/**
 *
 * @author Jeremy.CHAUT
 */
public class ColorFontTools {

    public static String getRGB(Color c) {
        if (c == null) {
            c = Color.BLACK;
        }
        return new Double(c.getRed() * 255).intValue() + "," + new Double(c.getGreen() * 255).intValue() + "," + new Double(c.getBlue() * 255).intValue();
    }

    public static Color getColor(String rgb) {
        if (rgb != null) {
            String[] t = rgb.split(",");
            if (t.length == 3) {
                try {
                    int r = Integer.parseInt(t[0]);
                    int g = Integer.parseInt(t[1]);
                    int b = Integer.parseInt(t[2]);
                    return Color.rgb(r, g, b);
                } catch (NumberFormatException e) {
                }
            }
        }
        return null;
    }

}
