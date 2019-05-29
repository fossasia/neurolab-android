/*
  'sketch_serial_echo'
  The following code sets up the Arduino to simply echo whatever it receives on the serial port.
*/

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
}

// the loop routine runs over and over again forever:
void loop() {
  char incomingByte;
   // If there is a data stored in the serial receive buffer, read it and print it to the serial port as human-readable ASCII text.
  if(Serial.available()){  
    incomingByte = Serial.read();
    Serial.print(incomingByte);  
  }
}
