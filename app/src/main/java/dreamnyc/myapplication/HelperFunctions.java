package dreamnyc.myapplication;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Abhishek on 1/30/2016.
 */
public class HelperFunctions {

    public static void unpack(String zipFile, String location) {
        int size;
        int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if (!location.endsWith("/")) {
                location += "/";
            }
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if (null != parentDir) {
                            if (!parentDir.isDirectory()) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String buildDocument(String s, String name) {
        String path = s.substring(0, s.lastIndexOf("/"));
        String code = "Done.";
        File chapter = new File(s);

        String toBeSaved = path + "/" + name + ".txt";
        try {
            Document doc = Jsoup.parse(chapter, "UTF-8");
            Elements e = doc.getAllElements();
            for (int j = 0; j < e.size(); j++) {
                if (j == 0) {
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(toBeSaved)));


                        out.println(e.get(j).text());
                        out.close();

                    } catch (IOException e2) {

                    }
                } else {
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(toBeSaved, true)));


                        out.println(e.get(j).text());
                        out.close();

                    } catch (IOException e2) {

                    }
                }


            }
        } catch (IOException e1) {

        }
        return toBeSaved;
    }

    public static String fileAsText(String s) {
        File chapter = new File(s);
        //Get the text file
        File file = new File(s);

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    public static  String extractName(String nameOfTheEpub) {
        int lastSlashPosition = nameOfTheEpub.lastIndexOf("/") + 1;
        int lastDotPosition = nameOfTheEpub.lastIndexOf(".");
        String name = nameOfTheEpub.substring(lastSlashPosition, lastDotPosition);

        return name;
    }

    public static boolean saveFile(File src, File dst) throws IOException {
        if (src.getAbsolutePath().toString().equals(dst.getAbsolutePath().toString())) {

            return true;

        } else {

            if (dst.isDirectory()) {

                dst.mkdirs();

            }

            InputStream is = new FileInputStream(src);
            OutputStream os = new FileOutputStream(dst);
            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            is.close();
            os.close();

        }
        return true;
    }

    public static void deleteFile(String path) {

        File toBeDeleted = new File(path);
        toBeDeleted.delete();


    }

    public static void cl(String str) {
        Log.d("Okay", str);
    }

//    public void addFiles() {
//        String path = getExternalFilesDir(null).toString();
//        cl(path);
//        File f = new File(path);
//        File file[] = f.listFiles();
//
//        for (int i = 0; i < file.length; i++) {
//            nameFiles.add(file[i]);
//        }
//    }
//
//    public void showFiles(ArrayList nameFiles) {
//
//        String path = getExternalFilesDir(null).toString();
//        int j = 0;
//        for (j = 0; j < nameFiles.size(); j++) {
//            path = nameFiles.get(j).toString();
//            cl("PATH: " + path);
//
//            File f = new File(path + "/");
//            File file[] = f.listFiles();
//            Filewalker fw = new Filewalker();
//            fw.walk(f);
//        }
//
//
//    }


}

