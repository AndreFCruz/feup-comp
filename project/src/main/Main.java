package main;

import worker.StaticWorkerFactory;
import worker.WorkerFactory;

import java.io.FileNotFoundException;
import java.util.List;

public class Main implements Runnable {
    Dispatcher dispatcher;
    Configuration configuration;
    FactoryManager factoryManager;

    String targetFile;
    String configFile;
    String patternsFile;

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Unable to parse command line arguments, usage: " + Dispatcher.getUsage());
            System.exit(0);
        }
        String targetFile = args[0];
        String configFile = args.length == 2 ? args[1] : null;
        String patternsFile = "patterns/Patterns.java";

        Main obj = new Main(targetFile, configFile, patternsFile);
        obj.run();
    }

    Main(String targetFile, String configFile, String patternsFile) {
        this.targetFile = targetFile;
        this.configFile = configFile;
        this.patternsFile = patternsFile;

        configuration = configFile != null ? Configuration.loadConfiguration(configFile) : null;
        dispatcher = initializeDispatcher(targetFile);

        factoryManager = initializeFactoryManager(patternsFile);
        dispatcher.setFactoryManager(factoryManager);
    }

    private Dispatcher initializeDispatcher(String targetFile) {
        Dispatcher dispatcher;
        if (configuration != null) {
            dispatcher = new Dispatcher(configuration);
        } else {
            dispatcher = new Dispatcher();
        }

        processTargetFile(targetFile);

        return dispatcher;
    }

    private void processTargetFile(String targetFile) {
        try {
            dispatcher.readSpoonTarget(targetFile);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }
    }

    // Currently path is hardcoded, in the future should be received as command line arg
    private FactoryManager initializeFactoryManager(String patternsFile) {
        FactoryManager manager = new FactoryManager();
        addStaticWorkerFactories(manager, configuration);
        addDynamicWorkerFactories(manager, patternsFile);

        return manager;
    }

    private void addStaticWorkerFactories(FactoryManager manager, Configuration configuration) {
        List<StaticWorkerFactory> factories = configuration.getActiveDynamicFeatures();
        for (WorkerFactory factory : factories) {
            manager.addWorkerFactory(factory);
        }
    }

    private void addDynamicWorkerFactories(FactoryManager manager, String patternsFile) {
        // TODO
    }

    @Override
    public void run() {
        dispatcher.run();
    }
}
