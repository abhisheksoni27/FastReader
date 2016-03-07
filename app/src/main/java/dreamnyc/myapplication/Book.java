package dreamnyc.myapplication;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;

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
 * Created by Abhishek on 2/6/2016.
 */
public class Book {

    String title;
    String pathOfTOC;
    String pathOfCover;
    String path;
    String OPFFile;
    String SpineExtension;
    int lastReadPosition = 0;
    String lastReadChapter = "";
    ArrayList Spine = new ArrayList();
    ArrayList SpinePath = new ArrayList();
    ArrayList SpineName = new ArrayList();
    String[] TOC;
    String author;

    public Book() {

    }

    public Book(String title) {
        this.title = title;
    }

    public static Book fromCursor(Cursor c){
        Gson okay = new Gson();
       String s = c.getString(c.getColumnIndexOrThrow("bookObject"));
Book b = okay.fromJson(s,Book.class);
        return b;

    }

    public int getLastReadPosition() {
        return lastReadPosition;
    }

    public void setLastReadPosition(int lastReadPosition) {
        this.lastReadPosition = lastReadPosition;
    }

    public String getLastReadChapter() {
        return lastReadChapter;
    }

    public void setLastReadChapter(String lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }

    public String getSpineExtension() {
        return SpineExtension;
    }

    public void setSpineExtension(String spineExtension) {
        SpineExtension = spineExtension;
    }

    public ArrayList getSpinePath() {
        return SpinePath;
    }

    public void setSpinePath(ArrayList spinePath) {
        SpinePath = spinePath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOPFFile() {
        return OPFFile;
    }

    public void setOPFFile(String OPFFile) {
        this.OPFFile = OPFFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList getSpine() {
        return Spine;
    }

    public void setSpine(ArrayList spine) {
        Spine = spine;
    }


    public String getPathOfTOC() {
        return pathOfTOC;
    }

    public void setPathOfTOC(String pathOfTOC) {
        this.pathOfTOC = pathOfTOC;
    }

    public String getPathOfCover() {
        return pathOfCover;
    }

    public void setPathOfCover(String pathOfCover) {
        this.pathOfCover = pathOfCover;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getContents(String path, String ed) {


        Log.d("Path", path);
        setOPFFile(path);
        int TOCFLAG = 0;
        int COVERFLAG = 0;
        String[] rootPath = path.split("/");
        String rootpath = "";
        for (int j = 0; j < rootPath.length - 1; j++) {
            rootpath = rootpath + "/" + rootPath[j];

        }
        rootpath = rootpath.substring(1);

        int lastSlash = rootpath.lastIndexOf("/");
        String pathroot = rootpath.substring(0, lastSlash + 1);
        ArrayList<String> contents = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            File file = new File(path);
            Document doc = builder.parse(file);
            // Do something with the document here.
            String a = doc.getElementsByTagName("dc:title").item(0).getTextContent();
            String b = doc.getElementsByTagName("dc:creator").item(0).getTextContent();
            setTitle(a);
            setAuthor(b);
            NodeList nodes = doc.getElementsByTagName("item");
            NodeList nodeSpine = doc.getElementsByTagName("itemref");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);

                String cover = element.getAttribute("media-type");
                if (cover.contains("image") && COVERFLAG == 0) {
                    String coverBook = element.getAttribute("href");
                    setPathOfCover(rootpath + "/" + coverBook);
                    COVERFLAG = 1;

                } else if (cover.contains("application/x-dtbncx+xml") && TOCFLAG == 0) {
                    String toc = element.getAttribute("href");
                    setPathOfTOC(rootpath + "/" + toc);
                    TOCFLAG = 1;

                }


            }

            for (int k = 0; k < nodeSpine.getLength(); k++) {
                String t = "";
                t = nodeSpine.item(k).getAttributes().getNamedItem("idref").getNodeValue();
                Spine.add(t);

                for (int z = 0; z < nodes.getLength(); z++) {
                    Element spineElement = (Element) nodes.item(z);
                    String t1 = spineElement.getAttribute("id");

                    if (t == t1) {
                        String opfRemoved = getOPFFile().substring(0, getOPFFile().lastIndexOf("/") + 1);
                        String pathHTML = spineElement.getAttribute("href");
                        SpinePath.add(opfRemoved + pathHTML);
                        SpineExtension = pathHTML.substring(pathHTML.lastIndexOf("."));
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    String findInSpine(String s, Book b) {
        Log.d("Okay" + Math.random() + "Okay", s);
        char ch = '.';
        ArrayList spine = b.getSpinePath();
        s = b.getSpinePath().get(0).toString().substring(0, b.getSpinePath().get(0).toString().lastIndexOf("/") + 1) + s + getSpineExtension();
        for (int i = 0; i < spine.size(); i++) {

            String test = spine.get(i).toString();
            Log.d("Test inside for okay", test + "");
            if (test.contains(s) && test.indexOf(s) > 0 && test.charAt(test.indexOf(s) + s.length()) == ch) {

                s = b.getSpinePath().get(i).toString();
            }


        }
        return s;
    }

}
