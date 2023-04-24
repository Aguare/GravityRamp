void setup()
{
	
}
// Este cambio lo hizo canche
void loop() {
  if (BT.available()) {
    incomingByte = BT.read();
    Serial.println(incomingByte);
  }
}
