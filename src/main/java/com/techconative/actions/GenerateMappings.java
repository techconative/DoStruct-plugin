package com.techconative.actions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GenerateMappings {
    static String ClassBName;
    static String ClassAName;
    static  List<String> attributelist;
    static String mappings;
    static Void generateMappings(String selectedText) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        Document document = null;
        try {
            document = dBuilder.parse(new InputSource(new StringReader(selectedText)));
        } catch (SAXException | IOException | NullPointerException ex) {
            copyToClipboard(" ");
            return null;
        }
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();


        if (root.getNodeName().equals("mapping")) {

            attributelist = new ArrayList<>();

            /* Name of the Class on class-a tag */
            String class_a = document.getElementsByTagName("class-a").item(0).getTextContent();

            /* Name of the Class on class-b tag */
            String class_b = document.getElementsByTagName("class-b").item(0).getTextContent();

            String[] ClassBArray = class_b.split("[.]");
            String[] ClassAArray = class_a.split("[.]");
            ClassBName = ClassBArray[ClassBArray.length - 1];
            ClassAName = ClassAArray[ClassAArray.length - 1];
            String str5 = String.valueOf(ClassAName.charAt(0)).toLowerCase() + ClassAName.substring(1);

            /* Mapping for field tags */
            NodeList fieldList = document.getElementsByTagName("field");

            IntStream.range(0, fieldList.getLength())
                    .mapToObj(fieldList::item)
                    .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                    .map(x -> (Element) x).forEach(y ->
                            attributelist.add("@Mapping(source=\"" + y.getElementsByTagName("a").item(0).getTextContent()
                                    + "\",target = \"" + y.getElementsByTagName("b").item(0).getTextContent() + "\")")
                    );

            /* Mapping for field-exclude tags */
            NodeList excludeList = document.getElementsByTagName("field-exclude");

            IntStream.range(0, excludeList.getLength())
                    .mapToObj(excludeList::item)
                    .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                    .map(x -> (Element) x).forEach(y ->
                            attributelist.add("@Mapping(target = \"" + y.getElementsByTagName("b").item(0).getTextContent()
                                    + "\", ignore = true)"));

            /* remove line separator for last element */
            attributelist.set(attributelist.size() - 1, attributelist.get(attributelist.size() - 1)
                    .replace("[/t]", ""));

             mappings = "@Mappings({" + String.join("," + System.lineSeparator(), attributelist) + "})"
                    + System.lineSeparator() + "public abstract " + ClassBName
                    + String.format(" to%s(%s %s", ClassBName, ClassAName, str5) + ");";

            copyToClipboard(mappings);
        }
        return null;
    }
    static void copyToClipboard(String mappings) {
        StringSelection stringSelection = new StringSelection(mappings);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
