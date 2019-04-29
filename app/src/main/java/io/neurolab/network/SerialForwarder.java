package io.neurolab.network;

import com.illposed.osc.OSCPortOut;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

public class SerialForwarder {

    private SerialPort serialPort;
    private static int n = 0;

    public static void main(String[] args) throws UnknownHostException, SocketException, InterruptedException {
        SerialForwarder of = new SerialForwarder("/dev/ttyACM0", 230400);
        of.connect();
        Thread.sleep(1000);
        for (int i = 0; i < 32; i += 4) {
            of.forwardMessage(new int[]{i, i, i, i, i, i});
            Thread.sleep(25);
        }
        for (int i = 127; i > 1; i--) {
            of.forwardMessage(new int[]{i, i, i, i, i, i});
            Thread.sleep(25);
        }
        of.disconnect();

    }

    private DatagramSocket socket = null;
    private String address;
    private int baudrate;
    private String oscAddress;
    private OSCPortOut oscPortOut;
    private boolean connected = false;
    private boolean connectionSuccessful;

    public SerialForwarder() {
    }

    public SerialForwarder(String address, int baudRate) {
        this.address = address;
        this.baudrate = baudRate;

    }

    public boolean connect() {
        return connect(this.address, this.baudrate);
    }


    public boolean connect(String address, int baudRate) {
        serialPort = new SerialPort(address);
        connected = false;
        try {
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
            serialPort.openPort();
            serialPort.setParams(baudRate, 8, 1, 0);
            serialPort.setEventsMask(mask);// Set mask
            connected = true;
        } catch (SerialPortException ex) {
            System.err.println(ex);
        }
        return connected;
    }


    public boolean isConnected() {
        return connected;
    }

    public void forwardMessage(int[] message) {
        String messageString = "";
        for (int i = 0; i < message.length - 1; i++)
            messageString += Integer.toHexString(Math.max(0, Math.min(message[i], 255))) + " ";
        messageString += Integer.toHexString(Math.max(0, Math.min(message[message.length - 1], 255))) + "\n";

        try {
            System.out.println("messageString:" + messageString);
            serialPort.purgePort(SerialPort.PURGE_TXCLEAR | SerialPort.PURGE_RXCLEAR);
            serialPort.writeString(messageString);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

    }

    public boolean disconnect() {
        try {
            serialPort.closePort();
            connected = false;
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return !connected;
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
        try {
            System.out.println(n + "\t:" + serialPort.readString().trim());
            n += 1;
            if (n > 500)
                this.disconnect();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

    }

}
