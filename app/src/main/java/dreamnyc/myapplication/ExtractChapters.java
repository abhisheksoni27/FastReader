package dreamnyc.myapplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Abhishek on 2/28/2016.
 */
public class ExtractChapters {

    public static ArrayList extractChapters(String toc, String opfPath, String name, String rootPath) {
        String path = opfPath.substring(0, opfPath.lastIndexOf("/"));
        ArrayList chapters = new ArrayList();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            File file = new File(toc);
            Document doc = builder.parse(file);
            NodeList nodes = doc.getElementsByTagName("content");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                chapters.add(path + "/" + element.getAttribute("src"));
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapters;
    }
}
