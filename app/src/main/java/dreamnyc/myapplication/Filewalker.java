package dreamnyc.myapplication;


import java.io.File;

public class Filewalker {
    String s = null;

    public String walk(File root) {

        File[] list = root.listFiles();

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f);
            } else {
                if (f.getAbsolutePath().toString().lastIndexOf("toc") != -1 || f.getAbsolutePath().toString().lastIndexOf("table") != -1 || f.getAbsolutePath().toString().lastIndexOf("content") != -1 || f.getAbsolutePath().toString().lastIndexOf(".xhtml") != -1) {
                    return f.getAbsolutePath();
                }
            }
        }
        return null;
    }


    public String container(File root, String lookup) {

        File[] list = root.listFiles();

        for (File f : list) {
            if (f.isDirectory()) {
                container(f, lookup);
            } else {
                if (f.getAbsolutePath().contains(lookup)) {

                    setState(f.getAbsolutePath());
                    return getState();

                }

            }
        }

        return getState();
    }

    public String getState() {
        return s;
    }

    public void setState(String state) {

        s = state;

    }

}
