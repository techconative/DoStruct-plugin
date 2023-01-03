package com.techconative.actions.utilities;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

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
        if (className.equals("className") || className.isBlank() || className.isEmpty()) {
            return "MAPPER";
        }
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = className.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String ch = String.valueOf(chars[i]);
            if (i != 0 && ch.equals(ch.toUpperCase())) {
                stringBuilder.append("_" + ch);
            } else {
                stringBuilder.append(ch.toUpperCase());
            }
        }
        return stringBuilder.toString();
    }

    public static String findAndApply(String text) {
        if (text.matches("^[a-z]+([A-Z][a-z0-9]+)+")){
            return text;
        }
        int ctr = 0 ;
        int n = text.length( ) ;
        char ch[ ] = text.toCharArray( ) ;
        int c = 0 ;
        for ( int i = 0; i < n; i++ )
        {
            if( i == 0 )
                ch[ i ] = Character.toLowerCase( ch[ i ] ) ;
            if ( ch[ i ] == ' ' )
            {
                ctr++ ;
                ch[ i + 1 ] = Character.toUpperCase( ch[ i + 1] ) ;
                continue ;
            }
            else
                ch[ c++ ] = ch[ i ] ;
        }

        return String.valueOf( ch, 0, n - ctr ).replace("_","")
                .replace("-","");

    }

    public static String apply(String text) {
        text = findAndApply(text);
        return String.valueOf(text.charAt(0)).toUpperCase() + text.replaceFirst("" + text.charAt(0), "")
                .replace("_","").replace("-","");
    }


}
