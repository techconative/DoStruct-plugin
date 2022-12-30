package com.techconative.actions.service;

import com.squareup.javapoet.*;
import com.techconative.actions.utilities.Utilities;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    public static String generateMappings(String selectedText, String path, boolean generate) throws IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Map<String,String> map=new HashMap<>();
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

        if (length != document.getElementsByTagName("class-a").getLength() &&
                length != document.getElementsByTagName("class-b").getLength()){
            return null;
        }

        for (int i = 0; i < length; i++) {
            map.clear();
            NodeList nodeList = document.getElementsByTagName("mapping").item(i).getChildNodes();
            List<AnnotationSpec.Builder> annotationSpecList = new ArrayList<>();

            IntStream.range(0, nodeList.getLength())
                    .mapToObj(nodeList::item)
                    .forEach(y -> {

                                if (y.getNodeName().equals("class-a")) {
                                    map.put("ClassAName",getClassName(y.getTextContent()));
                                    map.put("packageA",getPackage(y.getTextContent()));
                                } else if (y.getNodeName().equals("class-b")) {
                                    map.put("ClassBName",getClassName(y.getTextContent()));
                                    map.put("packageB",getPackage(y.getTextContent()));
                                } else if(y.getNodeName().equals("field")){
                                    Element element=(Element) y;
                                    AnnotationSpec.Builder annotationSpec = AnnotationSpec.builder(Mapping.class);
                                    annotationSpec.addMember("source", "$S", element.getElementsByTagName("a")
                                                    .item(0).getTextContent()).addMember("target", "$S",
                                            element.getElementsByTagName("b").item(0).getTextContent());
                                    annotationSpecList.add(annotationSpec);
                                }
                                else if(y.getNodeName().equals("field-exclude")){
                                    Element  element=(Element) y;
                                    AnnotationSpec.Builder annotationSpec = AnnotationSpec.builder(Mapping.class);
                                    annotationSpec.addMember("target", "$S", element.getElementsByTagName("b").item(0)
                                            .getTextContent()).addMember("ignore", "true");
                                    annotationSpecList.add(annotationSpec);
                                }
                            }
                    );

            buildJavaClass(path,map);
            generateMethod(map,annotationSpecList);
        }
        return generateJavaClass(path,generate);
    }


    private  static String  getPackage(String value){
        String[] strings=value.split("[.]");
        return value.replace("." + strings[strings.length - 1], "").trim();
    }
    private  static String  getClassName(String value){
        String[] strings=value.split("[.]");
        return strings[strings.length - 1];
    }

    static void generateMethod(Map<String,String> map,List<AnnotationSpec.Builder> annotationSpecList){

        ClassName classTypeB = ClassName.get(map.get("packageA"), map.get("ClassBName"));
        ClassName classTypeA=ClassName.get(map.get("packageB"), map.get("ClassAName"));

        MethodSpec.Builder method = MethodSpec
                .methodBuilder("to" + map.get("ClassBName"))
                .addParameter(classTypeA,Utilities.getObjectNameForClassName(map.get("ClassAName")))
                .returns(classTypeB).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        annotationSpecList.forEach(x-> method.addAnnotation(x.build()));
        person.addMethod(method.build());
    }

    static private void buildJavaClass(String path, Map<String,String> map){
        if(!alreadyExecuted){
        String className=map.get("ClassAName")+map.get("ClassBName")+"Mapper";

        String[] strings=getPath(path);
        ClassName ClassTitle = ClassName.get(strings[1], className);
        ClassName Mappers = ClassName.get("org.mapstruct.factory", "Mappers");
        FieldSpec android = FieldSpec.builder(ClassTitle, "person"+"Mapper")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer( "$T.getMapper($T.class)",Mappers,ClassTitle)
                .build();

        person = TypeSpec
                .classBuilder(className)
                .addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT)
                .addAnnotation(Mapper.class)
                .addField(android);
        alreadyExecuted=true;
        }
    }

    static private String generateJavaClass(String path, boolean generate) throws IOException {
        String[] strings=getPath(path);
        TypeSpec typeSpec=person.build();

        JavaFile javaFile = JavaFile.builder(strings[1],typeSpec).build();
        System.out.println(javaFile);

        if(generate){
            write(javaFile,strings[0]);
        }
       String code= String.valueOf(javaFile);
        alreadyExecuted=false;
        return code;
    }
    static private String[] getPath(String path) {
        String str=path.replace(path.charAt(2),'.');
        String[] strings=str.split("src.main.java.");
        strings[0]=str.replaceAll("."+strings[1],"").trim().replace('.',path.charAt(2));
        return strings;
    }

    static private void write(JavaFile javaFile,String path) throws IOException {
        javaFile.writeTo(Paths.get(path).normalize());
    }
}
