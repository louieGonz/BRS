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

/**
 * Created by jake on 1/17/15.
 */
public class DeviceDetect extends Activity {
    private static final String TAG = "DeviceDetect";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detect);


        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        try{
            UsbAccessory a[] = manager.getAccessoryList();
            Log.v(TAG,a[0].toString());

        }catch(NullPointerException e){
            Log.v(TAG,"USB not found");
        }

        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        //Find drivers
        if(availableDrivers.isEmpty()){
            failure_message("No availble driver");
            return;
        }
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if(connection==null){
            failure_message("No connection");
            //need permission if fails
            return;
        }

        List<UsbSerialPort> ports = driver.getPorts();
        UsbSerialPort port = ports.get(0);
        try {
            port.open(connection);
        }catch(IOException e){
            failure_message("Cannot open port");
            e.printStackTrace();
        }
        try{
            port.setParameters(115200, 10,100,1);
            byte buffer[] = new byte[16];
            int numBytes = port.read(buffer,1000);
            Log.v(TAG,"Bytes read " + numBytes );
        }catch (IOException e){
         //deal with that
        }finally{
            try {
                port.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
  }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
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
