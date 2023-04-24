#include <SoftwareSerial.h>

SoftwareSerial BT(10, 11); // 10 RXD, 11 TXD

char incomingByte;

void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
  BT.begin(9600);
}
// Este cambio lo hizo canche
void loop() {
  if (BT.available()) {
    incomingByte = BT.read();
    Serial.println(incomingByte);
  }
}
