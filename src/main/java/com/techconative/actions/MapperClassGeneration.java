package com.techconative.actions;

import com.squareup.javapoet.*;
import org.mapstruct.Mapper;
import org.w3c.dom.NodeList;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;


class MapperClassGeneration {
    public static void generate(NodeList nodeList, String path, Map<String,String> map) throws IOException, ClassNotFoundException {

        ClassName classTypeB = ClassName.get(map.get("packageA"), map.get("ClassBName")); 
        ClassName classTypeA=ClassName.get(map.get("packageB"), map.get("ClassAName"));

        MethodSpec.Builder method = MethodSpec
                .methodBuilder("to" + map.get("ClassBName"))
                .addParameter(classTypeA,Utilities.getObjectNameForClassName(map.get("ClassAName")))
                .returns(classTypeB)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        method =GenerateMappings.bi(method,nodeList,false);

        String className=map.get("ClassAName")+map.get("ClassBName")+"Mapper";

        String[] strings=getPath(path);
        ClassName ClassTitle = ClassName.get(strings[1], className);
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
                .methodBuilder("to" + map.get("ClassAName"))
                .addParameter(classTypeB,Utilities.getObjectNameForClassName(map.get("ClassBName")))
                .returns(classTypeA)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

        method =GenerateMappings.bi(method,nodeList,true);

        TypeSpec typeSpec=person.addMethod(method.build()).build();
        JavaFile javaFile = JavaFile
                .builder(strings[1],typeSpec)
                .build();

        javaFile.writeTo(Paths.get(strings[0]).normalize());
    }



    static private String[] getPath(String path) {
        String str=path.replace(path.charAt(2),'.');
        String[] strings=str.split("src.main.java.");
        strings[0]=str.replaceAll("."+strings[1],"").trim().replace('.',path.charAt(2));
        return strings;
    }
}

