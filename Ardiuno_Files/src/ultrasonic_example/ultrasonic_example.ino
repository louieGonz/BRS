
 
 //FOR SESNORS CANNOT BE RUN CONCURRENTLY

const int trigPin_1 = 2;
const int trigPin_2 =12;
const int echoPin_1 = 4;
const int echoPin_2 = 8; 
void setup() {
  // initialize serial communication:
  Serial.begin(9600);
}
 
void loop()
{
  // establish variables for duration of the ping, 
  // and the distance result in inches and centimeters:
  long duration_1, duration_2, inches_1, inches_2;
 
  // The sensor is triggered by a HIGH pulse of 10 or more microseconds.
  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
  
  //SENSOR 1
  pinMode(trigPin_1, OUTPUT);
  digitalWrite(trigPin_1, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_1, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin_1, LOW);

  // Read the signal from the sensor: a HIGH pulse whose
  // duration is the time (in microseconds) from the sending
  // of the ping to the reception of its echo off of an object.
  pinMode(echoPin_1, INPUT);
  duration_1 = pulseIn(echoPin_1, HIGH);
 
  
  //SENSOR 2
  pinMode(trigPin_2,OUTPUT);
  digitalWrite(trigPin_2, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_2, HIGH);
  digitalWrite(trigPin_2, LOW);
  pinMode(echoPin_2, INPUT);
  duration_2 = pulseIn(echoPin_2, HIGH);
  
  
  inches_1 = microsecondsToInches(duration_1);
  inches_2 = microsecondsToInches(duration_2);


  delayMicroseconds(10);
  Serial.print(inches_1);
  Serial.print(" sensor 1 (in),  ");
  Serial.print(inches_2);
  Serial.print(" sensor 2 (in)");
  Serial.println();
  
  delay(100);
}
 
long microsecondsToInches(long microseconds)
{
  // According to Parallax's datasheet for the PING))), there are
  // 73.746 microseconds per inch (i.e. sound travels at 1130 feet per
  // second).  This gives the distance travelled by the ping, outbound
  // and return, so we divide by 2 to get the distance of the obstacle.
  // See: http://www.parallax.com/dl/docs/prod/acc/28015-PING-v1.3.pdf
  return microseconds / 74 / 2;
}
 
long microsecondsToCentimeters(long microseconds)
{
  // The speed of sound is 340 m/s or 29 microseconds per centimeter.
  // The ping travels out and back, so to find the distance of the
  // object we take half of the distance travelled.
  return microseconds / 29 / 2;
}
