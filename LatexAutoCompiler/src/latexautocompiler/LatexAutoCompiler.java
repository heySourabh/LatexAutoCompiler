package latexautocompiler;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
                if (runPdfLatex(texFile) != 0) {
                    throw new IllegalStateException("Check log file. \n"
                            + FileUtils.tail(new File(texFile.getAbsolutePath().replace(".tex", ".log")), 2));
                }
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
            proc.waitFor(30, TimeUnit.SECONDS);
            proc.destroy();
        } catch (InterruptedException ex) {
            // Ignore exception
        }
        return proc.exitValue();
    }
}
