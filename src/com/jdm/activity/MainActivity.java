package com.jdm.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.jdm.common.Notifications;
import com.jdm.common.SetupBean;
import com.jdm.service.UsbListenerThread;
import com.jdm.service.UsbService;
import com.jdm.takatademo.R;

public class MainActivity extends Activity
{
   private final static String TAG = "MainActivity";
   public static MainActivity thisActivity;
   public static UsbService usbService;
   public static FtdiSerialDriver ftdi;
   public static NotificationManager notificationManager; 
   private UsbManager mUsbManager;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      thisActivity = this;
      
      setContentView(R.layout.activity_main);
      initializeScreenValuesetup();
      
      if (!UsbService.isServiceRunning())
      {
         Notifications.createNotifications(this);
         notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); 
      
         UsbListenerThread.abort = false;
         bindService(new Intent(this, UsbService.class), mConnection, Context.BIND_AUTO_CREATE);
      
         getUsbDeviceIfExists();
      }
   }
   
   /**
    * Initialize the setup options
    */
   private void initializeScreenValuesetup()
   {
      CheckBox checkboxBeeps = (CheckBox) findViewById(R.id.checkBoxBeeps);
      if (SetupBean.isBeepOnTransition()) checkboxBeeps.setChecked(true);
      
      CheckBox checkboxNotifications = (CheckBox) findViewById(R.id.checkBoxNotifications);
      if (SetupBean.isShowNotifications()) checkboxNotifications.setChecked(true);
      
      CheckBox checkboxActivities = (CheckBox) findViewById(R.id.checkBoxLaunchLockoutActivity);
      if (SetupBean.isShowActivity()) checkboxActivities.setChecked(true);
      
      EditText editTextPollingFreq = (EditText) findViewById(R.id.editTextPollingFreq);
      editTextPollingFreq.setText(Integer.toString(SetupBean.getPollingMillis()));
      editTextPollingFreq.setOnEditorActionListener(new TextView.OnEditorActionListener() 
      {
         @Override
         public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
         {
             if (actionId == EditorInfo.IME_ACTION_DONE)
             {
                EditText editTextPollingFreq = (EditText) findViewById(R.id.editTextPollingFreq);
                Editable sfreq = editTextPollingFreq.getText();
                int newFreq = Integer.parseInt(sfreq.toString());
                if (newFreq < 100 || newFreq > 10000)
                {
                   Toast.makeText(getApplicationContext(), "Polling Frequency should be between 100 and 10000 millis", Toast.LENGTH_SHORT).show();
                   editTextPollingFreq.setText(Integer.toString(SetupBean.getPollingMillis()));
                   return false;
                }
                SetupBean.setPollingMillis(newFreq);
             }
             return false;
         }
      });  
      
      TextView textViewDeviceDescription = (TextView) findViewById(R.id.textViewDeviceDescription);
      if (SetupBean.getDeviceDescription().isEmpty())
      {
         textViewDeviceDescription.setText(R.string.no_device_found);
         Button buttonStopService = (Button) findViewById(R.id.buttonToggleService);
         buttonStopService.setVisibility(Button.INVISIBLE);
      }
      else
      {
         textViewDeviceDescription.setText(SetupBean.getDeviceDescription());
      }
     
      
      checkboxNotifications.requestFocus();
   }
   
   public void toggleLockoutActivity(View view)
   {
      SetupBean.setShowActivity(!SetupBean.isShowActivity());
   }
   
   public void toggleNotifications(View view)
   {
      SetupBean.setShowNotifications(!SetupBean.isShowNotifications());
   }
   
   public void toggleBeeps(View view)
   {
      SetupBean.setBeepOnTransition(!SetupBean.isBeepOnTransition());
   }
   
   public void toggleUsbListener(View view)
   {
      if (!UsbListenerThread.abort)
      {
         UsbListenerThread.abort = true;
         UsbService.setServiceRunning(false);
         usbService.stopSelf();
         NotificationManager notificationManager = MainActivity.notificationManager; 
         notificationManager.cancel(0);
         Toast.makeText(getApplicationContext(), "Takata Demo service stopped", Toast.LENGTH_SHORT).show();
      }
   }
   
   private void getUsbDeviceIfExists()
   {
      mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
      HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
      UsbDevice device = null;
      for (String name : deviceList.keySet())
      {
         //TODO Identify device by mProductId or vendorId.  For now use last one on list
         device = deviceList.get(name); 
      }
      
      // Note: there will always be one USB device (the port itself). The
      // second one is the external device we want.
      if (device == null || deviceList.size() == 1)
      {
         return;
      }
      else
      {
         PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
         IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
         registerReceiver(mUsbReceiver, filter);
         mUsbManager.requestPermission(device, mPermissionIntent);
      }
   }
   
   private ServiceConnection mConnection = new ServiceConnection()
   {
      public void onServiceConnected(ComponentName className, IBinder binder)
      {
         usbService = ((UsbService.MyBinder) binder).getService();
      }

      public void onServiceDisconnected(ComponentName className)
      {
         usbService = null;
      }
   };
   
   private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
   private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
   {
      public void onReceive(Context context, Intent intent)
      {
         String action = intent.getAction();
         if (ACTION_USB_PERMISSION.equals(action))
         {
            synchronized (this)
            {
               UsbDevice okdevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

               if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
               {
                  if (okdevice != null)
                  {
                     UsbDeviceConnection conn = mUsbManager.openDevice(okdevice);
                     ftdi = new FtdiSerialDriver(okdevice, conn);
                     usbService.startUsbListener();
                     
                     SetupBean.setDeviceDescription(okdevice.toString());
                     
                     // Once the listener is started, put an initial notification out there so we can get back to the
                     // setup screen, then finish MainActivity
                     NotificationManager notificationManager = MainActivity.notificationManager; 
                     notificationManager.notify(0, Notifications.getNotifyStartup());
                     finish();
                  }
               }
               else
               {
                  Log.d(TAG, "permission denied for device " + okdevice);
               }
               unregisterReceiver(mUsbReceiver);
            }
         }
      }
   };
   
   public static int getStateFromService()
   {
      if (usbService != null)
      {
         try
         {
            return usbService.getState();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
      return -1;
   }

}
