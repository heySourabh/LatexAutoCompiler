package latexautocompiler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Sourabh Bhat
 */
public class LatexAutoCompiler {

    public static void main(String[] args) throws IOException {
        // Get main tex file path
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select the main TeX file");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            startProcessing(fileChooser.getSelectedFile());
        } else {
            System.out.println("Not file selected!!");
        }
    }

    private static void startProcessing(File texFile) throws IOException {
        File dir = texFile.getParentFile();
        long dirModifiedDate = 0L;

        while (true) {
            sleepForSecs(1);
            if (FileUtils.getLatestModifiedDate(dir) == dirModifiedDate) {
                continue;
            }
            System.out.println("Updating tex file");
            System.out.println("Latest modified file: " + FileUtils.getLatestModifiedFile(dir));
            try {
                PdfLatexInterface.runPdfLatex(texFile); // Run twice to take care of indexing
                if (PdfLatexInterface.runPdfLatex(texFile) != 0) { // Check for error in compilation
                    throw new IllegalStateException("Check log file. \n"
                            + combineStrings(FileUtils.readLinesWithPattern(
                                            new File(texFile.getAbsolutePath().replace(".tex", ".log")),
                                            Pattern.compile(".+:[0-9]+:.+"))));
                }
                // Open pdf file:
                Desktop.getDesktop().open(new File(texFile.getAbsolutePath().replace(".tex", ".pdf")));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error in compilation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            dirModifiedDate = FileUtils.getLatestModifiedDate(dir);
        }
    }

    private static void sleepForSecs(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException ex) {
            // Ignore
        }
    }

    static private String combineStrings(ArrayList<String> strings) {
        String outStr = "";
        for (String str : strings) {
            outStr += "\n" + str;
        }

        return outStr;
    }
}
