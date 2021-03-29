package metriker;

public class JavaMethod {

    public String name;
    public int loc;
    public int mccabe = 1;

    public JavaMethod(String signatur, String strippedCode) {

        // throws entfernen...
        if (signatur.contains("throws ")) {
            this.name = signatur.substring(0, signatur.indexOf("throws "));
        } else {
            this.name = signatur;
        }

        // add space behind ,
        this.name = name.replace(",", ", ");
        this.name = name.replace("  ", " ");

        // calculate loc of class...
        this.loc = strippedCode.split("\n").length;

        // calculate mccabe...
        String strippedCodeWithoutNewLines = strippedCode.replace("\n", " ");
        while (strippedCodeWithoutNewLines.contains("  ")) {
            strippedCodeWithoutNewLines = strippedCodeWithoutNewLines.replace("  ", " ");
        }
        mccabe += JavaClass.countOccurances(strippedCodeWithoutNewLines, " if(");
        mccabe += JavaClass.countOccurances(strippedCodeWithoutNewLines, " while(");
        mccabe += JavaClass.countOccurances(strippedCodeWithoutNewLines, " case ");
        mccabe += JavaClass.countOccurances(strippedCodeWithoutNewLines, "?");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public int getMccabe() {
        return mccabe;
    }

    public void setMccabe(int mccabe) {
        this.mccabe = mccabe;
    }
}
