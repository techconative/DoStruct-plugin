package com.techconative.actions.test;

import com.techconative.actions.service.GenerateMappings;

import javax.swing.text.BadLocationException;
import java.io.IOException;


public class Test {
    public static void main(String[] args) throws IOException, BadLocationException {
      Test test=new Test();
      test.run();
    }
    void run() throws IOException, BadLocationException {
        String xml= """
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
       // GenerateMappings.generateMappings(xml, "<GIve complete absolute path>",false,"className","mapper");
        GenerateMappings.CheckXml(xml);
        GenerateMappings.generateMappings("C:\\Users\\VISHNU\\Documents\\GitHub\\plugin\\src\\main\\java\\com\\techconative\\actions\\mappers",true,"className","mapper");
    }

}
