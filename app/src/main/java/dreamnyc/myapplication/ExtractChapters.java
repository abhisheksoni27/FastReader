package dreamnyc.myapplication;

import android.util.Log;

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
    private static final String TAG = "ExtractChapters";
    public static ArrayList extractChapters(String toc, String opfPath) {
        String path = opfPath.substring(0, opfPath.lastIndexOf("/"));
        ArrayList chaptersPathList = new ArrayList();
        ArrayList chaptersList = new ArrayList();
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
                String src = element.getAttribute("src");
                chaptersPathList.add(path + "/" + src);

                if (src.contains(".")) {
                    Log.d(TAG, "extractChapters: "+src);
                    chaptersList.add(src.split("[.]")[0]);
                }

            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chaptersList;
    }
}
