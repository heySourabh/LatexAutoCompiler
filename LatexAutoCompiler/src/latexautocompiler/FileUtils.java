package latexautocompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author Sourabh Bhat
 */
public class FileUtils {

    public static long getLatestModifiedDate(File dir) {
        if (dir.isFile()) {
            return dir.lastModified();
        }
        File[] files = dir.listFiles();
        long latestDate = 0;
        for (File file : files) {
            long fileModifiedDate = file.isDirectory()
                    ? getLatestModifiedDate(file) : file.lastModified();
            if (fileModifiedDate > latestDate) {
                latestDate = fileModifiedDate;
            }
        }
        return Math.max(latestDate, dir.lastModified());
    }

    public static File getLatestModifiedFile(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return dir;
        }
        File[] files = dir.listFiles();
        File latestModifiedFile = dir;
        for (File file : files) {
            if (file.isDirectory()) {
                file = getLatestModifiedFile(file);
            }
            if (file.lastModified() >= latestModifiedFile.lastModified()) {
                latestModifiedFile = file;
            }
        }

        return latestModifiedFile;
    }
    
    public static ArrayList<String> readLinesWithPattern(File file, Pattern pattern) {
        ArrayList<String> strs = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                Scanner lineSc = new Scanner(sc.nextLine());
                String str = lineSc.findInLine(pattern);
                if (str != null) {
                    strs.add(str.trim());
                }
            }
        } catch (FileNotFoundException ex) {
            // Ignore
        }
        return strs;
    }
}
