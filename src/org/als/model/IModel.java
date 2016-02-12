/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.model;

import java.util.ArrayList;

/**
 *
 * @author jeremy.chaut
 */
public interface IModel {
    public ArrayList<?> getData();
    public void setData(ArrayList<?> data);
    public String getModelName();
    public String getI18NName();
}
