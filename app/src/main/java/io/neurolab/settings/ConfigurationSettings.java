package io.neurolab.settings;

public class ConfigurationSettings {

    private ServerSettings serverSettings = new ServerSettings();
    private NFBRelaxSettings nfbRelaxSettings = new NFBRelaxSettings();

    public ServerSettings getServerSettings() {
        return serverSettings;
    }

    public NFBRelaxSettings getNfbRelaxSettings(){
        return nfbRelaxSettings;
    }

}
