package com.jdm.service;

import java.io.IOException;

import android.util.Log;

import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.jdm.common.Constants;
import com.jdm.common.SetupBean;

public class UsbListenerThread implements Runnable
{
   private final String TAG = "UsbListenerThread";
   private UsbService usbService;
   public static boolean abort = false;
   private FtdiSerialDriver ftdi;
   private byte buffer[] = new byte[100];
   
   public UsbListenerThread(UsbService usbService, FtdiSerialDriver ftdi)
   {
      this.usbService = usbService;
      this.ftdi = ftdi;
   }

   @Override
   public void run()
   {
      abort = false;

      try
      {
         ftdi.reset();
         ftdi.open();
         ftdi.setBaudRate(9600);
         ftdi.setWriteBufferSize(100);
         ftdi.write(("Takata Demo build " + Constants.VERSION + "\n").getBytes(), 5000);
      }
      catch (IOException e)
      {
         Log.e(TAG,"Exception initializing USB", e);
         return;
      }
      
      for (;;)
      {
         if (abort)  break;
         try
         {
            int received = ftdi.read(buffer, 5000);
            
            if (received > 1)
            {
               byte[] bytes = new byte[received];
               for (int i=0; i<received; i++)  bytes[i] = buffer[i];
               String msg = new String(bytes, "UTF-8");
               
               // If the sender is pumping several messages a second we're likely to have a bunch
               // of them, so split them out and use the last one
               String[] msgs = msg.split("\n");
               if (msgs.length == 0)  continue;
               msg = msgs[msgs.length-1];
               if (SetupBean.isEchoDebugOnSerial())  ftdi.write(("got msg=|" + msg + "|\n").getBytes(), 5000);
               
               if (msg.equalsIgnoreCase(Constants.NOT_CONNECTED_MSG))
               {
                  //if (SetupBean.isEchoDebugOnSerial()) ftdi.write(("is NOT_CONNECTED_MSG\n").getBytes(), 5000);
                  if (!usbService.isStateNotConnected())
                  {
                     usbService.setStateNotConnected();
                  }
               }
               else if (msg.equalsIgnoreCase(Constants.CONNECTED_MSG))
               {
                  //if (SetupBean.isEchoDebugOnSerial())  ftdi.write(("is CONNECTED_MSG\n").getBytes(), 5000);
                  if (!usbService.isStateConnected())
                  {
                     usbService.setStateConnected();
                  }
               }
               else if (msg.equalsIgnoreCase(Constants.LOCKOUT_MSG))
               {
                  //if (SetupBean.isEchoDebugOnSerial())  ftdi.write(("is LOCKOUT_MSG\n").getBytes(), 5000);
                  if (!usbService.isStateLockout())
                  {
                     usbService.setStateLockout();
                  }
               }
               else if (msg.equalsIgnoreCase(Constants.ERROR_MSG))
               {
                  //if (SetupBean.isEchoDebugOnSerial())  ftdi.write(("is ERROR_MSG\n").getBytes(), 5000);
                  if (!usbService.isStateError())
                  {
                     usbService.setStateError();
                  }
               }
               else
               {
                  //if (SetupBean.isEchoDebugOnSerial())  ftdi.write(("is NOT recognized\n").getBytes(), 5000);
                  if (!usbService.isNoState())
                  {
                     usbService.setNoState();
                  }
               }
            }
            Thread.sleep(SetupBean.getPollingMillis());  
         }
         catch (Exception e)
         {
            Log.e(TAG,"Exception reading USB", e);
            break;
         }
      }
   }

}
