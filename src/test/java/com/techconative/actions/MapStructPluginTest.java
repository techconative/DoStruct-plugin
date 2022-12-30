package com.techconative.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class MapStructPluginTest {
    private static MapStructPlugin mapStructPlugin = new MapStructPlugin();
    private static AnActionEvent actionEvent;

    public static void main(String[] args) throws IOException, UnsupportedFlavorException {
        actionPerform();
    }

    @Test
    public static void actionPerform() throws IOException, UnsupportedFlavorException {
//        String xml = "<mapping type=\"one-way\">\n" +
//                "    <class-a>com.tech.sample.ClassA</class-a>\n" +
//                "    <class-b>com.tech.sample.ClassB</class-b>\n" +
//                "    <field>\n" +
//                "      <a>Field1</a>\n" +
//                "      <b>Field1.2</b>\n" +
//                "    </field>\n" +
//                "    <field>\n" +
//                "      <a>Field2</a>\n" +
//                "      <b>Field2.2</b>\n" +
//                "    </field>\n" +
//                "    <field-exclude>\n" +
//                "      <a>Field3</a>\n" +
//                "      <b>Field3.3</b>\n" +
//                "    </field-exclude>\n" +
//                "  </mapping>";
//        actionEvent = mock(AnActionEvent.class);
//        System.out.println(actionEvent);
//        Editor editor = mock(Editor.class);
//        CaretModel caretModel = mock(CaretModel.class);
//        Caret caret = mock(Caret.class);
//        when(actionEvent.getRequiredData(any())).thenReturn(editor);
//        when(editor.getCaretModel()).thenReturn(caretModel);
//        when(caretModel.getCurrentCaret()).thenReturn(caret);
//        when(caret.getSelectedText()).thenReturn(xml);
//        String expected = "@Mappings({@Mapping(source=\"Field1\",target = \"Field1.2\"),\n" +
//                "@Mapping(source=\"Field2\",target = \"Field2.2\"),\n" +
//                "@Mapping(target = \"Field3.3\", ignore = true)})\n" +
//                "public abstract ClassB toClassB(ClassA classA);";
//        mapStructPlugin.actionPerformed(actionEvent);
//        String data = (String) Toolkit.getDefaultToolkit()
//                .getSystemClipboard().getData(DataFlavor.stringFlavor);
//        System.out.println(data.trim());
//        System.out.println("----------");
//        System.out.println(expected.trim());
//        assertEquals(data.lines().toList(), expected.lines().toList());
    }
}