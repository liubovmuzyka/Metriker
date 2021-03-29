package metriker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class Main {

    private static FileWriter Classesold;
    private static FileWriter Classes;
    
    public static void main(String[] args) throws IOException {
        
        File oldDir = new File("C:\\Users\\Liubov\\GitHub\\Portfolio_FST_2021\\Metriker\\Metriker\\javapacman-1");
        File newDir = new File("C:\\Users\\Liubov\\GitHub\\Portfolio_FST_2021\\Metriker\\Metriker\\javapacman-2");
        JSONObject jo1;
        {
            JsonContainer object1 = new JsonContainer();
            List<JavaFile> newFiles = Arrays.asList(oldDir.listFiles()).stream().map(e -> new JavaFile(e)).collect(Collectors.toList());
            for (JavaFile file : newFiles) {
                if (file.includedClasses.size() > 0) {
                    JavaClass publicClasse = null;
                    for (JavaClass classe : file.includedClasses) {
                        if (classe.getName().equals(file.fileName)) {
                            publicClasse = classe;
                            object1.classes.add(classe);
                        }
                    }
                    if (publicClasse == null) {
                        throw new RuntimeException();
                    }
                    for (JavaClass classe : file.includedClasses) {
                        if (classe != publicClasse) {
                            publicClasse.klassen.add(classe);
                        }
                    }
                }
            }
            jo1 = new JSONObject(object1);
        }

        JSONObject jo2;
        {
            JsonContainer object2 = new JsonContainer();
            List<JavaFile> newFiles = Arrays.asList(newDir.listFiles()).stream().map(e -> new JavaFile(e)).collect(Collectors.toList());
            for (JavaFile file : newFiles) {
                if (file.includedClasses.size() > 0) {
                    JavaClass publicClasse = null;
                    for (JavaClass classe : file.includedClasses) {
                        if (classe.getName().equals(file.fileName)) {
                            publicClasse = classe;
                            object2.classes.add(classe);
                        }
                    }
                    if (publicClasse == null) {
                        throw new RuntimeException();
                    }
                    for (JavaClass classe : file.includedClasses) {
                        if (classe != publicClasse) {
                            publicClasse.klassen.add(classe);
                        }
                    }
                }
            }
            jo2 = new JSONObject(object2);
        }
        Classesold = new FileWriter("C:\\Users\\Liubov\\GitHub\\Portfolio_FST_2021\\Metriker\\Metriker\\Classesold.json");
        Classes = new FileWriter("C:\\Users\\Liubov\\GitHub\\Portfolio_FST_2021\\Metriker\\Metriker\\Classes.json");
        try {

            Classesold.write(jo1.toString(1));
            Classes.write(jo2.toString(1));

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                Classesold.close();
                Classes.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
}
