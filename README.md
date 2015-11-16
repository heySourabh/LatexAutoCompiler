# LatexAutoCompiler
## Compiles LaTeX files if modified and shows line number of error in case of compilation error

This code constantly checks if any file has been modified in the folder and subfolders. If any file has been modified, then the LaTeX project is compiled. A pdf viewer like evince automatically refreshes the pdf to display the latest changes. This tool can be used along with gedit with all the TeX files open in tabs, and evince viewer opened on half of the screen or other screen. As the changes are made in TeX file and saved, they are automatically reflected in the pdf file.

Basically, the tool does the following in a loop:

1. Checks if any file has changed in main folder and all subfolder
2. if any file has changed, then runs "pdflatex" with suitable arguments
3. Checks the status returned by pdflatex command
4. In case, the status != 0 (i.e. error has ocurred), then reads error lines from .log file and displays the error message
