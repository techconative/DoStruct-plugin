package com.techconative.actions.test;

import java.io.IOException;


public class Test {
    public static void main(String[] args) throws IOException {
      Test test=new Test();
      test.run();
    }
    void run() throws IOException {
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<mappings>\n" +
                "    <mapping type=\"one-way\" map-id=\"Custom_Map\">\n" +
                "        <class-a>com.techconative.actions.model.DAO.EmployeeDAO</class-a>\n" +
                "        <class-b>com.techconative.actions.model.DTO.EmployeeDTO</class-b>\n" +
                "        <field map-id=\"Reference_Method\">\n" +
                "            <a>name</a>\n" +
                "            <b>empName</b>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <a>description</a>\n" +
                "            <b>empDescription</b>\n" +
                "        </field>\n" +
                "        <field-exclude>\n" +
                "            <a>empNo</a>\n" +
                "            <b>empNumber</b>\n" +
                "        </field-exclude>\n" +
                "    </mapping>\n" +
                "\n" +
                "    <mapping type=\"one-way\">\n" +
                "        <class-a>com.techconative.actions.model.DAO.StudentDAO</class-a>\n" +
                "        <class-b>com.techconative.actions.model.DTO.StudentDTO</class-b>\n" +
                "        <field>\n" +
                "            <a>name</a>\n" +
                "            <b>studentName</b>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <a>description</a>\n" +
                "            <b>studentDescription</b>\n" +
                "        </field>\n" +
                "        <field-exclude>\n" +
                "            <a>age</a>\n" +
                "            <b>studentAge</b>\n" +
                "        </field-exclude>\n" +
                "    </mapping>\n" +
                "\n" +
                "</mappings>";
       // GenerateMappings.generateMappings(xml, "<GIve complete absolute path>",false,"className","mapper");
        //GenerateMappings.generateMappings(xml,"<GIve complete absolute path>",true,"className","mapper");
    }

}
