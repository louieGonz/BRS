package brs.com.brs;
import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    private int packetSize =10;
    private final byte sig_start = (byte) 0xFF;
    private final byte sig_error = (byte) 0xEF;
    private final byte sig_kill = (byte)  0xEE;

    protected List<UsbSerialPort> get_ports(){
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

    protected void connectToDevice(UsbSerialPort port){
        UsbDeviceConnection mConnection = mUsbManager.openDevice(port.getDriver().getDevice());
        try{
            port.open(mConnection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        }catch (IOException e){
            failure_message("Couldn't connect through port");
        }



    }

    protected void readPort(){
        //Read from port
        try {
            byte buffer_in[] = new byte[packetSize];
            int numBytesRead = mPort.read(buffer_in, 100);

            //Check if buffer has pertinent data
            boolean read_it = false;
            if(buffer_in[0] ==0xFF ){
                read_it =true;
            }else if(buffer_in[0] ==0xEF){
                // run error protocol
            }
            if(read_it){
                String message = "Read " + numBytesRead + " bytes:" + HexDump.dumpHexString(buffer_in);
                sucess_message(message);
            }
        } catch (IOException e2) {
            failure_message("Couldn't Read");
            try {
                mPort.close();
            } catch (IOException e3) {
                //ignore
            }
        }
    }


    protected void writePort(byte sig){
        byte[] sig_out =sig_out = new byte[1];
        sig_out[0] = sig;
        try{
            mPort.write(sig_out,100);
        }catch (IOException e4){
            failure_message("Couldn't write");
            return;
        }

    }



    /*
            Runnable for timed read input
    */
    final Handler timeHandler = new Handler();
    final Runnable timerRunnable = new Runnable(){
        @Override
        public void run(){
            writePort(sig_start);
            readPort();
            timeHandler.postDelayed(this,1000);
        }
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detect);

        /*Setup Connection
         *   -Find Drivers
         *   -Connect with driver
         *   -Connect to a port
         */

        if(get_ports() != null){
            mPort = get_ports().get(0);
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
            if (get_ports() != null) {
                mPort = get_ports().get(0);
                connectToDevice(mPort);

            } else {
                failure_message("Couldn't get a port");
                return;
            }
        }
        timeHandler.postDelayed(timerRunnable,0);

    }


    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        timeHandler.removeCallbacks(timerRunnable);
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
