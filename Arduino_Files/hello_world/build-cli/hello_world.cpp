#include <Arduino.h>

void setup()                    // run once, when the sketch starts
{
  Serial.begin(115200);           // set up Serial library at 9600 bps 
}

void loop()                       // run over and over again
{
                                  // do nothing!
  Serial.println("Hello universe!");  // prints hello with ending line break 
}
