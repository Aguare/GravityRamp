void setup()
{
	
}

void loop() {
  if (BT.available()) {
    incomingByte = BT.read();
    Serial.println(incomingByte);
  }
}
