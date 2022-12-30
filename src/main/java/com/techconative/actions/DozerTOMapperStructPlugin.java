package com.techconative.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.impl.FileChooserFactoryImpl;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import com.techconative.actions.service.GenerateMappings;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FileChooserUI;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;


public class DozerTOMapperStructPlugin extends AnAction {
    static JFileChooser jfc= new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    static File lastPath = null;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Editor ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = ediTorRequiredData.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();
        FileChooserDescriptor fileChooserDescriptor=new FileChooserDescriptor(false,true,false,false,false,false);
        FileChooser.chooseFile(fileChooserDescriptor,e.getProject(),null,  consumer->{
                Messages.showMessageDialog(e.getProject(),
                        consumer.toNioPath().normalize().toString(), "Give Path", Messages.getInformationIcon());
        }
        );

        jfc.setDialogTitle("Choose a directory to save your file: ");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (lastPath == null) {
            File file = new File(e.getProject().getBasePath().replace("/", "\\"));
            jfc.setCurrentDirectory(file);
        }
        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile().isDirectory()) {
            lastPath=jfc.getSelectedFile();
            jfc.setCurrentDirectory(lastPath);
            System.out.println("You selected the directory: " + lastPath.getAbsolutePath());
            try {
                GenerateMappings.generateMappings(selectedText,lastPath.getAbsolutePath(),true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
