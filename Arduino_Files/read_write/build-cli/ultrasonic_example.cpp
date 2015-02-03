#include <Arduino.h>

 
 //FOR SESNORS CANNOT BE RUN CONCURRENTLY

const int trigPin_1 = 2;
const int trigPin_2 =12;
const int echoPin_1 = 4;
const int echoPin_2 = 8; 
const int packet_size = 10;
const byte err_packet[] = {0xEF};
int startByte =0;

void setup() {
  // initialize serial communication:
  Serial.begin(115200);
  startByte = Serial.read();

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

void long_to_byte(long num, byte* byte_array, int start){
     //Manipulates byte array, start contains lowest order byte
     //Shift back and add to obtain original data

     byte_array[start] = (num & 0x000000FFUL);
     byte_array[start+1] = (num & 0x0000FF00UL) >> 4;
     byte_array[start+2] = (num & 0x00FF0000UL) >> 16;
     byte_array[start+3] = (num & 0xFF000000UL) >> 24;
}


long get_distance(int trig_pin, int echo_pin){

      //Transmit
      pinMode(trig_pin, OUTPUT);
      digitalWrite(trig_pin, LOW);
      delayMicroseconds(2);
      digitalWrite(trig_pin, HIGH);
      delayMicroseconds(10);
      digitalWrite(trig_pin, LOW);
     
      //Recieve
      pinMode(echo_pin, INPUT);
      long duration = pulseIn(echo_pin, HIGH);
      return  microsecondsToInches(duration);
}







void send_packet(long r_1, long r_2,int err){
     /* Need to implement error packet*/    
 
     byte* packet = (byte*)malloc(packet_size*sizeof(byte));
     packet[0] = 0xFF;                // start 
     long_to_byte(r_1,packet,1);      // data
     long_to_byte(r_2,packet,5);
     packet[packet_size-1] =  0xEE;  //end
           
     Serial.write(packet,packet_size);

}

void loop()
{
   if(Serial.available()>0){
      int startByte = Serial.read();
      if(startByte == (byte) 0xFF){
         long r1 =  get_distance(trigPin_1, echoPin_1);
         long r2 =  get_distance(trigPin_2, echoPin_2);
         send_packet(r1,r2,1);
      } 
   } 
}    
     
     
long microsecondsToCentimeters(long microseconds)
{    
      // The speed of sound is 340 m/s or 29 microseconds per centimeter.
      // The ping travels out and back, so to find the distance of the
      // object we take half of the distance travelled.
      return microseconds / 29 / 2;
}    
     
    
    
    
    
