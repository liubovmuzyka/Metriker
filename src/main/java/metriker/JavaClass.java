package metriker;

import java.util.ArrayList;
import java.util.List;

public class JavaClass {

    public String name;
    public int loc;

    public List<JavaMethod> methoden = new ArrayList<JavaMethod>();
    public List<JavaClass> klassen = new ArrayList<JavaClass>();

    public JavaClass(String strippedCode) {

        // get name...
        String firstLine = strippedCode.substring(0, strippedCode.indexOf("\n"));
        if (firstLine.contains(" implements ")) {
            firstLine = firstLine.substring(0, firstLine.indexOf(" implements "));
        }
        if (firstLine.contains(" extends ")) {
            firstLine = firstLine.substring(0, firstLine.indexOf(" extends "));
        }
        name = firstLine.substring(firstLine.lastIndexOf(" ") + 1, firstLine.length());
        if (name.endsWith("{")) {
            name = name.substring(0, name.length() - 1);
        }

        // calculate loc of class...
        loc = strippedCode.split("\n").length;

        // remove static blocks...
        int indexOfStaticBlock = strippedCode.indexOf("\nstatic{");
        while (indexOfStaticBlock != -1) {
            strippedCode = strippedCode.substring(0, indexOfStaticBlock) + strippedCode.substring(JavaFile.endOfScope(strippedCode, indexOfStaticBlock));
            indexOfStaticBlock = strippedCode.indexOf("\nstatic{");
        }

        // remove Attributes...
        {
            String newS = "";
            int arrayCreationLevelEntered = 0;
            for (String line : strippedCode.split("\n")) {
                if (arrayCreationLevelEntered > 0) { // previous line startet inline array creation
                    arrayCreationLevelEntered += countOccurances(line, '{');
                    arrayCreationLevelEntered -= countOccurances(line, '}');
                } else {
                    if (line.contains("=new") && line.contains("[]") && !isInsideScope(newS)) {
                        arrayCreationLevelEntered++;
                    } else if (isInsideScope(newS) || line.contains("{") || line.contains("}")) {
                        if (newS.length() == 0) {
                            newS = line;
                        } else {
                            newS += "\n" + line;
                        }
                    }
                }
            }
            strippedCode = newS;
        }

        // extract methods...
        {
            int indexOfNewLine = strippedCode.indexOf("\n");
            int indexOfNextNewLine = indexOfNewLine == -1 ? -1 : strippedCode.indexOf("\n", indexOfNewLine + 1);
            while (indexOfNewLine != -1 && indexOfNextNewLine != -1) {
                String methodNameLineCandidate = strippedCode.substring(indexOfNewLine + 1, indexOfNextNewLine);
                if (methodNameLineCandidate.contains("(") && methodNameLineCandidate.contains(")") && methodNameLineCandidate.contains("{") && !isInsideScope(strippedCode, indexOfNewLine)) {
                    int endOfScope = JavaFile.endOfScope(strippedCode, indexOfNewLine + 1);
                    methoden.add(new JavaMethod(
                            methodNameLineCandidate.substring(
                                    0,
                                    methodNameLineCandidate.length() - 1
                            ), strippedCode.substring(indexOfNewLine + 1, endOfScope)));
                    strippedCode = strippedCode.substring(0, indexOfNewLine + 1) + strippedCode.substring(endOfScope + 1);
                    indexOfNewLine--;
                }
                indexOfNewLine = strippedCode.indexOf("\n", indexOfNewLine + 1);
                indexOfNextNewLine = strippedCode.indexOf("\n", indexOfNewLine + 1);
            }
        }

        // extract inner classes...
        {
            int indexOfNewLine = strippedCode.indexOf("\n");
            int indexOfNextNewLine = indexOfNewLine == -1 ? -1 : strippedCode.indexOf("\n", indexOfNewLine + 1);
            while (indexOfNewLine != -1 && indexOfNextNewLine != -1) {
                String methodNameLineCandidate = strippedCode.substring(indexOfNewLine + 1, indexOfNextNewLine);
                if (methodNameLineCandidate.contains("class ") && methodNameLineCandidate.contains("{") && !isInsideScope(strippedCode, indexOfNewLine)) {
                    int endOfScope = JavaFile.endOfScope(strippedCode, indexOfNewLine + 1);
                    String innerClassCode = strippedCode.substring(indexOfNewLine + 1, endOfScope);
                    klassen.add(new JavaClass(innerClassCode));
                    //System.out.println("****** " + innerClassCode);
                    strippedCode = strippedCode.substring(0, indexOfNewLine + 1) + strippedCode.substring(endOfScope + 1);
                    indexOfNewLine--;
                }
                indexOfNewLine = strippedCode.indexOf("\n", indexOfNewLine + 1);
                indexOfNextNewLine = strippedCode.indexOf("\n", indexOfNewLine + 1);
            }
        }
    }

    public static boolean isInsideScope(String strippedCodeBeforePostion) {
        return isInsideScope(strippedCodeBeforePostion, strippedCodeBeforePostion.length());
    }

    public static boolean isInsideScope(String strippedCode, int position) {
        int levelCounter = 0;
        for (int i = 0; i < position; i++) {
            if (strippedCode.charAt(i) == '{') {
                levelCounter++;
            } else if (strippedCode.charAt(i) == '}') {
                levelCounter--;
            }
        }
        return levelCounter > 1;
    }

    public static int countOccurances(String s, char c) {
        return countOccurances(s, c + "");
    }

    public static int countOccurances(String s, String c) {
        return s.length() - s.replace(c, "").length();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JavaMethod> getMethoden() {
        return methoden;
    }

    public void setMethoden(List<JavaMethod> methoden) {
        this.methoden = methoden;
    }

    public List<JavaClass> getKlassen() {
        return klassen;
    }

    public void setKlassen(List<JavaClass> klassen) {
        this.klassen = klassen;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

}
