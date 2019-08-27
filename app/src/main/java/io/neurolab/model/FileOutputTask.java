package io.neurolab.model;

import java.io.PrintWriter;
import java.nio.ByteBuffer;

import io.neurolab.interfaces.Task;
import io.neurolab.main.NFBServer;

public class FileOutputTask implements Task {

    private NFBServer nfbServer;
    private PrintWriter out = null;
    private byte[] stopBytes;
    private String fileName = "defaultFileOutput.bw";
    private boolean write = false;

    public FileOutputTask(NFBServer nfbServer) {
        this.nfbServer = nfbServer;
        this.stopBytes = doubleToByteArray(Double.MAX_VALUE);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        init();
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    @Override
    public void run() {
        if ((write) && (!nfbServer.getInputData().isEmpty())) {
            try {
                for (int s = 0; s < nfbServer.getNumSamples(); s++) {
                    double[] currentPacket = nfbServer.getInputData().get(s);

                    out.print(nfbServer.getCurrentTimestamp() + ",");
                    for (int c = 0; c < currentPacket.length; c++)
                        out.print(currentPacket[c] + ",");
                    out.println("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] doubleToByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public static byte[] longToByteArray(long value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(value);
        return bytes;
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    @Override
    public void init() {
        try {
            if (out != null)
                out.close();
            out = new PrintWriter(fileName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
