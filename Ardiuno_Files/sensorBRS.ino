#include <NewPing.h>


#define SONAR_NUM 2
#define MAX_DISTANCE 200 // in cm
#define PING_INTERVAL 33// time between sensor pings in ms -- 29ms is min


unsigned long pintTim[SONAR_NUM];
unsigned int cm[SONAR_NUM]; // stores distance of specific sensor
uint8_t currentSensor = 0; // tracks active sensor

NewPing sonar[SONAR_NUM] = {
	NewPing(triggerPin,echoPin,MAX_DISTANCE);
	NewPing();
};

void setup(){
	Serial.Begin(115200);
	pingTime[0] = millis() + 75; // millis() return time from start of program

	for(uint8_t i =1, i<SONAR_NUM; i++)
		pingTimer[i] = pingTimer[i-1] + PING_INTERVAL;
	
	void loop(){
		for(uint8_t i =0; i < SONAR_NUM; i++){
			if(millis() >= pingTimer[i]){
				pingTimer[i] += PING_INTERVAL * SONAR_NUM;
				if(i==0 && currentSensor == SONAR_NUM - 1) // reach last sensor in array
					oneSensorCycle();
				sonar[currentSensor].timerStop();
				currentSensor=i;
				cm[currentSensor] = 0;
				sonar[currentSensor].ping_timer(echoCheck); 
			}
		}
	}
