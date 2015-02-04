package brs.com.brs;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.os.Handler;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;
import java.util.ArrayList;

/**
 * Created by jake on 1/17/15.
 */
public class DeviceDetect extends Activity {
    private static final String TAG = "DeviceDetect";

    //USB VARS
    private UsbManager mUsbManager;
    private UsbSerialPort mPort;
    private UsbDeviceConnection mConnection;
    private TextView infoView;
    private int packetSize =10;

    List<long> position_buffer = new List<long>(1024;)


/*Byte signals*/
    private final byte sig_start = (byte) 0xFF;
    private final byte sig_error = (byte) 0xEF;
    private final byte sig_kill  = (byte) 0xEE;


/*-----------MAIN  FN'S--------------*/
    /*
      getPorts()
            -gets manager for driver
            -driver gets list of ports
    */
    protected List<UsbSerialPort> getPorts(){
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> mDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        List<UsbSerialPort> ports = null;

        if(mDrivers.isEmpty()){
            return ports;
        }else{
            sucess_message("Found driver");
        }
        ports = mDrivers.get(0).getPorts();
        return ports;


    }


    /*
    *  connectToDevice()
    *        -opens connection to port
    */
    protected void connectToDevice(UsbSerialPort port){
        UsbDeviceConnection mConnection = mUsbManager.openDevice(port.getDriver().getDevice());
        try{
            port.open(mConnection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        }catch (IOException e){
            failure_message("Couldn't connect through port");
        }



    }


/* writePort()
*       Parameters: byte signal
*       Returns: Void
*       Protocol:
*            -Converts Sig to Byte array
*            -Wites for 0.5 sec
*       Errors
*             -IO exeception
 */
    protected void writePort(byte sig){
        byte[] sig_out = new byte[1];
        sig_out[0] = sig;
        try{
            mPort.write(sig_out,500);
        }catch (IOException e4){
            failure_message("Couldn't write");

            return;
        }

    }



/* readPort()
*      Parameters: N/A
*      Returns: String (for now)
*      Protocol:
*              -Buffers 32 bytes
*              -Reads for 0.2 secs
*              -Return HEX string
*      Errors:
*              -IO exception
*/
    protected String readPort(){
        try {
            byte buffer_in[] = new byte[32];
            int numBytesRead =0;
            int num = mPort.read(buffer_in, 200);
            if(numBytesRead >0){
               String message = "Bytes:" + HexDump.dumpHexString((buffer_in)) + "\n";
               return message;
            }
        } catch (IOException e6) {
                failure_message("Couldn't Read/Write");
                try {
                    mPort.close();
                } catch (IOException e7) {
                    //ignore
                }
        }
         return "nothing";
    }


/*decode()
 *      Parameters: Byte Buffer
 *      Returns: List of longs
 *      Protocol:
 *           -Read through buffer find 0xFF
 *           -Parse, convert, and insert data 
 */
     protected decode(byte[] buffer_in ){
               for(int i =0; i<buffer_in.size; ++i){
                   if(buffer_in[i] == sig_start){
                      break; // find start signal
                   }
                }   
                ++i
                while(i != buffer_in.size && buffer_in[i] != sig_kill) {
                      long temp = long(buffer_in[i]) + long(buffer_in[i+1] >> 4);
                      position_buffer.append(temp);
                      i+=2;
                }         
     }



/*  
*   Runnable for timed communication with arduino
*/
    final Handler readWriteHandle = new Handler();
    final Runnable readWriteRun = new Runnable(){
        @Override
        public void run(){
            infoView.append(readPort(sig_start));                      //adds new hex string to interface
            infoView.setMovementMethod(new ScrollingMovementMethod()); //enables scrolling for interface
            readWriteHandle.postDelayed(this,500);                     // calls same thread in .5 secs

        }
    };






/*--------- TEST INTERFACE ---------*/



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detect);
        infoView =(TextView)findViewById(R.id.infoMessage);
        infoView.setText("Information:\n");
        infoView.setTextSize(20);

        /*Setup Connection
         *   -Find Drivers
         *   -Connect with driver
         *   -Connect to a port
         */

        if(getPorts() != null){
            mPort = getPorts().get(0);
        }else{
            failure_message("Couldn't get a port");
            return;
        }
        connectToDevice(mPort);




    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.




    }
    @Override
    protected void onResume() {
        super.onResume();

        //If no port connect to port
        if (mPort == null) {
            if (getPorts() != null) {
                mPort = getPorts().get(0);
                connectToDevice(mPort);

            } else {
                failure_message("Couldn't get a port");
                return;
            }
        }else{
            readWriteHandle.postDelayed(readWriteRun,0);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        readWriteHandle.removeCallbacks(readWriteRun);
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        readWriteHandle.removeCallbacks(readWriteRun);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
         readWriteHandle.removeCallbacks(readWriteRun);

        // The activity is about to be destroyed.
    }

/*-----------UTIL FN'S-------------*/

    public void failure_message(String warn){
        TextView failureView = (TextView)findViewById(R.id.failureMessage);
        failureView.setText("Failure" + warn);
        failureView.setTextSize(40);
    }

    public void sucess_message(String warn){
        TextView successView = (TextView)findViewById(R.id.sucessMessage);
        successView.append("Sucess" + warn);
        successView.setTextSize(40);
    }



}
