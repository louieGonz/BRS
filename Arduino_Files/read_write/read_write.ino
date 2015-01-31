int incomingByte;


void setup(){
     Serial.begin(115200);
}

void loop(){

     if(Serial.available() > 0){
        incomingByte = Serial.read();
        if(incomingByte = 0xFF)
           Serial.println("Ahoy!");
     }

}
