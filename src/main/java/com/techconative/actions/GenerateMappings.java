package com.techconative.actions;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import org.mapstruct.Mapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class GenerateMappings {
    static private Map<String, List<String>> nodeMap = new HashMap<>();

    static Void generateMappings(String selectedText, String path) {
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
            Utilities.copyToClipboard(" ");
            return null;
        }
        document.getDocumentElement().normalize();

        int length = document.getElementsByTagName("mapping").getLength();
        if (length != document.getElementsByTagName("class-a").getLength() && length != document.getElementsByTagName("class-b").getLength())
            return null;

        for (int i = 0; i < length; i++) {
            NodeList nodeList = document.getElementsByTagName("mapping").item(i).getChildNodes();

            IntStream.range(0, nodeList.getLength())
                    .mapToObj(nodeList::item)
                    .forEach(y -> {
                                if (y.getNodeName().equals("class-a")) {
                                    nodeMap.put("class-a", List.of(y.getTextContent()));
                                } else if (y.getNodeName().equals("class-b")) {
                                    nodeMap.put("class-b", List.of(y.getTextContent()));
                                }
                            }
                    );
            Map<String,String> map=initialize(nodeMap.get("class-b").get(0).split("[.]"),
                    nodeMap.get("class-a").get(0).split("[.]"));
            try {
                MapperClassGeneration.generate(nodeList, path,map);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private static Map<String,String> initialize(String[] ClassBArray, String[] ClassAArray) {
        Map<String,String> map=Map.of("ClassAName",ClassAArray[ClassAArray.length - 1],
                "ClassBName",ClassBArray[ClassBArray.length - 1],
                "packageA",nodeMap.get("class-a").get(0).replace("." + ClassAArray[ClassAArray.length - 1], "").trim(),
                "packageB",nodeMap.get("class-b").get(0).replace("." + ClassBArray[ClassBArray.length - 1], "").trim());

        return map;
    }


    static MethodSpec.Builder bi(MethodSpec.Builder bi, NodeList fieldList,boolean twoWay) {
        IntStream.range(0, fieldList.getLength())
                .mapToObj(fieldList::item)
                .filter(node -> node.getNodeName().equals("field"))
                .map(x -> (Element) x).forEach(y -> {
                    AnnotationSpec.Builder annotationSpec = AnnotationSpec
                            .builder(Mapping.class);
                    if (twoWay) {
                        annotationSpec.addMember("source", "$S", y.getElementsByTagName("b").item(0).getTextContent())
                                .addMember("target", "$S", y.getElementsByTagName("a").item(0).getTextContent());
                    } else {
                        annotationSpec.addMember("source", "$S", y.getElementsByTagName("a").item(0).getTextContent())
                                .addMember("target", "$S", y.getElementsByTagName("b").item(0).getTextContent());
                    }

                    bi.addAnnotation(annotationSpec.build());

                });

        IntStream.range(0, fieldList.getLength())
                .mapToObj(fieldList::item)
                .filter(node -> node.getNodeName().equals("field-exclude"))
                .map(x -> (Element) x).forEach(y -> {
                    AnnotationSpec.Builder annotationSpec = AnnotationSpec
                            .builder(Mapping.class);
                    if (twoWay) {
                        annotationSpec.addMember("target", "$S", y.getElementsByTagName("a").item(0).getTextContent())
                                .addMember("ignore", "true");
                    } else {
                        annotationSpec.addMember("target", "$S", y.getElementsByTagName("b").item(0).getTextContent())
                                .addMember("ignore", "true");
                    }
                    bi.addAnnotation(annotationSpec.build());
                });
        return bi;
    }
}
