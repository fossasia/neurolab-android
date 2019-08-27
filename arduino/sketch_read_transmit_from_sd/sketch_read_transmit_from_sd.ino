#include <SD.h>  // Load SD library
#include <SPI.h>

int baudRate = 9600;
int chipSelect = 4;  // chip select pin for the MicroSD Card Adapter
String fileName = "Dataset1.csv";

/*
   try initializing the sd card; returns true if successful else returns false.
*/
bool initializeSDCard() {
  // Initializing SD card...
  if (!SD.begin(chipSelect)) {  // Try initializing the SD card
    // Initialization Failed 
    return false; // if return value is false, something went wrong
  }
  // Initialization Successful
  return true;
}

/*
  try opening the file with the name passed in as the argument from the sd card. Returns the file object in a true or false context accordingly.
*/
File openTheFileFromSDCard(String fileName) {
  File file = SD.open(fileName);   // Grabing the file reference object
  if (!file) {                    // If the file doesn't exist this would return false
    Serial.println("error opening: " + fileName);
    return file;
  }
  return file;
}

/*
  Reads the file passed in as an argument from the SD card and transmit it over the serial output channel line by line with '\n' as the line delimitter. Skips null lines.
*/
void readFromSDCardToSerialOutputLineByLine(File file) {
  // read from the file until there's nothing else in it
  String line;
  // we could use something here like line.reserve(numberOfBytes) to avoid heap memory fragmentation
  while (file.available()) {
    line = file.readStringUntil('\n');
    line.trim();
    if (line != "") {
      Serial.println(line);
      // TODO: Delay can be added here to transmit data over the serial channel for a specified constant rate.
    }  // Skip blank null lines.
  }
  Serial.println("Recorded");
  file.close();
}

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(baudRate);

  pinMode(chipSelect, OUTPUT);  // chip select pin must be set to OUTPUT mode

  if (initializeSDCard()) {   // If the sd card is initialized successfully
    File file = openTheFileFromSDCard(fileName);     // If null object is returned then either the file doesn't exist
    if (file) {
      readFromSDCardToSerialOutputLineByLine(file);
    }
  }
}

// the loop routine runs over and over again forever:
void loop() {
  /*
    char incomingByte;
    // If there is a data stored in the serial receive buffer, read it and print it to the serial port as human-readable ASCII text.
    if(Serial.available()){
    incomingByte = Serial.read();
    Serial.print(incomingByte);
    }
  */
}
