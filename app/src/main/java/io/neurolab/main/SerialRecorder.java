package io.neurolab.main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SerialRecorder implements Runnable {

    private PrintWriter out;

    public void run() {
    }

    public void write(long timestamp, String data) {
        out.println(timestamp + " " + data);
    }

    public boolean recordToFile(String fileName) {
        if (out != null)
            out.close();

        try {
            out = new PrintWriter(fileName, "UTF-8");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void stop() {
        if (out != null)
            out.close();
    }
}
