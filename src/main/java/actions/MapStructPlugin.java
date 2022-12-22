package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
public class MapStructPlugin extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Editor ediTorRequiredData = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = ediTorRequiredData.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        Document document = null;
        try {
            document = dBuilder.parse(new InputSource(new StringReader(selectedText)));
        } catch (SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();



        List<String> attributelist = new ArrayList<>();

        /* Name of the Class on class-a tag */
        String class_a = document.getElementsByTagName("class-a").item(0).getTextContent();

        /* Name of the Class on class-b tag */
        String class_b = document.getElementsByTagName("class-b").item(0).getTextContent();

        String[] ClassB_Array = class_b.split("[.]");
        String[] ClassA_Array = class_a.split("[.]");
        String ClassB_Name = ClassB_Array[ClassB_Array.length - 1];
        String ClassA_Name = ClassA_Array[ClassA_Array.length - 1];
        String str5 = String.valueOf(ClassA_Name.charAt(0)).toLowerCase() + ClassA_Name.substring(1);

        /* Mapping for field tags */
        NodeList fieldList = document.getElementsByTagName("field");

        IntStream.range(0, fieldList.getLength())
                .mapToObj(fieldList::item)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(x -> (Element) x).forEach(y ->
                        attributelist.add("@Mapping(source=\"" + y.getElementsByTagName("a").item(0).getTextContent()
                                + "\",target = \"" + y.getElementsByTagName("b").item(0).getTextContent() + "\")")
                );

        /* Mapping for field-exclude tags */
        NodeList excludeList = document.getElementsByTagName("field-exclude");

        IntStream.range(0, excludeList.getLength())
                .mapToObj(excludeList::item)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(x -> (Element) x).forEach(y ->
                        attributelist.add("@Mapping(target = \"" + y.getElementsByTagName("b").item(0).getTextContent()
                                + "\", ignore = \"true\")"));
        //remove line separator for last element
        attributelist.set(attributelist.size()-1, attributelist.get(attributelist.size()-1).replace("[/t]",""));

        String mappings = "@Mappings({" + String.join(","+System.lineSeparator(), attributelist) + "})"+System.lineSeparator()
                + "protected abstract " + ClassB_Name + String.format(" to%s(%s %s", ClassB_Name, ClassA_Name, str5) + ")";



        StringSelection stringSelection = new StringSelection(mappings);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    @Override
    public boolean isDumbAware() {
        return super.isDumbAware();
    }
}
