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
import com.intellij.ui.JBColor;
import com.techconative.actions.service.GenerateMappings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
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
                            "Given Path Is", Messages.getInformationIcon());
                    Pair<String, Boolean> pair = Messages.showInputDialogWithCheckBox("Mapper Class name", "Enter Mapper Class Name",
                            "Generate class", true, true, Messages.getQuestionIcon(), null, null);
                    String str = Messages.showInputDialog("Give variable name", "Give Variable Name", Messages.getQuestionIcon());
                    try {
                        if (pair.second){
                            GenerateMappings.generateMappings(selectedText, consumer.toNioPath().normalize().toString(),
                                    pair.second, pair.first, str);
                            Messages.showMessageDialog(pair.first+" Class is generated on path "+consumer.toNioPath().normalize()
                            ,"Success Alert",Messages.getInformationIcon());
                        }
                        else{
                            getJTextPlane(GenerateMappings.generateMappings(selectedText,
                                    consumer.toNioPath().normalize().toString(), pair.second, pair.first, str));
                        }
                    } catch (IOException | BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }

                }
        );
    }

    JTextPane getJTextPlane(String code) throws BadLocationException {
        JFrame frame = new JFrame("MAPPER ABSTRACT CLASS CODE");
        Container cp = frame.getContentPane();
        JTextPane pane = new JTextPane();
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setFontSize(set, 17);
        StyleConstants.setBold(set, true);
        pane.setCharacterAttributes(set, true);
        pane.setText(code);
        set = new SimpleAttributeSet();
        StyleConstants.setItalic(set, true);
        StyleConstants.setForeground(set, JBColor.BLUE);
        Document doc = pane.getStyledDocument();
        doc.insertString(doc.getLength(), "", set);
        set = new SimpleAttributeSet();
        doc.insertString(doc.getLength(), "", set);
        JScrollPane scrollPane = new JScrollPane(pane);
        cp.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(675, 600);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return pane;

    }
}
