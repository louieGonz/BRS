#include <Arduino.h>

 
 //FOR SESNORS CANNOT BE RUN CONCURRENTLY

struct data_buffer;
struct node;
struct data_buffer* data;
const int trigPin_1 = 2;
const int trigPin_2 =12;
const int echoPin_1 = 4;
const int echoPin_2 = 8; 
const int packet_size = 16;
const byte err_packet[] = {0xEF};
int startByte =0;

//LIFO
struct data_buffer{

     struct node* head;
     struct node* tail;
     int size;
};

struct node{
    struct node* next;
    long r;
};


void  data_buffer(){
      data  = (struct data_buffer*) malloc(sizeof( struct data_buffer));
      data->head=NULL;
      data->tail=NULL;
      int size =0;
  
}

void push(long r){
     struct node* n =(struct node*)malloc(sizeof( struct node));
     n->r = r;
     if(data->size == 0){
        n->next =NULL;
        data->head = n;
        data->tail = n;
        ++(data->size);
     }else{
       n->next = data->head;
       data->head = n;
       ++(data->size);
     }
        
}

long pop(){
    long temp = 0;
    if(data->tail == NULL){
          return temp;
     }else{
         temp = data->tail->r;
     } 

    if(data->size ==1){
          data->tail = data->head = NULL;
          data->size =0; 
    }else{
     struct node* curr =data->head;
        for(int i=0;i<data->size;++i){
           if(curr->next = data->tail){break;}
           if(curr->next =NULL){ return -1;}
              curr = curr->next;
         }
      data->tail = curr;
    }
    return temp;
}

void setup() {
  // initialize serial communication:
  Serial.begin(115200);
  srand(42);
  data_buffer();
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
     byte* packet = (byte*)malloc(packet_size*sizeof(byte));
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
/*      int sigByte = Serial.read();
      if(sigByte == (byte) 0xFF){
        
        delay(500);
        send_packet(rand(),rand(),1);      
      }else{
         send_packet(0,0,1);
      } 
*/
}

void loop()
{
  //push(get_distance(trigPin_1,echoPin_1));     
  //push(get_distance(trigPin_2,echoPin_2));    `
   delay(1500);
   send_packet(rand(),rand(),1);      
}    
     
     
long microsecondsToCentimeters(long microseconds)
{    
      // The speed of sound is 340 m/s or 29 microseconds per centimeter.
      // The ping travels out and back, so to find the distance of the
      // object we take half of the distance travelled.
      return microseconds / 29 / 2;
}    
     
    
    
    
    
