package latexautocompiler;

import java.io.File;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Sourabh Bhat
 */
public class PdfLatexInterface {
    static final int TIME_OUT = 2;
    static final TimeUnit TIME_OUT_UNIT = TimeUnit.MINUTES;

    /**
     * Runs pdflatex and returns the status
     *
     * @param texFile
     * @return status returned by pdflatex command
     * @throws Exception
     */
    static public int runPdfLatex(File texFile) throws Exception {
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
            if (proc.waitFor(TIME_OUT, TIME_OUT_UNIT) == false) {
                proc.destroy();
                throw new InterruptedIOException("The compilation did not complete within specified timeout.");
            }
        } catch (InterruptedException ex) {
            // Ignore exception
        }
        return proc.exitValue();
    }
}
