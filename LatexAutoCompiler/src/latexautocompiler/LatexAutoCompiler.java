package latexautocompiler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
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
                runPdfLatex(texFile); // Run twice to take care of indexing
                if (runPdfLatex(texFile) != 0) { // Check for error in compilation
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

    /**
     * Runs pdflatex and returns the status
     *
     * @param texFile
     * @return status returned by pdflatex command
     * @throws Exception
     */
    static private int runPdfLatex(File texFile) throws Exception {
        File dir = texFile.getParentFile();
        Process proc = Runtime.getRuntime().exec(
                new String[]{
                    "pdflatex",
                    "-halt-on-error",
                    "-file-line-error",
                    texFile.getAbsolutePath()
                },
                null,
                dir
        );
        try {
            proc.waitFor(1, TimeUnit.MINUTES);
            proc.destroy();
        } catch (InterruptedException ex) {
            // Ignore exception
        }
        return proc.exitValue();
    }

    static private String combineStrings(ArrayList<String> strings) {
        String outStr = "";
        for (String str : strings) {
            outStr += "\n" + str;
        }

        return outStr;
    }
}
