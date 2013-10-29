package com.jdm.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import static com.jdm.common.Constants.*;

import com.jdm.activity.MainActivity;

public class UsbService extends Service
{
   private final String TAG = "UsbService";
   private final IBinder mBinder = new MyBinder();
   private static boolean stateNotConnected = false;
   private static boolean stateConnected = false;
   private static boolean stateLockout = false;
   private static boolean stateError = false;
   
   private static boolean serviceRunning = false;
   
   @Override
   public void onCreate()
   {
      Log.i(TAG, "onCreate");
   } 
   
   @Override
   public void onDestroy()
   {
      Log.i(TAG, "onDestroy");
   }  
   
   @Override
   public int onStartCommand(Intent intent, int flags, int startId)
   {
      return Service.START_NOT_STICKY;
   }
   
   public class MyBinder extends Binder 
   {
      public UsbService getService() 
      {
        return UsbService.this;
      }
   }
   
   @Override
   public IBinder onBind(Intent arg0)
   {
      return mBinder;
   }
   
   public void startUsbListener()
   {
      Thread usbListener = new Thread(new UsbListenerThread(this, MainActivity.ftdi)); 
      usbListener.start();
      serviceRunning = true;
   }
   
   public void stopUsbListener()
   {
      UsbListenerThread.abort = true;
   }
   
   public boolean isStateConnected()
   {
      return stateConnected;
   }
   
  public boolean isStateLockout()
   {
      return stateLockout;
   }
   
   public boolean isStateError()
   {
      return stateError;
   }
   
   public boolean isStateNotConnected()
   {
      return stateNotConnected;
   }
   
   public boolean isNoState()
   {
      if (!stateConnected && !stateLockout && !stateError && !stateNotConnected)  return true;
      return false;
   }
   
   public int getState()
   {
      if (stateNotConnected)  return STATE_NOT_CONNECTED;
      if (stateConnected)  return STATE_CONNECTED;
      if (stateLockout)  return STATE_LOCKOUT;
      if (stateError)  return STATE_ERROR;
      return -1;
   }

   public void setStateNotConnected()
   {
      toggleState(STATE_NOT_CONNECTED);
   }
   
   public void setStateConnected()
   {
      toggleState(STATE_CONNECTED);
   }

   public void setStateLockout()
   {
      toggleState(STATE_LOCKOUT);
   }
   
   public void setStateError()
   {
      toggleState(STATE_ERROR);
   }
   
   public void setNoState()
   {
      toggleState(-1);
   }
   
   /**
    * Turn other states off and the one passed to us on.  If the
    * one passed in is invalid or 0, the result is "No" state.
    * @param state to set
    */
   private void toggleState(int state)
   {
      stateNotConnected = false;
      stateConnected = false;
      stateLockout = false;
      stateError = false;
      switch (state)
      {
         case STATE_NOT_CONNECTED:
            stateNotConnected = true;
            break;
         case STATE_CONNECTED:
            stateConnected = true;
            break;
         case STATE_LOCKOUT:
            stateLockout = true;
            break;
         case STATE_ERROR:
            stateError = true;
            break;
         default:
            break;
      }
      
      Intent intent = new Intent();
      intent.setAction("com.jdm.broadcast.StateReceiver");
      sendBroadcast(intent); 
   }

   public static boolean isServiceRunning()
   {
      return serviceRunning;
   }

   public static void setServiceRunning(boolean serviceRunning)
   {
      UsbService.serviceRunning = serviceRunning;
   }
}
