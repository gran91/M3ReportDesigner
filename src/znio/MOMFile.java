/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package znio;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeremy.CHAUT
 */
public class MOMFile {

    public static Path createMOMFile(String p) {
        Path newFile = Paths.get(p);
        try {
            Files.deleteIfExists(newFile);
            Files.write(newFile, "//!codepage UTF8!\n//!multicolumn!".getBytes("UTF-8"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            return null;
        }
        return newFile;
    }

    public static String formatMOMParameter(ArrayList<String> par) {
        return formatMOMParameter(par.toArray(new String[par.size()]));
    }

    public static String formatMOMParameter(String[] par) {
        String s = "";
        for (Object p : par) {
            if (p.toString().contains(" ")) {
                s += "\"" + p.toString().trim() + "\"\t";
            } else {
                s += p.toString().trim() + "\t";
            }
        }
        return s;
    }

    public static boolean addLine(Path p, String[] lin) {
        return addLine(p, formatMOMParameter(lin));
    }

    public static boolean addLine(Path p, ArrayList<String> lin) {
        return addLine(p, formatMOMParameter(lin));
    }

    public static boolean addLine(Path p, String lin) {
        if (Files.exists(p)) {
            try {
                Files.write(p, ("\n" + lin).getBytes("UTF-8"), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
                return true;
            } catch (IOException ex) {
                Logger.getLogger(MOMFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public static List<String> readLines(Path p) {
        try {
            List<String> list = Files.readAllLines(p, Charset.forName("UTF-8"));
            list.remove(0);
            list.remove(0);
            return list;
        } catch (IOException ex) {
            Logger.getLogger(MOMFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static HashMap<String, String[]> readAllLine(Path p) {
        HashMap<String, String[]> map = new HashMap<>();
        try {
            List<String> list = Files.readAllLines(p, Charset.forName("UTF-8"));
            list.stream().map((list1) -> list1.split("\t")).forEach((l) -> {
                if (l.length > 1) {
                    ArrayList<String> param = new ArrayList(Arrays.asList(l));
                    param.remove(0);
                    map.put(formatMOMData(l[0]), formatMOMData(param.toArray(new String[param.size()])));
                } else {
                    map.put(l[0], null);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(MOMFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;
    }

    public static HashMap<String, String[]> extractKeyBegin(String key, HashMap<String, String[]> map) {
        HashMap<String, String[]> mapExtract = new HashMap<>();
        map.keySet().stream().filter((mapKey) -> (mapKey.startsWith(key))).forEach((mapKey) -> {
            mapExtract.put(mapKey, map.get(mapKey));
        });
        return mapExtract;
    }

    public static String readLine(Path p, int nb) {
        try {
            List<String> list = Files.readAllLines(p, Charset.forName("UTF-8"));
            if (list.size() >= nb + 2) {
                return list.get(nb + 1);
            }
        } catch (IOException ex) {
            Logger.getLogger(MOMFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static ArrayList<String> getParamFromLine(String lin) {
        ArrayList<String> l = new ArrayList<>();
        String[] t = lin.split("\t");
        for (String t1 : t) {
            l.add(t1.trim());
        }
        return l;
    }

    public static String[] formatMOMData(String[] o) {
        int cpt = 0;
        for (Object s : o) {
            o[cpt] = formatMOMData(o[cpt]);
            cpt++;
        }
        return o;
    }

    public static String formatMOMData(String s) {
        if (s.charAt(0) == '"') {
            s = s.substring(1);
            if (s.charAt(s.length() - 1) == '"') {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
}
