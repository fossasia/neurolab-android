#include<SoftwareSerial.h>

SoftwareSerial softSerial(2,3);
char signal = 0;
char data = 0;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  softSerial.begin(38400);
  pinMode(13,OUTPUT);  // LED attached at pin 13 to signal switching between modes.
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available() >0)
  {
    signal = Serial.read();
    
    Serial.write(signal);
    switch(signal) {
      case 1:                         // When "1" is sent, communication takes place via Bluetooth
      readDataUsingBluetooth();
      case 0:                         // When "0" is sent, communication takes place via USB
      readDataUsingUSB();
    }
  }
}
void readDataUsingBluetooth() {
  
  digitalWrite(13, HIGH);
  data = Serial.read();                // Read data while in BLuetooth Mode
  while(data!=0)
  {
  data = Serial.read();
  Serial.write(data);
  }
}

void readDataUsingUSB() {
  digitalWrite(13, LOW);
  data = Serial.read();               // // Read data while in USB Mode
  while(data!=1)
  {
    data = Serial.read();
    softSerial.write(data);
  }
}
