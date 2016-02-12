/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.als.MainApp;

/**
 *
 * @author jeremy.chaut
 */
public abstract class AbstractModel implements IModel, Cloneable{

    protected String nameModel="";
    protected String i18nID="";
    private Locale locale;
    private Properties configProp;
    private ResourceBundle resourceMessage;
    
    public AbstractModel(){
    configProp = new Properties();
        FileInputStream in;
        try {
            File f = new File(MainApp.configFileName);
            if (f.exists() && f.isFile()) {
                in = new FileInputStream(MainApp.configFileName);
                configProp.load(in);
                in.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        locale = Locale.getDefault();
        if (configProp.contains("lang")) {
            if (!configProp.get("lang").toString().trim().isEmpty()) {
                locale = new Locale(System.getProperty(configProp.get("lang").toString().trim()));
            }
        }
        resourceMessage = ResourceBundle.getBundle("ressources/language", locale);
    }
    
    @Override
    public String getModelName() {
        return nameModel;
    }

    @Override
    public String getI18NName() {
     
        String s=resourceMessage.getString(i18nID);
        if(s.isEmpty())return nameModel; else return s;
    }
    
}
