package io.neurolab.tools;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.net.URL;

public class ResourceManager {
    public static ResourceManager resourceManager;
    private static ClassLoader classLoader;
    public static boolean loadFromPhone = false;
    private Context context;

    private static String TAG = ResourceManager.class.getCanonicalName();

    public static ResourceManager getInstance() {
        if (resourceManager == null) {
            resourceManager = new ResourceManager();
            classLoader = resourceManager.getClass().getClassLoader();
        }
        return resourceManager;
    }

    public File getResource(Context context, String resourceName) {
        this.context = context;
        Log.d(TAG, "loading resource '" + resourceName + "'");
        if (resourceName.startsWith("ABSPATH:")) {
            return new File(this.context.getFilesDir(), resourceName.substring(8));
        }
        if (loadFromPhone)
            return new File(this.context.getFilesDir(), "./resources/" + resourceName);

        URL resource = classLoader.getResource(resourceName);
        if (resource == null)
            return new File(resourceName);
        else
            return new File(classLoader.getResource(resourceName).getFile().replaceAll("%20", " "));
    }

}