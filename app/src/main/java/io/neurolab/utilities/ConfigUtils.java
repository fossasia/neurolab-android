package io.neurolab.utilities;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.neurolab.settings.ConfigurationSettings;

public class ConfigUtils {

    private static final String CONFIG_FILE_NAME = "config.json";

    public static ConfigurationSettings loadSettingsConfig(Context context){
        File configFile = new File(context.getFilesDir(), CONFIG_FILE_NAME);
        if(!configFile.isFile()){
            return new ConfigurationSettings();
        }

        String json = readConfigFile(configFile);

        Gson gson = new Gson();
        ConfigurationSettings configurationSettings = gson.fromJson(json, ConfigurationSettings.class);
        return configurationSettings;
    }

    public static void saveSettingsConfig(Context context, ConfigurationSettings configurationSettings){
        File configFile = new File(context.getFilesDir(), CONFIG_FILE_NAME);

        if(!configFile.isFile()){
           createConfigFile(configFile);
        }

        Gson gson = new Gson();
        String json = gson.toJson(configurationSettings);

        try(FileOutputStream fos = new FileOutputStream(configFile, false)){
            byte[] b = json.getBytes();
            fos.write(b);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void createConfigFile(File configFile){
        try{
            configFile.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static String readConfigFile(File configFile){
        String json = null;

        try(FileInputStream fis = new FileInputStream(configFile)) {
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

}
