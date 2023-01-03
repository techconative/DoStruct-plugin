package com.techconative.actions.service;

import com.intellij.openapi.ui.Messages;
import com.squareup.javapoet.*;
import com.techconative.actions.utilities.Utilities;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.lang.model.element.Modifier;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

public class GenerateMappings {

    static private TypeSpec.Builder person;
    static private boolean alreadyExecuted = false;
    private static Document finalDocument;

    public static String generateMappings(String selectedText, String path, boolean generate,
                                          String className, String mapperName) throws IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Map<String, String> map = new HashMap<>();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Messages.showMessageDialog(String.valueOf(ex), "ERROR", Messages.getErrorIcon());
            return null;
        }
        finalDocument = null;
        try {
            finalDocument = dBuilder.parse(new InputSource(new StringReader(selectedText)));
        } catch (SAXException | IOException | NullPointerException ex) {
            Messages.showMessageDialog(String.valueOf(ex), "ERROR", Messages.getErrorIcon());
            return null;
        }
        finalDocument.getDocumentElement().normalize();

        int length = finalDocument.getElementsByTagName("mapping").getLength();

        if (length != finalDocument.getElementsByTagName("class-a").getLength() &&
                length != finalDocument.getElementsByTagName("class-b").getLength()) {
            Messages.showMessageDialog("Wrong xml structure", "ERROR", Messages.getErrorIcon());
            return null;
        }


        IntStream.range(0, length).forEachOrdered(x -> {
            map.clear();
            NodeList nodeList = finalDocument.getElementsByTagName("mapping").item(x).getChildNodes();
            List<AnnotationSpec> annotationSpecList = new ArrayList<>();

            IntStream.range(0, nodeList.getLength())
                    .mapToObj(nodeList::item)
                    .forEach(y -> {

                                if (y.getNodeName().equals("class-a")) {
                                    map.put("ClassAName", getClassName(y.getTextContent()));
                                    map.put("packageA", getPackage(y.getTextContent()));
                                } else if (y.getNodeName().equals("class-b")) {
                                    map.put("ClassBName", getClassName(y.getTextContent()));
                                    map.put("packageB", getPackage(y.getTextContent()));
                                } else if (y.getNodeName().equals("field")) {
                                    if (y.getAttributes().getNamedItem("map-id") != null) {
                                        map.put("methodMapId", y.getAttributes().getNamedItem("map-id").getTextContent());
                                    }
                                    Element element = (Element) y;
                                    AnnotationSpec.Builder annotationSpec = AnnotationSpec.builder(Mapping.class);
                                    annotationSpec.addMember("source", "$S",
                                            element.getElementsByTagName("a")
                                                    .item(0).getTextContent()).addMember("target", "$S",
                                            element.getElementsByTagName("b").item(0).getTextContent());
                                    annotationSpecList.add(annotationSpec.build());
                                } else if (y.getNodeName().equals("field-exclude")) {
                                    Element element = (Element) y;
                                    AnnotationSpec.Builder annotationSpec = AnnotationSpec.builder(Mapping.class);
                                    annotationSpec.addMember("target", "$S",
                                            element.getElementsByTagName("b").item(0)
                                                    .getTextContent()).addMember("ignore", "true");
                                    annotationSpecList.add(annotationSpec.build());
                                }
                            }
                    );

            buildJavaClass(path, map, className, mapperName);
            if (finalDocument.getElementsByTagName("mapping").item(x).getAttributes().getNamedItem("map-id") != null) {
                String mapId = finalDocument.getElementsByTagName("mapping").item(x).getAttributes()
                        .getNamedItem("map-id").getTextContent();
                person.addAnnotation(AnnotationSpec.builder(Named.class)
                        .addMember("value", "$S", Utilities.apply(mapId)).build());
            }
            generateMethod(map, annotationSpecList);
        });

        return generateJavaClass(path, generate);
    }


    private static String getPackage(String value) {
        String[] strings = value.split("[.]");
        return value.replace("." + strings[strings.length - 1], "").trim();
    }

    private static String getClassName(String value) {
        String[] strings = value.split("[.]");
        return strings[strings.length - 1];
    }

    static void generateMethod(Map<String, String> map, List<AnnotationSpec> annotationSpecList) {

        ClassName classTypeB = ClassName.get(map.get("packageB"), map.get("ClassBName"));
        ClassName classTypeA = ClassName.get(map.get("packageA"), map.get("ClassAName"));

        MethodSpec.Builder method = MethodSpec
                .methodBuilder("to" + map.get("ClassBName"))
                .addParameter(classTypeA, Utilities.getObjectNameForClassName(map.get("ClassAName")))
                .returns(classTypeB).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        AnnotationSpec.Builder anno = AnnotationSpec.builder(Mappings.class);
        IntStream.range(0, annotationSpecList.size()).forEachOrdered(x ->
                anno.addMember("value", "$L", annotationSpecList.get(x))
        );
        method.addAnnotation(anno.build());
        if (map.containsKey("methodMapId")) {
            method.addAnnotation(AnnotationSpec.builder(Named.class)
                    .addMember("value", "$S", Utilities.findAndApply(map.get("methodMapId"))).build());
        }
        person.addMethod(method.build());
    }

    static private void buildJavaClass(String path, Map<String, String> map, String className, String mapperName) {
        if (!alreadyExecuted) {
            if (className.equals("className") || className.isBlank() || className.isEmpty()) {
                className = map.get("ClassAName") + map.get("ClassBName");
            }
            String[] strings = getPath(path);
            ClassName ClassTitle = ClassName.get(strings[1], className);
            ClassName Mappers = ClassName.get("org.mapstruct.factory", "Mappers");
            FieldSpec fieldSpec = FieldSpec.builder(ClassTitle, mapperName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .initializer("$T.getMapper($T.class)", Mappers, ClassTitle)
                    .build();


            person = TypeSpec
                    .classBuilder(className)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(Mapper.class)
                    .addField(fieldSpec);
            alreadyExecuted = true;
        }
    }

    static private String generateJavaClass(String path, boolean generate) throws IOException {
        String[] strings = getPath(path);
        TypeSpec typeSpec = person.build();

        JavaFile javaFile = JavaFile.builder(strings[1], typeSpec).build();

        if (generate) {
            write(javaFile, strings[0]);
        }
        String code = String.valueOf(javaFile);
        alreadyExecuted = false;
        return code;
    }

    static private String[] getPath(String path) {
        String str = path.replace(path.charAt(2), '.');
        String[] strings = str.split("src.main.java.");
        strings[0] = str.replaceAll("." + strings[1], "").trim().replace('.', path.charAt(2));
        return strings;
    }

    static private void write(JavaFile javaFile, String path) throws IOException {
        javaFile.writeTo(Paths.get(path));
    }
}
