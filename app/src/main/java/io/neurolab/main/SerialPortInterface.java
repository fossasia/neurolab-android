package io.neurolab.main;

import java.util.ArrayList;
import java.util.HashMap;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialPortInterface implements InputInterface {

    static SerialPort serialPort;
    static String leftOver = "";

    static int lastSampleCount = 0;
    static int leftOverIndex = 0;

    static double[] lastBrokenSamples = new double[4];

    static boolean brokenLine = false;
    static DataReceiver receiver;

    public static HashMap<String, Float> lookupTable = NeuroUtils.getBrainduinoDefaultLookupTable();
    public static ArrayList<double[]> data;
    private static String address;
    private static int baudRate;

    static int numberOfChannels;

    private boolean connectionSuccessful;
    private boolean bit24;
    private boolean recording = false;

    private String fileName = "";
    private SerialPortReader serialPortReader;

    public SerialPortInterface(DataReceiver receiver, int numberOfChannels, String address, int baudRate) {
        this(receiver, numberOfChannels, address, baudRate, false);
    }

    public SerialPortInterface(DataReceiver receiver, int numberOfChannels, String address, int baudRate, boolean bit24) {
        this.receiver = receiver;
        this.address = address;
        this.baudRate = baudRate;
        this.numberOfChannels = numberOfChannels;
        this.data = new ArrayList<double[]>();
        this.bit24 = bit24;

        SerialPortReader.bit24 = bit24;

        serialPort = new SerialPort(address);

        try {
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
            serialPort.openPort();
            serialPort.setParams(baudRate, 8, 1, 0);
            serialPort.setEventsMask(mask);// Set mask
            serialPort.addEventListener(serialPortReader = new SerialPortReader());
            connectionSuccessful = true;
            recording = false;
        } catch (SerialPortException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void run() {

    }

    @Override
    public int shutDown() {
        if (serialPort.isOpened()) {
            try {
                serialPort.removeEventListener();
                serialPort.closePort();
                return 1;
            } catch (SerialPortException e1) {
                e1.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }

    @Override
    public boolean sendCommand(String string) {
        try {
            return serialPort.writeString(string);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean record(String filename) {
        if (this.serialPortReader!=null)
            return recording = this.serialPortReader.record(filename);
        else
            return recording = false;
    }

    @Override
    public void stopRecording() {
        if (this.serialPortReader != null)
            this.serialPortReader.stopRecording();
    }

    @Override
    public boolean isRecording() {
        return recording;
    }
    
}