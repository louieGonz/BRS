#include <Arduino.h>
int incomingByte;


void setup(){
     Serial.begin(115200);
}

void loop(){

     if(Serial.available() > 0){
        incomingByte = Serial.read();
        Serial.println("Ahoy! ");
        Serial.print(incomingByte,HEX);
     }

}
