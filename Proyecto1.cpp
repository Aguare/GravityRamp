class NewPing {
public:
  NewPing(int TRIGGER_PIN, int ECHO_PIN, int MAX_DISTANCE) {
    trigPin = TRIGGER_PIN;
    echoPin = ECHO_PIN;
    maxDistance = MAX_DISTANCE;

    pinMode(trigPin, OUTPUT);
    pinMode(echoPin, INPUT);
  }

  int ping_cm() {
    // Clears the trigPin
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);
    // Sets the trigPin on HIGH state for 10 micro seconds
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
    // Reads the echoPin, returns the sound wave travel time in microsecond
    long duration = pulseIn(echoPin, HIGH);
    // Calculating the distance
    int distance = duration * 0.034 / 2;
    // Checks out of range
    if (distance > maxDistance) {
      distance = 0;
    }
    return (distance);
  }

private:
  int trigPin;
  int echoPin;
  int maxDistance;
};

#include <Servo.h>
#include <SoftwareSerial.h>

//BT COMUNICATION
SoftwareSerial BT(3, 2);  // 2 RXD, 3 TXDB
char incomingByte;

// SERVO MOTORS
#define SERVOPIN 13
#define SERVO_RAMP 12
int servo_angle = 0;
Servo servo_1;
Servo servo_2;


// ULTRASONIC SENSORS
#define TRIGGER2_PIN 8
#define ECHO2_PIN 9
#define TRIGGER_PIN 10
#define ECHO_PIN 11
#define MAX_DISTANCE 200
#define MIN_DISTANCE 5
NewPing sensor_ult1(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
NewPing sensor_ult2(TRIGGER2_PIN, ECHO2_PIN, MAX_DISTANCE);


void setup() {
  Serial.begin(9600);
  BT.begin(9600);
  servo_1.attach(SERVOPIN);
  servo_2.attach(SERVO_RAMP);
  servo_1.write(0);
  servo_2.write(90);
}

void loop() {
  if (BT.available() > 0) {
    incomingByte = BT.read();
    Serial.println(incomingByte);
    if (incomingByte == '1') {
      servo_angle = 0;
      BT.print("0 grados");
    } else if (incomingByte == '2'){
      servo_angle = 15;
      BT.print("15 grados");
    }else if (incomingByte == '4') {
      servo_angle = 30;
      BT.print("30 grados");
    } else if (incomingByte == '7') {
      servo_angle = 45;
       BT.print("45 grados");
    } else if (incomingByte == '*') {
      calculate_distance();
      BT.print("8456464.45 segundos");
    }else{
      BT.print("No chona");
    }
    move_servo(servo_angle);
  }
}

// Function to move the servos
void move_servo(int angle) { servo_1.write(angle); }

void calculate_distance() {
  long time_start = 0;
  long time_end = 0;
  int distance = sensor_ult1.ping_cm();
  int distance2 = sensor_ult2.ping_cm();
  servo_2.write(0);

  while (distance > MIN_DISTANCE) {
    if (time_start == 0) {
      // Get the time when the first sensor change the distance
      time_start = millis();
    }

    // Calculate distance with two sensors
    distance = sensor_ult1.ping_cm();
    distance2 = sensor_ult2.ping_cm();
  }

  // if distance is less than MIN_DISTANCE, then the sensor detected an object
  if (distance2 <= MIN_DISTANCE) {
    time_end = millis();
    // Calculate time elapsed
    float time_elapsed = (time_end - time_start);

    Serial.print(
        "Tiempo transcurrido hasta que el primer sensor detectó el mínimo: ");
    Serial.print(time_elapsed);
    Serial.println(" ms");
  }
  delay(100);
}
