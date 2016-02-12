/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fx.custom;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 *
 * @author JCHAUT
 */
public class TextFieldValidator {

    public static final Pattern emailPattern = Pattern.compile("^[\\w!#$%&?*+/=?`{|}~^-]+(?:\\.[\\w!#$%&?*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    public static final Pattern zipPattern = Pattern.compile("[0-9]{5}");
    public static final Pattern ipPattern = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$");
    public static final Pattern filePathPattern = Pattern.compile("^(([a-zA-Z]:|\\\\)\\\\)?(((\\.)|(\\.\\.)|([^\\\\/:\\*\\?\"\\|<>\\. ](([^\\\\/:\\*\\?\"\\|<>\\. ])|([^\\\\/:\\*\\?\"\\|<>]*[^\\\\/:\\*\\?\"\\|<>\\. ]))?))\\\\)*[^\\\\/:\\*\\?\"\\|<>\\. ](([^\\\\/:\\*\\?\"\\|<>\\. ])|([^\\\\/:\\*\\?\"\\|<>]*[^\\\\/:\\*\\?\"\\|<>\\. ]))?$");
    public static final Pattern directoryPathPattern = Pattern.compile("([A-Z]:\\\\[^/:\\*;\\/\\:\\?<>\\|]+)|(\\\\{2}[^/:\\*;\\/\\:\\?<>\\|]+)");
    public static final Pattern hostnamePattern = Pattern.compile("^(?=.{1,255}$)[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?(?:\\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?)*\\.?$");
    public static final Pattern allPortNumberPattern = Pattern.compile("^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$");
    public static final Pattern diviPattern = Pattern.compile("[A-Z0-9]{3}");
    public static final Pattern ornoPattern = Pattern.compile("[0-9]{10}");
    public static final Pattern jobnPattern = Pattern.compile("[0-9]{3}");

    public static BooleanBinding emptyTextFieldBinding(TextField textField, String message, Map<BooleanBinding, String> messages) {
        BooleanBinding binding = Bindings.createBooleanBinding(()
                -> textField.getText().trim().isEmpty(), textField.textProperty());
        configureTextFieldBinding(binding, textField, message, messages);
        return binding;
    }

    public static BooleanBinding patternTextFieldBinding(TextField textField, Pattern pattern, String message, Map<BooleanBinding, String> messages) {
        BooleanBinding binding = Bindings.createBooleanBinding(()
                -> !pattern.matcher(textField.getText()).matches(), textField.textProperty());
        configureTextFieldBinding(binding, textField, message, messages);
        return binding;
    }

    public static void configureTextFieldBinding(BooleanBinding binding, TextField textField, String message, Map<BooleanBinding, String> messages) {
        messages.put(binding, message);
        if (textField.getTooltip() == null) {
            textField.setTooltip(new Tooltip());
        }
        String tooltipText = textField.getTooltip().getText();
        binding.addListener((obs, oldValue, newValue) -> {
            updateTextFieldValidationStatus(textField, tooltipText, newValue, message);
        });
        updateTextFieldValidationStatus(textField, tooltipText, binding.get(), message);
    }

    public static BooleanBinding any(BooleanBinding[] bindings) {
        return Bindings.createBooleanBinding(()
                -> Arrays.stream(bindings).anyMatch(BooleanBinding::get), bindings);
    }

    public static LongBinding count(BooleanBinding[] bindings) {
        return Bindings.createLongBinding(()
                -> Arrays.stream(bindings).filter(BooleanBinding::get).collect(Collectors.counting()), bindings);
    }

    public static void updateTextFieldValidationStatus(TextField textField,
            String defaultTooltipText, boolean invalid, String message) {
        textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("validation-error"), invalid);
        String tooltipText;
        if (invalid) {
            tooltipText = message;
        } else {
            tooltipText = defaultTooltipText;
        }
        if (tooltipText == null || tooltipText.isEmpty()) {
            textField.setTooltip(null);
        } else {
            Tooltip tooltip = textField.getTooltip();
            if (tooltip == null) {
                textField.setTooltip(new Tooltip(tooltipText));
            } else {
                tooltip.setText(tooltipText);
            }
        }
    }

    public static void bindMessageLabels(BooleanBinding[] validationBindings, List<Node> labelList, Map<BooleanBinding, String> messages) {
        ListBinding<Node> nodeListBinding = new ListBinding<Node>() {

            {
// calling bind(...) here won't work, neither will using WeakInvalidationListeners. Not sure why....
                InvalidationListener invalidationListener = obs -> invalidate();
                Arrays.stream(validationBindings).forEach(binding
                        -> binding.addListener(invalidationListener));
            }

            @Override
            protected ObservableList<Node> computeValue() {
                return FXCollections.observableList(
                        Arrays.stream(validationBindings)
                        .filter(BooleanBinding::get)
                        .map(messages::get).map(Label::new)
                        .collect(Collectors.toList())
                );
            }
        };

        Bindings.bindContent(labelList, nodeListBinding);
    }

    public static StringProperty bindMessageLabels(BooleanBinding[] validationBindings, Map<BooleanBinding, String> messages) {
        StringProperty string = new SimpleStringProperty();
        ListBinding<Node> nodeListBinding = new ListBinding<Node>() {

            {
// calling bind(...) here won't work, neither will using WeakInvalidationListeners. Not sure why....
                InvalidationListener invalidationListener = obs -> invalidate();
                Arrays.stream(validationBindings).forEach(binding
                        -> binding.addListener(invalidationListener));
            }

            @Override
            protected ObservableList<Node> computeValue() {
                return FXCollections.observableList(
                        Arrays.stream(validationBindings)
                        .filter(BooleanBinding::get)
                        .map(messages::get).map(Label::new)
                        .collect(Collectors.toList())
                );
            }
        };
        return string;
//Bindings.bindContent(string, nodeListBinding);
    }
}
