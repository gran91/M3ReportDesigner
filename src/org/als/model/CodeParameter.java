/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Jeremy.CHAUT
 */
public class CodeParameter {

    private final StringProperty parameterCode, parameterDescription;
    private final StringProperty parameterType;

    public CodeParameter(String code) {
        this(code, "");
    }

    public CodeParameter(String code, String desc) {
        this(code, desc, "");
    }

    public CodeParameter(String code, String desc, String paramtype) {
        parameterCode = new SimpleStringProperty(code);
        parameterDescription = new SimpleStringProperty(desc);
        parameterType = new SimpleStringProperty(paramtype);
    }

    public StringProperty getParameterCode() {
        return parameterCode;
    }

    public StringProperty getParameterDescription() {
        return parameterDescription;
    }

    public StringProperty getParameterType() {
        return parameterType;
    }
    
    @Override
    public String toString(){
        return parameterCode.get();
    }
}
