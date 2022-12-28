package com.techconative.actions;


import com.squareup.javapoet.*;
import org.mapstruct.Mapper;
import org.w3c.dom.NodeList;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;


class GenerateJavaClass {
    public static void generate(NodeList nodeList) throws IOException {

        ClassName classTypeB = ClassName.get(GenerateMappings.packageA, GenerateMappings.ClassBName);
        ClassName classTypeA=ClassName.get(GenerateMappings.packageB, GenerateMappings.ClassAName);

        MethodSpec.Builder method = MethodSpec
                .methodBuilder("to" + GenerateMappings.ClassBName)
                .addParameter(classTypeA,getObjectNameForClassName(GenerateMappings.ClassAName))
                .returns(classTypeB)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        method =GenerateMappings.bi(method,nodeList);

        String className=GenerateMappings.ClassAName+GenerateMappings.ClassBName+"Mapper";


        ClassName ClassTitle = ClassName.get(getPath(), className);
        ClassName Mappers = ClassName.get("org.mapstruct.factory", "Mappers");
        FieldSpec android = FieldSpec.builder(ClassTitle, "person"+"Mapper")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .initializer( "$T.getMapper($T.class)",Mappers,ClassTitle)
                .build();

        TypeSpec.Builder person = TypeSpec
                .classBuilder(className)
                .addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT)
                .addAnnotation(Mapper.class)
                .addField(android)
                .addMethod(method.build());

         method = MethodSpec
                .methodBuilder("to" + GenerateMappings.ClassAName)
                .addParameter(classTypeB,getObjectNameForClassName(GenerateMappings.ClassBName))
                .returns(classTypeA)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        GenerateMappings.twoWay=true;
        method =GenerateMappings.bi(method,nodeList);
        GenerateMappings.twoWay=false;

        TypeSpec typeSpec=person.addMethod(method.build()).build();
        System.out.println("***************");
        System.out.println(typeSpec);
        System.out.println("***************");
        System.out.println(getPath());
        JavaFile javaFile = JavaFile
                .builder(getPath(),typeSpec)
                .build();

        javaFile.writeTo(Paths.get("./src/main/java"));
    }

    static private String getObjectNameForClassName(String className){
        return className.replaceFirst(String.valueOf(className.charAt(0)),String.valueOf(className.charAt(0)).toLowerCase());
    }

    static private String getPath() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream =
                GenerateJavaClass.class.getClassLoader().getResourceAsStream("application.properties");
        properties.load(inputStream);
        return properties.getProperty("mapper.path");
    }
}

