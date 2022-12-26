package com.techconative.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

public class MapStructPlugin extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Editor ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = ediTorRequiredData.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();
        GenerateMappings.generateMappings(selectedText);

    }
}
