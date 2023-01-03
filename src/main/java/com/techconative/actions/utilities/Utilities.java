package com.techconative.actions.utilities;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    public static String getObjectNameForClassName(String className) {
        return className.replaceFirst(String.valueOf(className.charAt(0)),
                String.valueOf(className.charAt(0)).toLowerCase());
    }

    public static void copyToClipboard(String mappings) {
        StringSelection stringSelection = new StringSelection(mappings);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static String GetVariableNameFromClassName(String className) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(className.charAt(0)).toUpperCase());
        String finalClassName = className.substring(1);
        Arrays.stream(className.split("[A-Z]"))
                .forEach(y -> {
                    if (finalClassName.indexOf(y) > 0)
                        stringBuilder.append(String.format("_%c%s",
                                finalClassName.charAt(finalClassName.indexOf(y) - 1), y));
                    else
                        stringBuilder.append(String.format("%s", y));
                });

        return stringBuilder.toString().toUpperCase();

    }

    public static String findAndApply(String src, String pattern,
                                    Function<String, String> txr) {
        Matcher m = Pattern.compile(pattern).matcher(src);

        StringBuilder sb = new StringBuilder();
        int last = 0;

        while (m.find()) {
            sb.append(src.substring(last, m.start()));
            sb.append(txr.apply(m.group(0)));
            last = m.end();
        }
        sb.append(src.substring(last));
      return sb.toString();

    }


}
