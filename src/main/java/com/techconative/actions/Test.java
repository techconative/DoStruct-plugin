package com.techconative.actions;

import java.io.IOException;
import java.net.URISyntaxException;


public class Test {
    public static void main(String[] args) throws URISyntaxException, IOException, ClassNotFoundException {
      Test test=new Test();
      test.run();
    }
    void run() {
        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<mappings>\n" +
                "    <mapping type=\"one-way\">\n" +
                "        <class-a>com.techconative.actions.model.Source</class-a>\n" +
                "        <class-b>com.techconative.actions.model.Destination</class-b>\n" +
                "        <field>\n" +
                "            <a>name</a>\n" +
                "            <b>nameD</b>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <a>description</a>\n" +
                "            <b>descriptionD</b>\n" +
                "        </field>\n" +
                "        <field-exclude>\n" +
                "            <a>count</a>\n" +
                "            <b>count</b>\n" +
                "        </field-exclude>\n" +
                "    </mapping>\n" +
                "\n" +
                "    <mapping type=\"one-way\">\n" +
                "        <class-a>com.techconative.actions.model.Source2</class-a>\n" +
                "        <class-b>com.techconative.actions.model.Destination2</class-b>\n" +
                "        <field>\n" +
                "            <a>name</a>\n" +
                "            <b>nameD</b>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <a>description</a>\n" +
                "            <b>descriptionD</b>\n" +
                "        </field>\n" +
                "        <field-exclude>\n" +
                "            <a>count</a>\n" +
                "            <b>count</b>\n" +
                "        </field-exclude>\n" +
                "    </mapping>\n" +
                "\n" +
                "    <mapping type=\"one-way\">\n" +
                "        <class-a>com.techconative.actions.model.Source3</class-a>\n" +
                "        <class-b>com.techconative.actions.model.Destination3</class-b>\n" +
                "        <field>\n" +
                "            <a>name</a>\n" +
                "            <b>nameD</b>\n" +
                "        </field>\n" +
                "        <field>\n" +
                "            <a>description</a>\n" +
                "            <b>descriptionD</b>\n" +
                "        </field>\n" +
                "        <field-exclude>\n" +
                "            <a>count</a>\n" +
                "            <b>count</b>\n" +
                "        </field-exclude>\n" +
                "    </mapping>\n" +
                "</mappings>";

        GenerateMappings.generateMappings(xml,"C:\\Users\\VISHNU\\Documents\\GitHub\\plugin\\src\\main\\java\\com\\techconative\\actions\\model");


    }
}
