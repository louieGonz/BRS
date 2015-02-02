#include <Arduino.h>


 
//Interupt Pins on arduino boards
// zero exts on pin 2 , one exists on 3
const int interrupt_zero = 0;
const int interrupt_one = 1;
const int trigPin_1  = 7;
const int trigPin_2  =8;

volatile int switch_one = 0;
volatile int switch_two = 0;

void trigger();

void switch_it_one(){
     switch_one += 1; 
}

void switch_it_two(){
     switch_two +=1;
}


void setup() {
  // initialize serial communication:
  Serial.begin(9600);
  attachInterrupt(0,switch_it_one,CHANGE);
  attachInterrupt(1,switch_it_two,CHANGE);
  trigger();
}
 
void trigger(){
  
  //SENSOR 1
  pinMode(trigPin_1, OUTPUT);
  digitalWrite(trigPin_1, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_1, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin_1, LOW);

  //SENSOR 2
  pinMode(trigPin_2, OUTPUT);
  digitalWrite(trigPin_2, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin_2, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin_2, LOW);

}


void loop()
{
  
  Serial.println("Hello"); 
   Serial.println(switch_one,HEX);
  Serial.println(switch_two,HEX);
  
}


