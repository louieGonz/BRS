 #include <QueueList.h>
 //FOR SESNORS CANNOT BE RUN CONCURRENTLY
const int trigPin_1 = 2;
const int trigPin_2 =12;
const int echoPin_1 = 4;
const int echoPin_2 = 8; 
const int packet_size = 16;
const byte err_packet[] = {0xEF};
int startByte =0;

byte* packet;
QueueList<long> q;
void setup() {
  // initialize serial communication:
  Serial.begin(115200);
  packet = (byte*)malloc(packet_size*sizeof(byte));
  srand(42);
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
     //Probably only need 2byte representation

     byte_array[start] = (num & 0x000000FFUL);
     byte_array[start+1] = (num & 0x0000FF00UL) >> 4;
     //byte_array[start+2] = (num & 0x00FF0000UL) >> 16;
     //byte_array[start+3] = (num & 0xFF000000UL) >> 24;
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
     packet[0] = 0x00; 
     packet[1] = 0xFF;               // start 
     long_to_byte(r_1,packet,2);      // data
     long_to_byte(r_2,packet,4);
     long_to_byte(r_2,packet,6);
     long_to_byte(r_2,packet,8);
     long_to_byte(r_2,packet,10);
     long_to_byte(r_2,packet,12);
     packet[packet_size-2] =  0xEE; //end
     packet[packet_size-1] =  0x00;  
           
     Serial.write(packet,packet_size);

}

void serialEvent(){
   int sig_byte = Serial.read();
   long buff[6] = {0,0,0,0,0,0};
   if(sig_byte == 0xFF){
      int i = 0;
      while(!q.isEmpty() && i < 5){
            buff[i] = q.pop();  
            ++i;    
       }
       send_packet(buff[0],buff[1],1);
    }else{
       Serial.write(0xEE);
    }

}

void loop()
{
   q.push(get_distance(trigPin_1,echoPin_1));
   q.push(get_distance(trigPin_2,echoPin_2)); 
   delay(1000);
}    
     
     
long microsecondsToCentimeters(long microseconds)
{    
      // The speed of sound is 340 m/s or 29 microseconds per centimeter.
      // The ping travels out and back, so to find the distance of the
      // object we take half of the distance travelled.
      return microseconds / 29 / 2;
}    
     
    
    
    
    
