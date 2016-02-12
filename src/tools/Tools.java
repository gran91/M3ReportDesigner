/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.awt.Toolkit;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author chautj
 */
public class Tools {

    public static Object[][] addDataToObject(Object[][] o, ArrayList<String> data, int n) {
        if (o[0].length >= data.size()) {
            for (int i = 0; i < data.size(); i++) {
                o[n][i] = data.get(i);
            }
        }
        return o;
    }

    public static boolean containsDuplicate(Object[] a) {
        return containsDuplicate(convertArrayToArrayList(a, null));
    }

    public static boolean containsDuplicate(ArrayList a) {
        ArrayList b = new ArrayList();
        for (int i = 0; i < a.size(); i++) {
            if (b.contains(a.get(i))) {
                return true;
            }
            b.add(a.get(i));
        }
        return false;
    }

    public static List<Object> deleteDuplicate(Object[] a) {
        return deleteDuplicate(Arrays.asList(a));
    }

    public static List<Object> deleteDuplicate(List<Object> a) {
        List<Object> temp = new ArrayList<>();
        for (Object o : a) {
            if (!temp.contains(o)) {
                temp.add(o);
            }
        }
        return temp;
    }

    public static Object[] convertArrayListToArray(ArrayList a, Object[] o) {
        int n = 0;
        if (o == null) {
            o = new Object[a.size()];
        } else if (a == null) {
            return o;
        } else {
            n = o.length;
            Object[] o1 = o;
            o = new Object[o.length + a.size()];
            for (int i = 0; i < o1.length; i++) {
                o[i] = o1[i];
                n = i + 1;
            }

        }
        for (int i = 0; i < a.size(); i++) {
            o[i + n] = a.get(i).toString();
        }
        return o;
    }

    public static ArrayList convertArrayToArrayList(Object[] o, ArrayList a) {
        int n = 0;
        if (a == null) {
            a = new ArrayList();
        }
        a.addAll(Arrays.asList(o));
        return a;
    }

    public static Object[] convertMObjectToSObject(Object[][] o, int n) {
        if (o == null || o.length == 0) {
            return null;
        }
        Object[] o1 = new Object[o.length];
        for (int i = 0; i < o1.length; i++) {
            o1[i] = o[i][n];
        }
        return o1;
    }

    public static Object[][] convertSObjectToMObject(Object[] o) {
        if (o == null || o.length == 0) {
            return null;
        }
        Object[][] o1 = new Object[o.length][1];
        for (int i = 0; i < o.length; i++) {
            o1[i][0] = o[i];
        }
        return o1;
    }

    public static boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static int convertToInt(boolean s) {
        if (s) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int convertToInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static double convertToDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static boolean convertToBoolean(int n) {
        return (n == 1) ? true : false;
    }

    public static boolean convertToBoolean(String n) {
        return convertToBoolean(convertToInt(n));
    }

    public static int convertBooleanToInt(boolean b) {
        return (b) ? 1 : 0;
    }

    public static Object[] addObjectToObject(Object[] o1, Object[] o2) {
        int n = 0;
        if (o1 == null && o2 != null) {
            return o2;
        } else if (o1 != null && o2 == null) {
            return o1;
        } else if (o1 == null && o2 == null) {
            return null;
        } else {
            Object[] o = new Object[o1.length + o2.length];
            for (int i = 0; i < o1.length; i++) {
                o[i] = o1[i];
                n = i + 1;
            }
            for (int i = 0; i < o2.length; i++) {
                o[i + n] = o2[i];
            }
            return o;
        }
    }

    public static String[] ObjectToString(Object[] o) {
        String[] s = new String[o.length];
        for (int i = 0; i < o.length; i++) {
            s[i] = o[i].toString();
        }
        return s;
    }

    public static String[][] ObjectToString(Object[][] o) {
        String[][] s = new String[o.length][o[0].length];
        for (int i = 0; i < o.length; i++) {
            for (int j = 0; j < o.length; j++) {
                s[i][j] = o[i][j].toString();
            }
        }
        return s;
    }

    public static ArrayList concatArrayList(ArrayList a1, ArrayList a2) {
        ArrayList a = new ArrayList();
        if (a1 != null) {
            for (int i = 0; i < a1.size(); i++) {
                a.add(a1.get(i));
            }
        }
        if (a2 != null) {
            for (int i = 0; i < a2.size(); i++) {
                a.add(a2.get(i));
            }
        }
        return a;
    }

    public static Object[] extractColumn(Object[][] data, int col) {
        if (data != null && data.length > 0) {
            if (data[0].length > col) {
                Object[] o = new Object[data.length];
                for (int i = 0; i < data.length; i++) {
                    o[i] = data[i][col];
                }
                return o;
            }
        }
        return null;
    }

    public static Object[] extractPart(Object[] data, int beg, int end) {
        if (data != null && data.length > end) {
            Object[] o = new Object[end - beg + 1];
            for (int i = beg; i <= end; i++) {
                o[i - beg] = data[i];
            }
            return o;
        }
        return null;
    }

    public static List extractDifference(List a1, List a2) {
        List amin;
        List amax;
        List aDiff = new ArrayList();
        if (a1.size() > a2.size()) {
            amin = a2;
            amax = a1;
        } else {
            amin = a1;
            amax = a2;
        }
        for (Object amax1 : amax) {
            if (!amin.contains(amax1)) {
                aDiff.add(amax1);
            }
        }
        return aDiff;
    }

    public static List[] extractAllDifference(List a1, List a2) {
        List a1Diff = new ArrayList();
        List a2Diff = new ArrayList();

        for (Object a11 : a1) {
            if (!a2.contains(a11)) {
                a1Diff.add(a11);
            }
        }

        for (Object a21 : a2) {
            if (!a1.contains(a21)) {
                a2Diff.add(a21);
            }
        }
        return new List[]{a1Diff, a2Diff};
    }

    public static int getIndexFromId(Object o[], String s) {
        for (int i = 0; i < o.length; i++) {
            if (String.valueOf(o[i]).equals(s)) {
                return i;
            }
        }
        return 0;
    }

    public static int getIdFromIndex(Object o[], int n) {
        if (o.length > n) {
            return new Integer(String.valueOf(o[n]));
        }
        return 0;
    }

    public static ImageIcon readImageIcon(String filename) {
        URL url = Tools.class.getResource("img/" + filename);
        if (url == null) {
            return null;
        }

        return new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
    }

    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            bytes = bos.toByteArray();
        } catch (IOException ex) {
//TODO: Handle the exception
        }
        return bytes;
    }

    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (IOException ex) {
//TODO: Handle the exception
        } catch (ClassNotFoundException ex) {
//TODO: Handle the exception
        }
        return obj;
    }
}
