package com.techconative.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.techconative.actions.service.GenerateMappings;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;


public class DozerTOMapperStructPlugin extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Editor ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = ediTorRequiredData.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();
        FileChooserDescriptor fileChooserDescriptor =
                new FileChooserDescriptor(false, true, false, false, false, false);
        FileChooser.chooseFile(fileChooserDescriptor, e.getProject(), null, consumer -> {
                    Messages.showMessageDialog(e.getProject(), "Path selected is :" + consumer.toNioPath().normalize().toString(),
                            "Give Path", Messages.getInformationIcon());
                    Pair<String, Boolean> pair = Messages.showInputDialogWithCheckBox("Mapper Class name", "Give Mapper Class Name",
                            "Generate class", true, true, Messages.getQuestionIcon(), null, null);
                    String str = Messages.showInputDialog("Give variable name", "Give Variable Name", Messages.getQuestionIcon());

                    System.out.println(pair.first);
                    System.out.println(pair.second);
                    System.out.println(str);
            try {
                GenerateMappings.generateMappings(selectedText, consumer.toNioPath().normalize().toString(), pair.second);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

                }
        );
    }
}
