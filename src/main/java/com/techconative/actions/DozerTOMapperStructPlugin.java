package com.techconative.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.techconative.actions.service.GenerateMappings;
import com.techconative.actions.utilities.Utilities;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.techconative.actions.utilities.Utilities.findAndApply;


public class DozerTOMapperStructPlugin extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Editor ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = ediTorRequiredData.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();


        FileChooserDescriptor fileChooserDescriptor =
                new FileChooserDescriptor(false, true, false, false, false, false);
        FileChooser.chooseFile(fileChooserDescriptor, e.getProject(), null, consumer -> {
                    JTextPanes(consumer.toNioPath().normalize().toString(), selectedText);
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

    void JTextPanes(String path, String selectedText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = JBUI.insets(5);//new Insets(10, 10, 10, 10)

        constraints.gridx = 0;
        constraints.gridy = 0;

        JLabel label = new JLabel("ClassName:");
        panel.add(label, constraints);
        JTextField textField = new JTextField(10);
        textField.setSize(new Dimension(100, 100));
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(textField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        JLabel label1 = new JLabel("Write In Location:");
        panel.add(label1, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        JCheckBox checkBox = new JCheckBox("(Select it to generate class)");
        panel.add(checkBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel label2 = new JLabel("Path:");
        panel.add(label2, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        JLabel label3 = new JLabel(path);
        panel.add(label3, constraints);

        JOptionPane.showConfirmDialog(null, panel, "Input Dialog",
                JOptionPane.OK_CANCEL_OPTION);

        String value1 = textField.getText();
        boolean value2 = checkBox.isSelected();
        String value3 = Utilities.GetVariableNameFromClassName(value1);

        try {
            if (value2) {
                GenerateMappings.generateMappings(selectedText, path, true, value1, value3);
            } else {
                getJTextPlane(GenerateMappings.generateMappings(selectedText, path, false, value1, value3));
            }
        } catch (IOException | BadLocationException ex) {
            throw new RuntimeException(ex);
        }

    }



}
