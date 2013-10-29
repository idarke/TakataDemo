package com.jdm.broadcast;

import static com.jdm.common.Constants.STATE_CONNECTED;
import static com.jdm.common.Constants.STATE_ERROR;
import static com.jdm.common.Constants.STATE_LOCKOUT;
import static com.jdm.common.Constants.STATE_NOT_CONNECTED;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;

import com.jdm.activity.LockoutActivity;
import com.jdm.activity.MainActivity;
import com.jdm.common.Notifications;
import com.jdm.common.SetupBean;

public class StateReceiver extends BroadcastReceiver
{
   @Override
   public void onReceive(Context arg0, Intent arg1)
   {
      int state = MainActivity.getStateFromService();
      
      if (SetupBean.isShowNotifications()) 
      {
         NotificationManager notificationManager = MainActivity.notificationManager; 
         switch (state)
         {
            case STATE_NOT_CONNECTED:
               notificationManager.notify(0, Notifications.getNotifyNotConnected());
               break;
            case STATE_CONNECTED:
               notificationManager.notify(0, Notifications.getNotifyConnected());
               break;
            case STATE_LOCKOUT:
               //notificationManager.notify(0, Notifications.getNotifyLockout());
               break;
            case STATE_ERROR:
               notificationManager.notify(0, Notifications.getNotifyError());
               break;
            default:
               notificationManager.notify(0, Notifications.getNotifyNothing());
               break;
         }
      }
         
      if (SetupBean.isBeepOnTransition())
      {
         final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
         tg.startTone(ToneGenerator.TONE_PROP_BEEP);
      }

      if (SetupBean.isShowActivity() && state == STATE_LOCKOUT)
      {
         Intent lockoutIntent = new Intent(arg0, LockoutActivity.class);
         MainActivity.thisActivity.startActivity(lockoutIntent);
      }
      
      // Kill any lockoutActivity currently displayed
      if ((state == STATE_NOT_CONNECTED || state == STATE_CONNECTED) && SetupBean.isShowActivity())
      {
         Intent intent = new Intent();
         intent.setAction("com.jdm.FinishLockout");
         arg0.sendBroadcast(intent);
      }

   }

}
