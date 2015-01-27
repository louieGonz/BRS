package brs.com.brs;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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
    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
    private UsbSerialPort mPort;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detect);

        /*Setup Connection
         *   -Find Drivers
         *   -Connect with driver
         *   -Connect to a port
         */

        //Looks for Drivers
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> mDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        if(mDrivers.isEmpty()){
            failure_message("No accessible driver");
            return;
        }else{
            sucess_message("Found driver");
        }

        //Create Connection
        UsbSerialDriver driver = mDrivers.get(0); //get first that shows up
        UsbDeviceConnection connection = mUsbManager.openDevice(driver.getDevice());
        if(connection == null){
            failure_message("Connection failed");
            return;
        }else{
            sucess_message("Connection Worked");
        }

        //Get Port
        UsbSerialPort mPort = driver.getPorts().get(0);
        if(mPort == null){
            failure_message("No Port/ Port not at 0");
            return;
        }else{
            try {
                mPort.open(connection);
                sucess_message("Port is connected!!");
            }catch (IOException e){
                failure_message("Port won't open");
            }
        }


        //Set port parameters
        try {
            mPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        }catch (IOException e){
            failure_message("Couldn't setup");
            try{
                mPort.close();
            }catch (IOException e1){
                //ignore
            }
        }

        //Read from port
        try {
            byte buffer[] = new byte[16];
            int numBytesRead = mPort.read(buffer, 1000);
            String message = "Read " + numBytesRead + " bytes:" +  HexDump.dumpHexString(buffer);
            sucess_message(message);
        }catch (IOException e2){
            failure_message("Couldn't Read");
            try{
                mPort.close();
            }catch (IOException e3){
                //ignore
            }
        }
  }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

        /*
        * Attempt to read from port
        */
/*
        try{
            mPort.setParameters(115200,8,UsbSerialPort.STOPBITS_1,UsbSerialPort.PARITY_NONE);
            byte buffer[] = new byte[16];
            //int numBytesRead = mPort.read(buffer,1000);
            //sucess_message(buffer.toString());
        }catch(IOException e){
            failure_message("Couldn't read");
            return;
        }finally{
            try {
                mPort.close();
            }catch (IOException e){
                failure_message("Couldn't close port");
                return;
            }
        }
*/
    }
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    public void failure_message(String warn){
        TextView textView = new TextView(this);
        textView.setText("Failure" + warn);
        textView.setTextSize(40);
        setContentView(textView);
    }

    public void sucess_message(String warn){
        TextView textView = new TextView(this);
        textView.setText("Sucess" + warn);
        textView.setTextSize(40);
        setContentView(textView);
    }

}
