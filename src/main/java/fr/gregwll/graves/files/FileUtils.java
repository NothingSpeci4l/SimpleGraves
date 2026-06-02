package fr.gregwll.graves.files;

import java.io.*;

public class FileUtils {

    public static void createFile(File file) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    public static void save(File file, String text) {
        try {
            createFile(file);
            FileWriter fw = new FileWriter(file);
            fw.write(text);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadContent(File file) {
        if (!file.exists()) return "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) text.append(line);
            reader.close();
            return text.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void delete(File file) {
        if (file.exists()) file.delete();
    }
}