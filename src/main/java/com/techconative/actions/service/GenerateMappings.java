package com.techconative.actions.service;

import com.intellij.openapi.ui.Messages;
import com.squareup.javapoet.*;
import com.techconative.actions.DozerTOMapperStructPlugin;
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
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class GenerateMappings {

    static private TypeSpec.Builder person;
    static private boolean alreadyExecuted = false;
    private static Document finalDocument;
    private static int length;


    private static Integer initializeXmlDocumentBuilder(String selectedText) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        length = 0;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Messages.showMessageDialog(String.valueOf(ex), "ERROR", Messages.getErrorIcon());
            return 0;
        }
        finalDocument = null;
        try {
            finalDocument = dBuilder.parse(new InputSource(new StringReader(selectedText)));
        } catch (SAXException | IOException | NullPointerException ex) {
            Messages.showMessageDialog(String.valueOf(ex), "ERROR", Messages.getErrorIcon());
            return 0;
        }
        finalDocument.getDocumentElement().normalize();
        return finalDocument.getElementsByTagName("mapping").getLength();
    }

    public static boolean CheckXml(String selectedText) {
        length = initializeXmlDocumentBuilder(selectedText);
        if (length == 1 && finalDocument.getElementsByTagName("mappings").getLength() == 0) {
            alreadyExecuted = true;
            return true;
        } else {
            alreadyExecuted = false;
            return false;
        }
    }

    public static String generateMappings(String path, boolean generate,
                                          String className, String mapperName) throws IOException, BadLocationException {
        if (length == 0) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        person = null;
        AtomicBoolean partialMapping = new AtomicBoolean(false);

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

                        switch (y.getNodeName()) {
                            case "class-a" -> {
                                map.put("ClassAName", getClassName(y.getTextContent()));
                                map.put("packageA", getPackage(y.getTextContent()));
                            }
                            case "class-b" -> {
                                map.put("ClassBName", getClassName(y.getTextContent()));
                                map.put("packageB", getPackage(y.getTextContent()));
                            }
                            case "field" -> {
                                Element element = (Element) y;
                                AnnotationSpec.Builder annotationSpec = AnnotationSpec.builder(Mapping.class);
                                annotationSpec.addMember("source", "$S",
                                        element.getElementsByTagName("a")
                                                .item(0).getTextContent()).addMember("target", "$S",
                                        element.getElementsByTagName("b").item(0).getTextContent());
                                if (y.getAttributes().getNamedItem("map-id") != null) {
                                    annotationSpec.addMember("qualifiedByName","$S",
                                            Utilities.findAndApply(y.getAttributes().getNamedItem("map-id")
                                                    .getTextContent()));
                                }
                                annotationSpecList.add(annotationSpec.build());
                            }
                            case "field-exclude" -> {
                                Element element = (Element) y;
                                AnnotationSpec.Builder annotationSpec = AnnotationSpec.builder(Mapping.class);
                                annotationSpec.addMember("target", "$S",
                                        element.getElementsByTagName("b").item(0)
                                                .getTextContent()).addMember("ignore", "true");
                                annotationSpecList.add(annotationSpec.build());
                            }
                        }
                            }
                    );

            buildJavaClass(path, map, className, mapperName);
            if (finalDocument.getElementsByTagName("mapping").item(x).getAttributes().getNamedItem("map-id") != null) {
                String mapId = finalDocument.getElementsByTagName("mapping").item(x).getAttributes()
                        .getNamedItem("map-id").getTextContent();
                map.put("methodMapId", mapId);
            }
            if (finalDocument.getElementsByTagName("mappings").getLength() == 0 && length >= 1 && !generate) {
                try {
                    DozerTOMapperStructPlugin.getJTextPlane(generateMethod(map, annotationSpecList, true)
                            .replaceAll("@org.mapstruct.", "@"));
                    partialMapping.set(true);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
            } else {
                generateMethod(map, annotationSpecList, false);
            }
        });
        if (partialMapping.get()) {
            alreadyExecuted = false;
            return null;
        } else {
            return generateJavaClass(path, generate);
        }
    }


    private static String getPackage(String value) {
        String[] strings = value.split("[.]");
        return value.replace("." + strings[strings.length - 1], "").trim();
    }

    private static String getClassName(String value) {
        String[] strings = value.split("[.]");
        return strings[strings.length - 1];
    }

    static String generateMethod(Map<String, String> map, List<AnnotationSpec> annotationSpecList, boolean partialMapping) {

        ClassName classTypeB = ClassName.get(map.get("packageB"), map.get("ClassBName"));
        ClassName classTypeA = ClassName.get(map.get("packageA"), map.get("ClassAName"));

        MethodSpec.Builder method = MethodSpec
                .methodBuilder("to" + map.get("ClassBName"))
                .addParameter(classTypeA, Utilities.getObjectNameForClassName(map.get("ClassAName")))
                .returns(classTypeB).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        if (!annotationSpecList.isEmpty() && annotationSpecList != null) {
            AnnotationSpec.Builder anno = AnnotationSpec.builder(Mappings.class);
            IntStream.range(0, annotationSpecList.size()).forEachOrdered(x ->
                    anno.addMember("value", "$L", annotationSpecList.get(x))
            );
            method.addAnnotation(anno.build());
        }
        if (map.containsKey("methodMapId")) {
            method.addAnnotation(AnnotationSpec.builder(Named.class)
                    .addMember("value", "$S", Utilities.findAndApply(map.get("methodMapId"))).build());
        }
        if (partialMapping) {
            return method.build().toString().replaceAll(map.get("packageB") + ".", "")
                    .replaceAll(map.get("packageA") + ".", "");
        } else {
            person.addMethod(method.build());
            return null;
        }
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

    private static String[] getPath(String path) {
        String fileSeparator = FileSystems.getDefault().getSeparator();
        String str = path.replace(fileSeparator, ".");
        String[] strings = new String[2];
        strings[1] = str.split(".src.main.java.")[1];
        strings[0] = path.replace(strings[1].replace(".", fileSeparator), "");
        return strings;
    }

    static private void write(JavaFile javaFile, String path) throws IOException {
        javaFile.writeTo(Paths.get(path));
    }
}
