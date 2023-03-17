package com.techconative.actions.generators;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import org.junit.Assert;
import org.w3c.dom.Document;

public class GenerateMappingsTest {
    public static void main(String[] args) throws IOException, BadLocationException {
        GenerateMappingsTest test = new GenerateMappingsTest();
        test.run();
    }

    public void run() throws IOException, BadLocationException {
        String xml =
                """
                <mapping type="one-way" map-id="Custom_Map">
                    <class-a>com.techconative.actions.model.DAO.EmployeeDAO</class-a>
                    <class-b>com.techconative.actions.model.DTO.EmployeeDTO</class-b>
                    <field map-id="Reference_Method">
                        <a>name</a>
                        <b>empName</b>
                    </field>
                    <field>
                        <a>description</a>
                        <b>empDescription</b>
                    </field>
                    <field-exclude>
                        <a>empNo</a>
                        <b>empNumber</b>
                    </field-exclude>
                </mapping>""";
        String expected =
                """
                @Mappings({
                    @Mapping(source = "name", target = "empName", qualifiedByName = "referenceMethod"),
                    @Mapping(source = "description", target = "empDescription"),
                    @Mapping(target = "empNumber", ignore = true)
                })
                @Named("customMap")
                public abstract EmployeeDTO toEmployeeDTO(EmployeeDAO employeeDAO);
                                """;

        Document finalDocument = GenerateMappings.getDocument(xml);
        String actual = GenerateMappings.generateMappings(finalDocument, null, false, "className", "attributeName");
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }
}
