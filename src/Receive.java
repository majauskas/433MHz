import java.util.ArrayList;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.wiringpi.Gpio;

public class Receive
{
  public static int RCSWITCH_MAX_CHANGES = 67;

  public static ArrayList<Integer> timings = new ArrayList();

  public static int nReceivedValue = 0;
  public static int nReceivedBitlength = 0;
  public static int nReceivedDelay = 0;
  public static int nReceivedProtocol = 0;
  public static int nReceiveTolerance = 120;

  public static void main(String[] args) throws InterruptedException
  {
    for (int i = 0; i < RCSWITCH_MAX_CHANGES; i++) {
      timings.add(0);
    }

    GpioController gpio = GpioFactory.getInstance();
    
    
    GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
    myButton.addListener(new GpioPinListener[] { new GpioPinListenerDigital()
    {
      int duration;
      int changeCount = 0;
      int lastTime = 0;
      int repeatCount = 0; 

      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
      {

        String stateName = event.getState().getName();

       
        int time = (int) (System.nanoTime() / 1000);
        duration = time - lastTime;

//        System.err.println(stateName+"\t "+duration);
       
        if (duration > 5000 && duration > (timings.get(0) - 200) && duration < (timings.get(0) + 200) )
        {
          repeatCount++;
          changeCount--;

//          System.err.println(stateName+"\t "+duration+"\t "+timings.get(changeCount)+"\t "+repeatCount+"\t "+changeCount);
          
          if (repeatCount == 2) {
        	  if (receiveProtocol1(changeCount) == false){       		  
//        	        if (receiveProtocol2(changeCount) == false){
//        	          //if (receiveProtocol3(changeCount) == false){}
//        	        }
        	  }

        	  repeatCount = 0;
          }
          changeCount = 0;
        }
        else if (duration > 5000) {
          changeCount = 0;
        }

        if (changeCount >= RCSWITCH_MAX_CHANGES) {
          changeCount = 0;
          repeatCount = 0;
        }
        timings.set(changeCount++, duration);
        lastTime = time;
        
        
        
      }

      private boolean receiveProtocol1(int changeCount){
          
      	  String binCode = "";
          long delay = timings.get(0) / 31;
          long delayTolerance = (long) (delay * nReceiveTolerance * 0.01);    


            for (int i = 1; i<changeCount ; i=i+2) { 
                if (timings.get(i) > delay - delayTolerance && timings.get(i) < delay+delayTolerance && timings.get(i+1) > delay*3-delayTolerance && timings.get(i+1) < delay*3+delayTolerance) {                	
                	binCode += "0";
                } else if (timings.get(i) > delay*3-delayTolerance && timings.get(i) < delay*3+delayTolerance && timings.get(i+1) > delay-delayTolerance && timings.get(i+1) < delay+delayTolerance) {
                	binCode += "1";
                } else {
                  // Failed
                  i = changeCount;
                  binCode = "";
                }
            }

          if (changeCount > 6 && binCode != "") {    // ignore < 4bit values as there are no devices sending 4bit values => noise
        	  
            nReceivedValue = Integer.parseInt(binCode, 2);
            nReceivedBitlength = changeCount / 2;
            nReceivedDelay = (int) delay;
    	    nReceivedProtocol = 1;

            System.out.println();
            System.out.println(nReceivedValue +"\t"+ binCode + "\t"+ nReceivedBitlength);
//            System.out.println("DecValue: " + nReceivedValue);
//            System.out.println("BitLength: " + nReceivedBitlength);
//      	    System.out.println("size: "+timings.size());
//      	    for (int i = 0; i < timings.size(); i++) {
//      	    	System.out.print(timings.get(i)+" ");
//			}
      	    System.out.println();
      	    
          }
         
          
      	if (binCode == ""){
      		return false;
      	}else if (binCode != ""){
      		return true;
      	}
		return false;
      	

      }

      
      
      
//      private boolean receiveProtocol2(int changeCount){
//          
//      	  long code = 0;
//          long delay = timings.get(0) / 10;
//          long delayTolerance = (long) (delay * nReceiveTolerance * 0.01);    
//
//          
//            for (int i = 1; i<changeCount ; i=i+2) {
//            
//                if (timings.get(i) > delay - delayTolerance && timings.get(i) < delay+delayTolerance && timings.get(i+1) > delay*2-delayTolerance && timings.get(i+1) < delay*2+delayTolerance) {
//                  code = code << 1;
//                } else if (timings.get(i) > delay*2-delayTolerance && timings.get(i) < delay*2+delayTolerance && timings.get(i+1) > delay-delayTolerance && timings.get(i+1) < delay+delayTolerance) {
//                  code+=1;
//                  code = code << 1;
//                } else {
//                  // Failed
//                  i = changeCount;
//                  code = 0;
//                }
//            }
//                 
//            code = code >> 1;
//          if (changeCount > 6) {    // ignore < 4bit values as there are no devices sending 4bit values => noise
//            nReceivedValue = code;
//            nReceivedBitlength = changeCount / 2;
//            nReceivedDelay = (int) delay;
//      	    nReceivedProtocol = 2;      	    
//          }
//         
//          
//      	if (code == 0){
//      		return false;
//      	}else if (code != 0){
//      		return true;
//      	}
//		return false;
//      	
//
//      }      
      
      
      
      
      
      
      
      

    }
     });
    
    
    
    System.out.println(" ... complete the GPIO #02 circuit and see the listener feedback here in the console.");
    while (true)
    {
//      if (nReceivedValue != 0)
//      {
//        System.out.println("nReceivedValue: " + nReceivedValue);
//
//        nReceivedValue = 0;
//      }

//      Thread.sleep(300);
    }
  }
}