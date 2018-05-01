package pattern_matcher;


import spoon.Launcher;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtMethod;
import spoon.support.reflect.declaration.CtClassImpl;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PatternDefinitions {
    private Map<Class<?>, ArrayList<CtBlock>> patterns;

    public PatternDefinitions(String targetDefinitions) throws FileNotFoundException {
        if (!Files.exists(Paths.get(targetDefinitions)))
            throw new FileNotFoundException(targetDefinitions + " does not exist.");

        patterns = new HashMap<>();

        processPatternDefinitions(targetDefinitions);
    }

    private void processPatternDefinitions(String targetDefinitions) {

        Launcher launcher = new Launcher();
        launcher.addInputResource(targetDefinitions); //Analyze the patterns file
        launcher.getEnvironment().setNoClasspath(true); // Semantic analysis kinda off
        launcher.buildModel();

        List <CtClassImpl> classElement = launcher.getModel().getElements(ctElement -> (ctElement.getClass() == CtClassImpl.class));
        if (classElement.size() > 1) {
            System.err.println("There can only be the main class defined in the Patterns file.");
            return;
        }

        updatePatternsContainer(classElement.get(0).getMethods());
    }

    private void updatePatternsContainer(Set<CtMethod> methods) {

        for (CtMethod method: methods) {
            CtBlock methodBody = method.getBody();

            if (methodBody.getStatement(0) == null) {
                System.out.println("Warning: Empty Pattern defined");
            }
            Class node =  methodBody.getStatement(0).getClass();

            if (patterns.containsKey(node))
                patterns.get(node).add(methodBody);
            else {
                ArrayList<CtBlock> placeholder = new ArrayList<>();
                placeholder.add(methodBody);
                patterns.put(node, placeholder);
            }
        }
    }

    public Map<Class<?>, ArrayList<CtBlock>> getPatterns() {
        return patterns;
    }
}
