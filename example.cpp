#include <SoftwareSerial.h>

SoftwareSerial BT(3, 2);  // 2 RXD, 3 TXD

char incomingByte;

void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
  BT.begin(9600);
}

void loop() {
  if (BT.available()> 0) {
    incomingByte = BT.read();
    Serial.println(incomingByte);
    if (incomingByte == '1') {
      BT.print("0 grados");
    } else if (incomingByte == '2'){
      BT.print("15 grados");
    }else if (incomingByte == '4') {
      BT.print("30 grados");
    } else if (incomingByte == '7') {
      BT.print("45 grados");
    } else if (incomingByte == '*') {
      BT.print("8456464.45 segundos");
    } else if (incomingByte == '!') {
      BT.print("8456464.45 segundos");
    }else{
      BT.print("No chona");
    }   
  }
}
