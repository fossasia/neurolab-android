package io.neurolab.main;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

class SerialPortReader implements SerialPortEventListener {

    static boolean bit24 = false;
    private boolean recording = false;
    private String fileName = "";
    private SerialRecorder serialRecorder;
    private Thread serialRecorderThread;
    private long currentTimestamp = 0;

    public SerialPortReader() {
        serialRecorderThread = new Thread(serialRecorder = new SerialRecorder());
        serialRecorderThread.start();
    }

    public boolean record(String fileName) {
        this.fileName = fileName;
        return recording = serialRecorder.recordToFile(fileName);
    }

    public boolean isRecording() {
        return recording;
    }

    public void stopRecording() {
        serialRecorder.stop();
        recording = false;
    }

    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {
            try {
                String input = SerialPortInterface.serialPort.readString();

                if ((input==null) || (input.length()==0)) {
                    System.err.println("SERIAL PORT RETURNED NULL");
                    return;
                }

                String[] inputLines = input.split("\r?\n|\r", -1);

                SerialPortInterface.data.clear();
                String[] inputLine = null;

                for (int i = 0; i < inputLines.length; i++) {
                    if (inputLines[i].length() < 15) {
                        if (SerialPortInterface.leftOver.length() > 0) {
                            inputLines[i] = (SerialPortInterface.leftOver + inputLines[i]);
                            SerialPortInterface.leftOver = "";
                            if (inputLines[i].length() < 15)
                                continue;
                        } else {
                            SerialPortInterface.leftOver = inputLines[i];
                            currentTimestamp = System.nanoTime();
                            continue;
                        }
                    }

                    if (recording) {
                        if (SerialPortInterface.leftOver.equals(""))
                            currentTimestamp = System.nanoTime();
                        serialRecorder.write(currentTimestamp, inputLines[i]);
                    }

                    if ((inputLines[i].contains("i")) || (inputLines[i].contains("s")))
                        continue;

                    inputLine = inputLines[i].split("\t", -1);
                    double[] samples = new double[SerialPortInterface.numberOfChannels];

                    for (int c = 0; c < SerialPortInterface.numberOfChannels; c++) {
                        if (SerialPortReader.bit24) {
                            samples[c] = 10000d* NeuroUtils.parseUnsignedHex(inputLine[c*2] + inputLine[(c*2)+1])/16777216d;
                        }
                        else {
                            if (SerialPortInterface.lookupTable.containsKey(inputLine[c]))
                                samples[c] = SerialPortInterface.lookupTable.get(inputLine[c]);
                            else
                                samples[c] = 0;
                        }
                    }
                    SerialPortInterface.data.add(samples.clone());
                }
                SerialPortInterface.receiver.appendData(SerialPortInterface.data);
            } catch (SerialPortException ex) {
                System.out.println(ex);
            }
        } else if (event.isCTS()) {
            if (event.getEventValue() == 1) {
                System.out.println("CTS - ON");
            } else {
                System.out.println("CTS - OFF");
            }
        } else if (event.isDSR()) {
            if (event.getEventValue() == 1) {
                System.out.println("DSR - ON");
            } else {
                System.out.println("DSR - OFF");
            }
        }
    }

}