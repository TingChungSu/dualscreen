package libs;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by 鼎鈞 on 2016/6/28.
 */
public class FileIO {

    public static boolean isFileExist(String strPath) {
        File file = new File(strPath);
        return file.exists();
    }
    public static void deleteAllFile(File path) {
        if (!path.exists()) {
            return;
        }
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFile(files[i]);
        }
        path.delete();
    }

    public static void createFolder(String path) {
        //make sure U can SDcard read/write
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return;
        File dirFile = new File(path);
        if (!dirFile.exists()) {  //if folder not exitst
            dirFile.mkdir();    //create folder
        }
    }

    public static void writeFile(String path, String data) {
        try {
            FileWriter fw = new FileWriter(path, false);
            BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
            bw.write(data);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path) {
        File file = new File(path);
        if (!file.exists())
            return null;

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

            return text.toString();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return null;
    }
}
