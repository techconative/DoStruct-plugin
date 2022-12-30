package com.techconative.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.JBColor;
import com.techconative.actions.service.GenerateMappings;
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

public class MapStructPlugin extends AnAction {
    static JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    static File lastPath = null;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = ediTorRequiredData.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();
        jfc.setDialogTitle("Choose a directory to view your files: ");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (lastPath == null) {
            File file = new File(e.getProject().getBasePath().replace("/", "\\"));
            jfc.setCurrentDirectory(file);
        }
        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile().isDirectory()) {
            lastPath = jfc.getSelectedFile();
            jfc.setCurrentDirectory(lastPath);
            System.out.println("You selected the directory: " + lastPath.getAbsolutePath());
            String code = null;
            try {
                code = GenerateMappings.generateMappings(selectedText, lastPath.getAbsolutePath(), false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                getJTextPlane(code);
            } catch (BadLocationException ex) {
                return;
            }
        }

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
