package metriker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JavaFile {

    public List<JavaClass> includedClasses = new ArrayList<>();

    public String strippedCode;
    public String codeWithoutInnerClasses;
    public String fileName;

    public JavaFile(File file) {
        
        // get file name...
        fileName = file.getName().substring(0, file.getName().indexOf("."));

        // read file...
        try {
            this.strippedCode = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            throw new Error(ex);
        }

        // remove \r...
        strippedCode = strippedCode.replace("\r", "");

        // replace tabs...
        strippedCode = strippedCode.replace("\t", " ");

        // strip String literals...
        int quotesPosition = indexOfWithoutEscaped(strippedCode, "\"", 0);
        while (quotesPosition != -1) {
            int quotesPosition2 = indexOfWithoutEscaped(strippedCode, "\"", quotesPosition + 1);
            strippedCode = strippedCode.substring(0, quotesPosition) + "new String()" + strippedCode.substring(quotesPosition2 + 1);
            quotesPosition = indexOfWithoutEscaped(strippedCode, "\"", 0);
        }

        // strip '}' and '{'...
        strippedCode = strippedCode.replace("'{'", "''");
        strippedCode = strippedCode.replace("'}'", "''");

        // strip multi line comments...
        int slashStarPosition = strippedCode.indexOf("/*", 0);
        while (slashStarPosition != -1) {
            int starSlashPosition = strippedCode.indexOf("*/", slashStarPosition + 2);
            strippedCode = strippedCode.substring(0, slashStarPosition) + strippedCode.substring(starSlashPosition + 2);
            slashStarPosition = strippedCode.indexOf("/*", 0);
        }

        // placing ; behind anotations...
        {
            String newS = "";
            for (String line : strippedCode.split("\n")) {
                if (line.contains("@")) {
                    if (newS.length() == 0) {
                        newS = line + ";";
                    } else {
                        newS += "\n" + line + ";";
                    }
                } else {
                    if (newS.length() == 0) {
                        newS = line;
                    } else {
                        newS += "\n" + line;
                    }
                }
            }
            strippedCode = newS;
        }

        // some symbols must be followed by \n
        //strippedCode = strippedCode.replace("\n", "");
        strippedCode = strippedCode.replace(";", ";\n");
        strippedCode = strippedCode.replace("{", "{\n");
        strippedCode = strippedCode.replace("}", "}\n");
        strippedCode = strippedCode.replace("@", "\n@");

        // strip single line comments...
        strippedCode += "\n";
        int doubleSlashPosition = strippedCode.indexOf("//", 0);
        while (doubleSlashPosition != -1) {
            int endOfLine = strippedCode.indexOf("\n", doubleSlashPosition + 2);
            strippedCode = strippedCode.substring(0, doubleSlashPosition) + strippedCode.substring(endOfLine);
            doubleSlashPosition = strippedCode.indexOf("//", 0);
        }

        // trim lines...
        String newS = "";
        for (String line : strippedCode.split("\n")) {
            if (line.trim().length() > 0) {
                if (newS.length() != 0 && (newS.endsWith(";") || newS.endsWith("{") || newS.endsWith("}"))) {
                    newS += "\n";
                }
                newS += line.trim();
            }
        }
        strippedCode = newS;

        // add spaces on some places...
        strippedCode = strippedCode.replace(">", "> ");
        strippedCode = strippedCode.replace("]", "] ");

        // remove some useless spaces...
        while (strippedCode.contains("  ")) {
            strippedCode = strippedCode.replace("  ", " ");
        }
        while (strippedCode.contains("\n\n")) {
            strippedCode = strippedCode.replace("\n\n", "\n");
        }
        strippedCode = strippedCode.replace(") {", "){");
        strippedCode = strippedCode.replace("\nstatic {", "\nstatic{");
        strippedCode = strippedCode.replace("\nfor (", "\nfor(");
        strippedCode = strippedCode.replace("\nif (", "\nif(");
        strippedCode = strippedCode.replace("\nswitch (", "\nswitch(");
        strippedCode = strippedCode.replace(" :", ":");
        strippedCode = strippedCode.replace(": ", ":");
        strippedCode = strippedCode.replace(" )", ")");
        strippedCode = strippedCode.replace(") ", ")");
        strippedCode = strippedCode.replace("( ", "(");
        strippedCode = strippedCode.replace(" (", "(");
        strippedCode = strippedCode.replace(" }", "}");
        strippedCode = strippedCode.replace("} ", "}");
        strippedCode = strippedCode.replace("{ ", "{");
        strippedCode = strippedCode.replace(" {", "{");
        strippedCode = strippedCode.replace(" <", "<");
        strippedCode = strippedCode.replace(" [", "[");
        strippedCode = strippedCode.replace(" =", "=");
        strippedCode = strippedCode.replace("= ", "=");
        strippedCode = strippedCode.replace(" @", "@");
        strippedCode = strippedCode.replace("@ ", "@");
        strippedCode = strippedCode.replace("& ", "&");
        strippedCode = strippedCode.replace(" &", "&");
        strippedCode = strippedCode.replace(" ^", "^");
        strippedCode = strippedCode.replace("^ ", "^");
        strippedCode = strippedCode.replace(" |", "|");
        strippedCode = strippedCode.replace("| ", "|");
        strippedCode = strippedCode.replace("! ", "!");
        strippedCode = strippedCode.replace(" !", "!");
        strippedCode = strippedCode.replace(" .", ".");
        strippedCode = strippedCode.replace(". ", ".");
        strippedCode = strippedCode.replace(", ", ",");
        strippedCode = strippedCode.replace(" ,", ",");
        strippedCode = strippedCode.replace("? ", "?");
        strippedCode = strippedCode.replace(" ?", "?");
        strippedCode = strippedCode.replace("; ", ";");
        strippedCode = strippedCode.replace(" ;", ";");
        strippedCode = strippedCode.replace("/ ", "/");
        strippedCode = strippedCode.replace(" /", "/");
        strippedCode = strippedCode.replace("* ", "*");
        strippedCode = strippedCode.replace(" *", "*");
        strippedCode = strippedCode.replace(" %", "%");
        strippedCode = strippedCode.replace("% ", "%");
        strippedCode = strippedCode.replace("- ", "-");
        strippedCode = strippedCode.replace(" -", "-");
        strippedCode = strippedCode.replace("+ ", "+");
        strippedCode = strippedCode.replace(" +", "+");
        strippedCode = strippedCode.replace("\" ", "\"");
        strippedCode = strippedCode.replace(" \"", "\"");
        strippedCode = strippedCode.replace("' ", "'");
        strippedCode = strippedCode.replace(" '", "'");

        // remove visabilities...
        strippedCode = "\n" + strippedCode;
        strippedCode = strippedCode.replaceAll("\npublic ", "\n");
        strippedCode = strippedCode.replaceAll("\nprotected ", "\n");
        strippedCode = strippedCode.replaceAll("\nprivate ", "\n");
        if (strippedCode.startsWith("\n")) {
            strippedCode = strippedCode.substring(1);
        }

        // find upper layer classes...
        strippedCode = "\n" + strippedCode;
        int indexOfClass = strippedCode.indexOf("\nclass ") + 1; // ignore first public class
        while (indexOfClass != 0) { // 0 because indexOfClass+1
            int enfOfScope = endOfScope(strippedCode, indexOfClass);
            includedClasses.add(new JavaClass(strippedCode.substring(indexOfClass, enfOfScope)));
            indexOfClass = strippedCode.indexOf("\nclass ", enfOfScope) + 1;
        }
    }

    public static final int endOfScope(String src, int startOfScope) {
        int level = 0;
        boolean atLeastOneLevelIn = false;
        do {
            char c = src.charAt(startOfScope++);
            if (c == '{') {
                level++;
                atLeastOneLevelIn = true;
            } else if (c == '}') {
                level--;
            }
        } while (level > 0 || !atLeastOneLevelIn);
        return startOfScope;
    }

    public static final int indexOfWithoutEscaped(String src, String str, int startIndex) {
        int indexOf = startIndex - 1;
        do {
            indexOf = src.indexOf(str, indexOf + 1);
        } while (indexOf != -1 && src.substring(indexOf - 1, indexOf).equals("\\"));
        return indexOf;
    }
}
