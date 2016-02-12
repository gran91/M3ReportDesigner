/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.als.task;

import api.ConnexionAPI;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.als.model.CodeParameter;
import org.als.model.Division;
import org.als.model.TitleCodeParameter;
import resources.Resource;

/**
 *
 * @author Jeremy.CHAUT
 */
public class ListCodeParameter implements Callable<Void> {

    private String ip;
    private int port;
    private String user;
    private Division division;
    private TitleCodeParameter titleCode;

    public ListCodeParameter(String host, int port, String login, Division d, TitleCodeParameter title) {
        this.ip = host;
        this.port = port;
        this.user = login;
        this.division = d;
        this.titleCode = title;
    }

    @Override
    public Void call() throws Exception {
        ConnexionAPI conAPI = new ConnexionAPI(ip, port);
        if (conAPI.getResult() != -1) {
            if (conAPI.verifConnection(user, "")) {
                String in = String.format("%-15s", Resource.TRANS_PARAMCODEJ);
                in += String.format("%-3s", division.getDivi());
                in += String.format("%-10s", titleCode.getTitleCode().getValue());
                ArrayList aOut = ConnexionAPI.parseFieldData(conAPI.listField(Resource.APINAME, Resource.TRANS_PARAMCODEJ, 'O', user, ""));
                ArrayList listParamCodeJ = conAPI.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_PARAMCODEJ), in, user, "");
                Object[][] tableData = new Object[listParamCodeJ.size()][((ArrayList) aOut.get(0)).size()];
                for (int i = 0; i < listParamCodeJ.size(); i++) {
                    String code = "";
                    String desc = "";
                    String type = "";
                    for (int j = 5; j < 8; j++) {
                        try {
                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(j).toString()) - 1;
                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(j).toString());
                            if (j == 5) {
                                code = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                            } else if (j == 7) {
                                desc = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                            } else if (j == 6) {
                                type = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                            }
                        } catch (NumberFormatException ex) {
                        } catch (Exception e) {
                        }
                    }
                    CodeParameter parameterCode = new CodeParameter(code, desc, type);
                    titleCode.getListParameter().add(parameterCode);

//listAvailableValue(titleCode, parameterCode);
                }
            } else {
//throw new RuntimeException(String.format(resourceMessage.getString("api.user"), (Object) new String[]{conAPI.getUser()}));
            }
        } else {
//throw new RuntimeException(String.format(resourceMessage.getString("api.parameter")));
        }
        return null;
    }

}
