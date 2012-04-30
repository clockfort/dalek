/**

Title: Arcade Controls for Media Kiosk Project - Microcontroller Assist Code
Description: Informs arcade computer via USB/serial link of button presses.

@author Chris Lockfort for Computer Science House
@date 2008
**/

const int SAMPLES=16;
int SAMPLE_DELAY=16383; //the max no. of microseconds that can be accurately delayed
// You could use larger variable types instead of bool arrays,
// and performance would likely increase, but the code would be very unreadable.
bool switchState[24];
bool pinState[24];
bool switchHistory[SAMPLES][24]; //let's keep 8 bytes of data history
bool debouncedState[24];
bool oldDebouncedState[24];

void setup()                   
{
  //Set serial baud rate
   Serial.begin(9600);
}

void loop()            
{
  checkSwitches();

  for(int i=0; i<SAMPLES; i++){
    checkSwitches();
    updateSwitchHistory(i);
    delayMicroseconds(SAMPLE_DELAY);
  }

  //Copy old debounced array into debounced/OLD
  for(int i=0; i<24; i++){oldDebouncedState[i]=debouncedState[i];}

  //Reset debounced array
  for(int i=0; i<24; i++){debouncedState[i]=true;}

  //I kinda just made up this way of doing software switch debouncing
  //if everything in a consecutive sample run is true, it's true, else its false
  for(int i=0; i<SAMPLES; i++){
    for(int j=0; j<24; j++)
      if(switchHistory[i][j]!=true)
        debouncedState[j]=false;
  }
  
  /*
  //<debug>
  Serial.print("Debug: ");
  for(int i=0; i<24; i++){
    Serial.print((char)switchHistory[0][i]+1-1);
  }
  Serial.println();
  //</debug>
  */
  
  printSwitchStates();
  delay(50);  //Let's not overwhelm the guy listening at the end of the serial port
}

void checkSwitches(){
     for(int i=2; i<14; i++){digitalWrite(i, LOW);} // make sure the pullup resistors are off
     for(int storageIndex=0,pin=0; storageIndex<25; storageIndex+=2,pin++)
	     pinState[storageIndex] = digitalRead(pin);	//Record states with the pullup resistors off
     for(int i=2; i<14; i++){digitalWrite(i, HIGH);} // Turn on pullup resistors
     for(int storageIndex=1,pin=0; storageIndex<25; storageIndex+=2,pin++)
     	     pinState[storageIndex] = digitalRead(pin); //Record states with the pullup resistor on
     for(int i=0; i<24; i+=2){ //Set proper ON/OFF status for switches according to state combinations
     	   switchState[i]= pinState[i] & pinState[i+1];
	   switchState[i+1]= !(pinState[i] & pinState[i+1]);
     }
}

 void printSwitchStates(){
   byte current=0;

   for(int value=1,i=0; i<8; i++,value*=2){
     if( oldDebouncedState[i]==0 && debouncedState[i]==1 ){current+=i;}
     Serial.print(current, HEX); 
   }
   current=0;
   for(int value=1,i=8; i<16; i++,value*=2){
     if( oldDebouncedState[i]==0 && debouncedState[i]==1 ){current+=i;}
     Serial.print(current, HEX);
   }
   current=0;
   for(int value=1,i=16; i<24; i++,value*=2){
     if( oldDebouncedState[i]==0 && debouncedState[i]==1 ){current+=i;}
     Serial.print(current, HEX);
   }
 }

void updateSwitchHistory(int i){
  for(int j=0; j<24; j++)
    switchHistory[i][j]=switchState[j];
}
