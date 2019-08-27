package io.neurolab.main.task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.neurolab.main.NFBServer;
import io.neurolab.interfaces.Task;

public class BinaryFileOutputTask implements Task {

    private static final byte[] FileOutputStream = null;
    private NFBServer nfbServer;
    private FileOutputStream out = null;
    private byte[] stopBytes;
    private String fileName = "defaultFileOutput.bw";
    private boolean write = false;

    public BinaryFileOutputTask(NFBServer nfbServer) {
        this.nfbServer = nfbServer;
        this.stopBytes = doubleToByteArray(Double.MAX_VALUE);
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

                    out.write(longToByteArray(nfbServer.getCurrentTimestamp()));
                    for (int c = 0; c < currentPacket.length; c++)
                        out.write(doubleToByteArray(currentPacket[c]));
                    out.write(stopBytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void init() {
        try {
            if (out != null)
                out.close();
            out = new FileOutputStream(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
