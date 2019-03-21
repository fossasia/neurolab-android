package io.neurolab.tools;

import java.io.File;
import java.net.URL;

public class ResourceManager {
    public static ResourceManager resourceManager;
    private static ClassLoader classLoader;
    public static boolean loadFromDisk = false;

    public static ResourceManager getInstance() {
        if (resourceManager == null) {
            resourceManager = new ResourceManager();
            classLoader = resourceManager.getClass().getClassLoader();
        }
        return resourceManager;
    }

    public File getResource(String resourceName) {
        System.out.println("loading resource '" + resourceName + "'");
        if (resourceName.startsWith("ABSPATH:")) {
            return new File(resourceName.substring(8));
        }
        if (loadFromDisk)
            return new File("./resources/" + resourceName);

        URL resource = classLoader.getResource(resourceName);
        if (resource == null)
            return new File(resourceName);
        else
            return new File(classLoader.getResource(resourceName).getFile().replaceAll("%20", " "));
    }

}