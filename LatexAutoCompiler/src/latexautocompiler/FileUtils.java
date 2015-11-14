package latexautocompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public static String tail(File file, int lines) {
        java.io.RandomAccessFile fileHandler = null;
        try {
            fileHandler
                    = new java.io.RandomAccessFile(file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer < fileLength) {
                        line = line + 1;
                    }
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1) {
                        line = line + 1;
                    }
                }
                if (line >= lines) {
                    break;
                }
                sb.append((char) readByte);
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch (java.io.FileNotFoundException e) {
            return null;
        } catch (java.io.IOException e) {
            return null;
        } finally {
            if (fileHandler != null) {
                try {
                    fileHandler.close();
                } catch (IOException e) {
                }
            }
        }
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
