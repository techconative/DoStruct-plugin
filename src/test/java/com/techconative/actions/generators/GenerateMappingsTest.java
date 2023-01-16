package com.techconative.actions.generators;

import junit.framework.TestResult;
import org.junit.Assert;

import javax.swing.text.BadLocationException;
import java.io.IOException;


public class GenerateMappingsTest {
    public static void main(String[] args) {
        GenerateMappingsTest test = new GenerateMappingsTest();
        test.run();
    }

    public TestResult run() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <mappings>
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
                    </mapping>

                    <mapping type="one-way">
                        <class-a>com.techconative.actions.model.DAO.StudentDAO</class-a>
                        <class-b>com.techconative.actions.model.DTO.StudentDTO</class-b>
                        <field>
                            <a>name</a>
                            <b>studentName</b>
                        </field>
                        <field>
                            <a>description</a>
                            <b>studentDescription</b>
                        </field>
                        <field-exclude>
                            <a>age</a>
                            <b>studentAge</b>
                        </field-exclude>
                    </mapping>

                </mappings>""";
        String expected = """
                package mappers;
                                
                import com.techconative.actions.model.DAO.EmployeeDAO;
                import com.techconative.actions.model.DAO.StudentDAO;
                import com.techconative.actions.model.DTO.EmployeeDTO;
                import com.techconative.actions.model.DTO.StudentDTO;
                import org.mapstruct.Mapper;
                import org.mapstruct.Mapping;
                import org.mapstruct.Mappings;
                import org.mapstruct.Named;
                import org.mapstruct.factory.Mappers;
                                
                @Mapper
                public abstract class EmployeeDAOEmployeeDTO {
                  public static EmployeeDAOEmployeeDTO mapper = Mappers.getMapper(EmployeeDAOEmployeeDTO.class);
                                
                  @Mappings({
                      @Mapping(source = "name", target = "empName", qualifiedByName = "referenceMethod"),
                      @Mapping(source = "description", target = "empDescription"),
                      @Mapping(target = "empNumber", ignore = true)
                  })
                  @Named("customMap")
                  public abstract EmployeeDTO toEmployeeDTO(EmployeeDAO employeeDAO);
                                
                  @Mappings({
                      @Mapping(source = "name", target = "studentName"),
                      @Mapping(source = "description", target = "studentDescription"),
                      @Mapping(target = "studentAge", ignore = true)
                  })
                  public abstract StudentDTO toStudentDTO(StudentDAO studentDAO);
                }
                """;

        GenerateMappings.checkXml(xml);
        String actual = null;
        try {
            actual = GenerateMappings.generateMappings("C:\\Users\\VISHNU\\Documents\\GitHub\\plugin\\src\\main\\java\\mappers", false, "className", "mapper");
        } catch (IOException | BadLocationException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(expected, actual);
        return null;
    }
}