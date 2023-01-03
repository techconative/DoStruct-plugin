package com.techconative.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

@SpringBootTest
class MapStructPluginTest {
    private static AnActionEvent actionEvent;

    public static void main(String[] args) throws IOException, UnsupportedFlavorException {
        actionPerform();
    }

    @Test
    public static void actionPerform(){
    }
}