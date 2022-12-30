package com.techconative.actions.utilities;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Utilities {

    public static  String getObjectNameForClassName(String className){
        return className.replaceFirst(String.valueOf(className.charAt(0)),String.valueOf(className.charAt(0)).toLowerCase());
    }

    public static void copyToClipboard(String mappings) {
        StringSelection stringSelection = new StringSelection(mappings);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }



}