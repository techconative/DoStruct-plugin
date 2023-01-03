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
        if(className.equals("className")||className.isBlank()||className.isEmpty()) {
            return "MAPPER";
        }
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

    public static String findAndApply(String text) {
        if (text.matches("^[a-z]+([A-Z][a-z0-9]+)+"))
            return text;
        String[] words = text.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            } else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();

    }

    public static String apply(String text) {
        if (text.matches("^[a-z]+([A-Z][a-z0-9]+)+"))
            return text;
        String[] words = text.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            } else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);




        }
        return String.valueOf(builder.toString().charAt(0)).toUpperCase()+builder.deleteCharAt(0);

    }


}
