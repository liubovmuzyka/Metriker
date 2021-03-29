package metriker;

import java.util.ArrayList;
import java.util.List;

public class JsonContainer {

    public List<JavaClass> classes = new ArrayList<>();

    public List<JavaClass> getClasses() {
        return classes;
    }

    public void setClasses(List<JavaClass> classes) {
        this.classes = classes;
    }
}
