/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fx.custom;

import com.sun.javafx.scene.control.behavior.PasswordFieldBehavior;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author jeremy.chaut
 */
public class PasswordFieldSkin extends TextFieldSkin {

    public PasswordFieldSkin(PasswordField passwordField) {
        super(passwordField, new PasswordFieldBehavior(passwordField));
    }

    @Override
    protected String maskText(String txt) {
        TextField textField = getSkinnable();

        int n = textField.getLength();
        StringBuilder passwordBuilder = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            passwordBuilder.append(BULLET);
        }

        return passwordBuilder.toString();
    }
}
