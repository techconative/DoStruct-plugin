package com.techconative.actions.generators;

import com.intellij.openapi.ui.Messages;
import com.squareup.javapoet.*;
import com.techconative.actions.DozerTOMapperStructPlugin;
import com.techconative.actions.utilities.Utilities;
import org.apache.commons.collections.CollectionUtils;
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

    private static Document initializeXmlDocumentBuilder(String selectedText) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;

        try {
            documentBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Messages.showMessageDialog(String.valueOf(ex), "ERROR", Messages.getErrorIcon());
            throw new RuntimeException(ex);
        }
        Document finalDocument = null;
        try {
            finalDocument = documentBuilder.parse(new InputSource(new StringReader(selectedText)));
        } catch (SAXException | IOException | NullPointerException ex) {
            Messages.showMessageDialog(String.valueOf(ex), "ERROR", Messages.getErrorIcon());
            throw new RuntimeException(ex);
        }
        finalDocument.getDocumentElement().normalize();
        return finalDocument;
    }

    public static Document getDocument(String selectedText) {
        return initializeXmlDocumentBuilder(selectedText);
    }

    public static boolean checkXml(Document finalDocument) {
        int length = finalDocument.getElementsByTagName("mapping").getLength();
        return length == 1;
    }

    public static String generateMappings(Document finalDocument, String path, boolean generate,
                                          String className, String mapperName) throws IOException, BadLocationException {

        Map<String, String> map = new HashMap<>();
        AtomicBoolean partialMapping = new AtomicBoolean(false);
        AtomicBoolean alreadyExecuted = new AtomicBoolean(false);
        int length = finalDocument.getElementsByTagName("mapping").getLength();

        var builder = new Object() {
            TypeSpec.Builder builder = null;
        };

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
                                            annotationSpec.addMember("qualifiedByName", "$S",
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
            if (!alreadyExecuted.get() && path != null) {
                builder.builder = buildJavaClass(path, map, className, mapperName);
                alreadyExecuted.set(true);
            }
            if (finalDocument.getElementsByTagName("mapping").item(x).getAttributes().getNamedItem("map-id") != null) {
                String mapId = finalDocument.getElementsByTagName("mapping").item(x).getAttributes()
                        .getNamedItem("map-id").getTextContent();
                map.put("methodMapId", mapId);
            }
            if (finalDocument.getElementsByTagName("mappings").getLength() == 0 && length >= 1 && !generate) {
                String code = generateMethod(map, annotationSpecList, true, builder.builder)
                        .replaceAll("@org.mapstruct.", "@");
                map.put("code", code);
                partialMapping.set(true);

            } else {
                generateMethod(map, annotationSpecList, false, builder.builder);
            }
        });
        if (partialMapping.get()) {
            return map.get("code");
        } else {
            return generateJavaClass(path, generate, builder.builder);
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

    static String generateMethod(Map<String, String> map, List<AnnotationSpec> annotationSpecList,
                                 boolean partialMapping, TypeSpec.Builder builder) {

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
            builder.addMethod(method.build());
            return null;
        }
    }

    static private TypeSpec.Builder buildJavaClass(String path, Map<String, String> map, String className, String mapperName) {

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


        return TypeSpec
                .classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(Mapper.class)
                .addField(fieldSpec);

    }

    static private String generateJavaClass(String path, boolean generate, TypeSpec.Builder builder) throws IOException {
        String[] strings = getPath(path);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(strings[1], typeSpec).build();

        if (generate) {
            write(javaFile, strings[0]);
        }
        String code = String.valueOf(javaFile);
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
