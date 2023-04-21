#include <SoftwareSerial.h> // Incluimos la librería  SoftwareSerial
SoftwareSerial BT(2, 3); // Definimos los pines RX y TX del Arduino conectados al Bluetooth

char NAME[21] = "ARQ1-BT";
char BPS = '4';
char PASS[5] = '1234';

void setup() {
  BT.begin(9600); // Inicializamos el puerto serie BT que hemos creado

  pinMode(13, OUTPUT);
  digitalWrite(13, HIGH);
  delay(4000);

  digitalWrite(13, LOW);

  BT.print("AT");
  delay(1000);
  BT.print("AT+NAME");
  BT.print(NAME);
  delay(1000);

  BT.print("AT+BAUD");
  BT.print(BPS);
  delay(1000);

  BT.print("AT+PIN");
  BT.print(PASS);
  delay(1000);
}

void loop() {
  digitalWrite(13, !digitalRead(13));
  delay(300);
}