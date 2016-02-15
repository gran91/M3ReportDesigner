/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import javafx.scene.image.Image;

/**
 *
 * @author Jérémy Chaut
 */
public class Resource {

    public static final String TITLE = "M3ReportDesigner";
    public static final String VERSION = "2.0.7";
    public static final Image LOGO_ICON = new Image(Resource.class.getResourceAsStream("/resources/images/logo.png"));
    public static final String pathMOMTable = System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + "tables";
    public static final String pathMOMFont = System.getProperty("file.separator") + "data" + System.getProperty("file.separator") + "fonts";
    public static final String baseFileName = "ALZ613";
    public static final String extFile = ".tbl";

    public static String APINAME = "ALZ613MI";
    public static String TRANS_CODEJ = "ListCodeJ";
    public static String TRANS_TITLECODEJ = "ListTitleCodeJ";
    public static String TRANS_PARAMCODEJ = "ListParmTitle";
    public static String TRANS_VALUEENTITY = "ListValueEntity";
    public static String TRANS_CR = "EditCR";

    public static String CODE = "code";
    public static String FONT = "font";
    public static String TITLE_FONT_COLOR = "titlefont";
    public static String TITLE_BORDER_COLOR = "titleborder";
    public static String TITLE_FRAME_COLOR = "titleframe";
    public static String TITLE_TYPE = "titletype";
    public static String SHAPE_FRAME_COLOR = "shapeframe";
    public static String SHAPE_BORDER_COLOR = "shapeborder";
    public static String PERSON = "person";
    public static String SIZE = "size";
    public static String SHAPE = "shape";
    public static String MAIN_COLOR = "main";
    public static String ALTERNATIVE_COLOR = "alternative";
}
