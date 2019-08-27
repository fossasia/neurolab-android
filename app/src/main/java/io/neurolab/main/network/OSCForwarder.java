package io.neurolab.main.network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPortOut;

public class OSCForwarder {

    public static void main(String[] args) throws UnknownHostException, SocketException, InterruptedException {
        OSCForwarder of = new OSCForwarder();
    }

    private DatagramSocket socket = null;
    private String address;
    private String port;
    private String oscAddress;
    private OSCPortOut oscPortOut;
    private boolean connected = false;

    public OSCForwarder() {

    }

    public OSCForwarder(String address, String port) {
        this.address = address;
        this.port = port;
        connect(address, port);
    }


    public boolean connect(String address, String port) {
        InetAddress addr;

        try {
            addr = InetAddress.getByName(address);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            return false;
        }

        try {
            oscPortOut = new OSCPortOut(addr, Integer.valueOf(port));
            connected = true;
        } catch (NumberFormatException | SocketException e1) {
            e1.printStackTrace();
            connected = false;
            return false;
        }

        int i = 1;
        Object[] args = new Object[1];
        return true;
    }

    public void forwardBundle(OSCBundle bundle) {
		for (OSCPacket packet : bundle.getPackets()) {
			System.out.println(packet.toString());
		}

        try {
            oscPortOut.send(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void forwardMessage(OSCMessage message) {
        try {
            oscPortOut.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean disconnect() {
        oscPortOut.close();
        connected = false;
        return true;
    }

}
