package io.neurolab.model;

import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import io.neurolab.tools.ResourceManager;

public class Config {

    public enum audiofeedback_params {sample, volume, x, y}

    public enum server_settings_params {serial_address, pro_mode, pp, load_from_disk, audiofeedback, simulation, bit24, fir, avg, tf}

    public enum osc_settings_params {mode, address, ip, port}

    public enum serial_settings_params {address, baudrate, message, mode}

    public static String serial_settings = "serial_settings";
    public static String osc_settings = "osc_settings";
    public static String audiofeedback = "default_audiofeedback";
    public static String server_settings = "server_settings";

    private File iniFile;
    public Preferences prefs;

    public Config(String filename) {
        iniFile = new File(filename);
        System.out.println("opening config from " + filename);
        try {
            loadConfig(iniFile);
        } catch (BackingStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadConfig(File iniFile) throws BackingStoreException, IOException {
        System.out.println(iniFile);

        System.out.println("loaded config");
        return true;
    }

    public boolean setPref(String section, String key, String value) {
        try {
            prefs.sync();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getPref(String section, String key) {
        try {
            if (prefs.nodeExists(section))
                return prefs.node(section).get(key, null);
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean store() {
        try {
            System.out.println("stored config");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
