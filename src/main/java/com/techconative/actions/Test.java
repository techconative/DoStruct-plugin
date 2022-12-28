package com.techconative.actions;

import com.techconative.actions.model.Destination;
import com.techconative.actions.model.Source;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Properties;

public class Test {
    public static void main(String[] args) throws URISyntaxException, IOException {
      Test test=new Test();
      test.run();
    }
    void run() throws URISyntaxException, IOException {
        Source simpleSource = new Source();
        simpleSource.setName("Sourcedd");
        simpleSource.setDescription("SourceDescriptioddn");
        System.out.println(simpleSource);
        String userDirectory = FileSystems.getDefault()
                .getPath("")
                .toAbsolutePath()
                .toString();
        System.out.println(userDirectory);
        URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();

        System.out.println(this.getClass().getPackage().getName());
        System.out.println(Arrays.stream(Package.getPackages()).toList().stream()
                .filter(x->x.getName().matches("com.techconative.*.*")).toList());

        GenerateMappings generateMappings=new GenerateMappings();
        System.out.println(generateMappings.getClass().getPackage().getName());

        Properties properties = new Properties();
        InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream("application.properties");
        properties.load(inputStream);
        System.out.println(properties.getProperty("mapper.path"));


    }
}
