/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.converter;

import javafx.util.StringConverter;

/**
 *
 * @author Jeremy.CHAUT
 */
public class FontSizeConverter extends StringConverter<Integer> {

    @Override
    public String toString(Integer object) {
        if (object == null) {
            return "12";
        }
        return object.toString();
    }

    @Override
    public Integer fromString(String string) {
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException e) {
            return 12;
        }
    }

}
